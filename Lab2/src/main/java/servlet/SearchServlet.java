package servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import modelDB.Function;
import modelDB.User;
import search.SearchService;
import service.DTOTransformService;
import dao.DAOFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SearchServlet extends HttpServlet {
    private SearchService searchService;
    private DTOTransformService dtoService;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        dtoService = new DTOTransformService(
                DAOFactory.getUserDAO(),
                DAOFactory.getFunctionDAO(),
                DAOFactory.getPointDAO()
        );
        searchService = new SearchService(dtoService);
        log.info("SearchServlet инициализирован");
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

        Optional<User> userOpt = dtoService.getUserByName(username);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req, resp);
        if (currentUser == null) return;

        boolean admin = isAdmin(currentUser);
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        resp.setContentType("application/json");

        @SuppressWarnings("unchecked")
        Map<String, Object> body = gson.fromJson(req.getReader(), HashMap.class);
        String field = (String) body.getOrDefault("field", "name");
        String value = (String) body.getOrDefault("value", "");
        String algorithm = (String) body.getOrDefault("algorithm", "linear");

        if (path.equals("/users")) {
            if (!admin) {
                resp.sendError(403);
                return;
            }
            List<User> results = searchService.findAll(User.class, field, value, algorithm);
            resp.getWriter().print(gson.toJson(results));
            return;
        }

        if (path.equals("/functions")) {
            List<Function> results = searchService.findAll(Function.class, field, value, algorithm);
            if (!admin) {
                results = results.stream()
                        .filter(f -> f.getOwnerId().equals(currentUser.getId()))
                        .toList();
            }
            resp.getWriter().print(gson.toJson(results));
            return;
        }

        // Другие пути (DFS/BFS и т.д.)
        if (!admin) {
            resp.sendError(403);
            return;
        }
        resp.getWriter().print(gson.toJson("Расширенный поиск доступен только ADMIN"));
    }

    @Override
    public void destroy() {
        dtoService.close();
    }
}