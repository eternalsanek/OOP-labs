package servlet;

import com.google.gson.Gson;
import dto.UserCreateDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import modelDB.User;
import service.DTOTransformService;
import dao.DAOFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class UserServlet extends HttpServlet {
    private DTOTransformService service;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        service = new DTOTransformService(
                DAOFactory.getUserDAO(),
                DAOFactory.getFunctionDAO(),
                DAOFactory.getPointDAO()
        );
        log.info("UserServlet инициализирован");
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
        String path = req.getPathInfo(); // например: null, "/", "/name/alice", "/a1b2c3d4-..."

        resp.setContentType("application/json");

        // 1. Список всех пользователей
        if (path == null || path.equals("/")) {
            if (!admin) {
                resp.sendError(403, "Только ADMIN может видеть всех пользователей");
                return;
            }
            List<UserDTO> dtos = service.getAllUsers().stream()
                    .map(u -> new UserDTO(u.getId(), u.getName(), u.getRole()))
                    .collect(Collectors.toList());
            resp.getWriter().print(gson.toJson(dtos));
            return;
        }

        if (path.startsWith("/name/")) {
            String name = path.substring("/name/".length());
            log.debug("Запрос пользователя по имени: {}", name);

            Optional<User> user = service.getUserByName(name);
            if (user.isEmpty()) {
                resp.sendError(404, "Пользователь с именем '" + name + "' не найден");
                return;
            }

            if (!admin && !user.get().getId().equals(currentUser.getId())) {
                resp.sendError(403, "Доступ к чужим данным запрещён");
                return;
            }

            UserDTO dto = new UserDTO(user.get().getId(), user.get().getName(), user.get().getRole());
            resp.getWriter().print(gson.toJson(dto));
            log.info("Возвращён пользователь по имени: {}", name);
            return;
        }

        UUID id;
        try {
            id = UUID.fromString(path.substring(1));
        } catch (IllegalArgumentException e) {
            resp.sendError(400, "Неверный формат ID пользователя");
            return;
        }

        Optional<User> user = service.getUserById(id);
        if (user.isEmpty()) {
            resp.sendError(404, "Пользователь с ID не найден");
            return;
        }

        if (!admin && !user.get().getId().equals(currentUser.getId())) {
            resp.sendError(403, "Доступ к чужим данным запрещён");
            return;
        }

        UserDTO dto = new UserDTO(user.get().getId(), user.get().getName(), user.get().getRole());
        resp.getWriter().print(gson.toJson(dto));
        log.info("Возвращён пользователь по ID: {}", id);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;
        if (!isAdmin(currentUser)) {
            resp.sendError(403, "Только ADMIN может создавать пользователей");
            return;
        }

        UserCreateDTO dto = gson.fromJson(req.getReader(), UserCreateDTO.class);
        User created = service.createUser(dto.getName(), dto.getPassword(), dto.getRole());
        resp.setStatus(201);
        resp.getWriter().print(gson.toJson(new UserDTO(created.getId(), created.getName(), created.getRole())));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        UUID id = UUID.fromString(req.getPathInfo().substring(1));
        if (!isAdmin(currentUser) && !currentUser.getId().equals(id)) {
            resp.sendError(403);
            return;
        }

        UserCreateDTO dto = gson.fromJson(req.getReader(), UserCreateDTO.class);
        User updated = service.updateUser(id, dto.getName(), dto.getPassword(), dto.getRole());
        resp.getWriter().print(gson.toJson(new UserDTO(updated.getId(), updated.getName(), updated.getRole())));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;
        if (!isAdmin(currentUser)) {
            resp.sendError(403, "Только ADMIN может удалять пользователей");
            return;
        }

        UUID id = UUID.fromString(req.getPathInfo().substring(1));
        service.deleteUser(id);
        resp.setStatus(204);
    }

    @Override
    public void destroy() {
        service.close();
        log.info("UserServlet уничтожен");
    }
}