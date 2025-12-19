package servlet;

import com.google.gson.Gson;
import dto.PointCreateDTO;
import dto.PointDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import modelDB.Function;
import modelDB.Point;
import modelDB.User;
import service.DTOTransformService;
import dao.DAOFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class PointServlet extends HttpServlet {
    private DTOTransformService service;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        service = new DTOTransformService(
                DAOFactory.getUserDAO(),
                DAOFactory.getFunctionDAO(),
                DAOFactory.getPointDAO()
        );
        log.info("PointServlet инициализирован");
    }

    private User getCurrentUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            resp.setHeader("WWW-Authenticate", "Basic realm=\"Lab7 API\"");
            resp.setStatus(401);
            log.warn("Доступ без авторизации с IP: {}", req.getRemoteAddr());
            return null;
        }

        String base64 = authHeader.substring("Basic ".length()).trim();
        String credentials = new String(java.util.Base64.getDecoder().decode(base64));
        String[] parts = credentials.split(":", 2);

        if (parts.length != 2) {
            resp.setStatus(401);
            return null;
        }

        String username = parts[0];
        String password = parts[1];

        Optional<User> userOpt = service.getUserByName(username);
        if (userOpt.isEmpty() || !userOpt.get().getPasswordHash().equals(password)) {
            resp.setHeader("WWW-Authenticate", "Basic realm=\"Lab7 API\"");
            resp.setStatus(401);
            log.warn("Неверная авторизация для пользователя: {}", username);
            return null;
        }

        log.info("Успешная авторизация: {}", username);
        return userOpt.get();
    }

    private boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    private boolean canAccessFunction(User currentUser, UUID functionId, HttpServletResponse resp) throws IOException {
        Optional<Function> func = service.getFunctionById(functionId);
        if (func.isEmpty()) {
            resp.sendError(404, "Функция не найдена");
            return false;
        }
        if (!isAdmin(currentUser) && !currentUser.getId().equals(func.get().getOwnerId())) {
            resp.sendError(403, "Доступ к чужим функциям запрещён");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        boolean admin = isAdmin(currentUser);
        String path = req.getPathInfo();
        resp.setContentType("application/json");

        if (path == null || path.equals("/")) {
            if (!admin) {
                resp.sendError(403);
                return;
            }
            List<Point> all = service.getAllUsers().stream()
                    .flatMap(u -> service.getFunctionsByOwner(u.getId()).stream())
                    .flatMap(f -> service.getPointsByFunction(f.getId()).stream())
                    .collect(Collectors.toList());
            List<PointDTO> dtos = all.stream()
                    .map(p -> new PointDTO(p.getId(), p.getFunctionId(), p.getXVal(), p.getYVal()))
                    .collect(Collectors.toList());
            resp.getWriter().print(gson.toJson(dtos));
            return;
        }

        if (path.startsWith("/function/")) {
            UUID functionId = UUID.fromString(path.substring("/function/".length()));
            if (!canAccessFunction(currentUser, functionId, resp)) return;

            List<Point> points = service.getPointsByFunction(functionId);
            List<PointDTO> dtos = points.stream()
                    .map(p -> new PointDTO(p.getId(), p.getFunctionId(), p.getXVal(), p.getYVal()))
                    .collect(Collectors.toList());
            resp.getWriter().print(gson.toJson(dtos));
            return;
        }

        UUID pointId = UUID.fromString(path.substring(1));
        Optional<Point> point = service.getPointById(pointId);
        if (point.isEmpty()) {
            resp.sendError(404);
            return;
        }
        if (!canAccessFunction(currentUser, point.get().getFunctionId(), resp)) return;

        PointDTO dto = new PointDTO(point.get().getId(), point.get().getFunctionId(), point.get().getXVal(), point.get().getYVal());
        resp.getWriter().print(gson.toJson(dto));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        String path = req.getPathInfo();
        if (!path.startsWith("/function/")) {
            resp.sendError(400, "Используйте /function/{id} для создания точки");
            return;
        }

        UUID functionId = UUID.fromString(path.substring("/function/".length()));
        if (!canAccessFunction(currentUser, functionId, resp)) return;

        PointCreateDTO dto = gson.fromJson(req.getReader(), PointCreateDTO.class);
        Point created = service.createPoint(functionId, dto.getXVal(), dto.getYVal());
        resp.setStatus(201);
        resp.getWriter().print(gson.toJson(new PointDTO(
                created.getId(), created.getFunctionId(), created.getXVal(), created.getYVal()
        )));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        UUID pointId = UUID.fromString(req.getPathInfo().substring(1));
        Optional<Point> point = service.getPointById(pointId);
        if (point.isEmpty()) {
            resp.sendError(404);
            return;
        }
        if (!canAccessFunction(currentUser, point.get().getFunctionId(), resp)) return;

        PointCreateDTO dto = gson.fromJson(req.getReader(), PointCreateDTO.class);
        Point updated = service.updatePoint(pointId, dto.getXVal(), dto.getYVal());
        resp.getWriter().print(gson.toJson(new PointDTO(
                updated.getId(), updated.getFunctionId(), updated.getXVal(), updated.getYVal()
        )));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        String path = req.getPathInfo();
        if (path.startsWith("/function/")) {
            UUID functionId = UUID.fromString(path.substring("/function/".length()));
            if (!canAccessFunction(currentUser, functionId, resp)) return;

            service.deletePointsByFunction(functionId);
            resp.setStatus(204);
            return;
        }

        UUID pointId = UUID.fromString(path.substring(1));
        Optional<Point> point = service.getPointById(pointId);
        if (point.isEmpty()) {
            resp.sendError(404);
            return;
        }
        if (!canAccessFunction(currentUser, point.get().getFunctionId(), resp)) return;

        service.deletePoint(pointId);
        resp.setStatus(204);
    }

    @Override
    public void destroy() {
        service.close();
    }
}