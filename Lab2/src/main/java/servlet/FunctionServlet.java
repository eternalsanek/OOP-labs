package servlet;

import com.google.gson.Gson;
import dto.FunctionCreateDTO;
import dto.FunctionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import modelDB.Function;
import modelDB.User;
import service.DTOTransformService;
import dao.DAOFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class FunctionServlet extends HttpServlet {
    private DTOTransformService service;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        service = new DTOTransformService(
                DAOFactory.getUserDAO(),
                DAOFactory.getFunctionDAO(),
                DAOFactory.getPointDAO()
        );
        log.info("FunctionServlet инициализирован");
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        boolean admin = isAdmin(currentUser);
        String path = req.getPathInfo();
        resp.setContentType("application/json");

        if (path == null || path.equals("/")) {
            if (!admin) {
                resp.sendError(403, "Только ADMIN может видеть все функции");
                return;
            }
            List<Function> all = service.getAllUsers().stream()
                    .flatMap(u -> service.getFunctionsByOwner(u.getId()).stream())
                    .collect(Collectors.toList());
            List<FunctionDTO> dtos = all.stream()
                    .map(f -> new FunctionDTO(f.getId(), f.getOwnerId(), f.getName(), f.getType(), f.getExpression()))
                    .collect(Collectors.toList());
            resp.getWriter().print(gson.toJson(dtos));
            return;
        }

        if (path.startsWith("/owner/")) {
            UUID ownerId = UUID.fromString(path.substring("/owner/".length()));
            if (!admin && !currentUser.getId().equals(ownerId)) {
                resp.sendError(403, "Доступ к чужим функциям запрещён");
                return;
            }
            List<Function> functions = service.getFunctionsByOwner(ownerId);
            List<FunctionDTO> dtos = functions.stream()
                    .map(f -> new FunctionDTO(f.getId(), f.getOwnerId(), f.getName(), f.getType(), f.getExpression()))
                    .collect(Collectors.toList());
            resp.getWriter().print(gson.toJson(dtos));
            return;
        }

        UUID funcId = UUID.fromString(path.substring(1));
        Optional<Function> function = service.getFunctionById(funcId);
        if (function.isEmpty()) {
            resp.sendError(404);
            return;
        }
        if (!admin && !currentUser.getId().equals(function.get().getOwnerId())) {
            resp.sendError(403);
            return;
        }
        FunctionDTO dto = new FunctionDTO(
                function.get().getId(),
                function.get().getOwnerId(),
                function.get().getName(),
                function.get().getType(),
                function.get().getExpression()
        );
        resp.getWriter().print(gson.toJson(dto));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Начинаем обработку POST /functions");
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) {
            log.warn("Не авторизован");
            return;
        }
        log.info("Пользователь авторизован");

        try {
            FunctionCreateDTO dto = gson.fromJson(req.getReader(), FunctionCreateDTO.class);
            log.info("Получен DTO: {}", dto);

            if (!isAdmin(currentUser) && !currentUser.getId().equals(dto.getOwnerId())) {
                resp.sendError(403, "Можно создавать функции только от своего имени");
                return;
            }

            Function created = service.createFunction(dto.getOwnerId(), dto.getName(), dto.getType(), dto.getExpression());
            log.info("Создана функция: {}", created);

            resp.setStatus(201);
            resp.getWriter().print(gson.toJson(new FunctionDTO(
                    created.getId(), created.getOwnerId(), created.getName(), created.getType(), created.getExpression()
            )));
        } catch (Exception e) {
            log.error("Ошибка при создании функции", e);
            resp.sendError(500, "Ошибка при обработке запроса");
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        UUID funcId = UUID.fromString(req.getPathInfo().substring(1));
        Optional<Function> function = service.getFunctionById(funcId);
        if (function.isEmpty()) {
            resp.sendError(404);
            return;
        }
        if (!isAdmin(currentUser) && !currentUser.getId().equals(function.get().getOwnerId())) {
            resp.sendError(403);
            return;
        }

        FunctionCreateDTO dto = gson.fromJson(req.getReader(), FunctionCreateDTO.class);
        Function updated = service.updateFunction(funcId, dto.getName(), dto.getType(), dto.getExpression());
        resp.getWriter().print(gson.toJson(new FunctionDTO(
                updated.getId(), updated.getOwnerId(), updated.getName(), updated.getType(), updated.getExpression()
        )));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        UUID funcId = UUID.fromString(req.getPathInfo().substring(1));
        Optional<Function> function = service.getFunctionById(funcId);
        if (function.isEmpty()) {
            resp.sendError(404);
            return;
        }
        if (!isAdmin(currentUser) && !currentUser.getId().equals(function.get().getOwnerId())) {
            resp.sendError(403);
            return;
        }

        service.deleteFunction(funcId);
        resp.setStatus(204);
    }

    @Override
    public void destroy() {
        service.close();
    }
}