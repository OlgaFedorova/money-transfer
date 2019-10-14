package ofedorova.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.application.user.CreateRequest;
import ofedorova.application.user.UserService;
import ofedorova.domain.user.User;
import ofedorova.infrastructure.rest.error.BadRequestException;
import ofedorova.infrastructure.util.ResponseMapper;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserServlet extends HttpServlet {

  private static Logger log = Logger.getLogger(UserService.class);

  private final static String USER_NAME_PATH = "userName";

  private final UserService userService;
  private final ResponseMapper responseMapper;
  private final ObjectMapper objectMapper;

  public UserServlet(UserService userService, ResponseMapper responseMapper, ObjectMapper objectMapper) {
    this.userService = userService;
    this.responseMapper = responseMapper;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      Map<String, Object> pathVariables = getPathVariablesForGet(req);
      String json = "";
      if (pathVariables.containsKey(USER_NAME_PATH)) {
        log.info(String.format("Find user by name"));
        User user = userService.findByName((String) pathVariables.get(USER_NAME_PATH));
        json = objectMapper.writeValueAsString(user);
      }
      resp.getWriter().println(json);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
    } catch (Throwable e) {
      log.error("Error find user", e);
      responseMapper.mapError(e, resp);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info(String.format("Create user"));
    try {
      CreateRequest createRequest = objectMapper.readValue(req.getReader(), CreateRequest.class);
      userService.create(createRequest);
      resp.setStatus(HttpServletResponse.SC_CREATED);
    } catch (Throwable e) {
      log.error("Error create user", e);
      responseMapper.mapError(e, resp);
    }
  }

  private Map<String, Object> getPathVariablesForGet(HttpServletRequest req) {
    if (req.getPathInfo() == null) {
      throw new BadRequestException();
    }

    String[] paths = req.getPathInfo().split("/");
    Map<String, Object> pathVariables = new HashMap<>();
    if (paths.length == 2) {
      pathVariables.put(USER_NAME_PATH, paths[1]);
    } else {
      throw new BadRequestException();
    }
    return pathVariables;
  }
}
