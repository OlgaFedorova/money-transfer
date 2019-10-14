package ofedorova.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.application.account.AccountService;
import ofedorova.application.account.CreateRequest;
import ofedorova.application.account.UpdateRequest;
import ofedorova.domain.account.Account;
import ofedorova.infrastructure.rest.error.BadRequestException;
import ofedorova.infrastructure.util.ResponseMapper;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountServlet extends HttpServlet {

  private final static String PATH_DEPOSIT = "deposit";
  private final static String PATH_WITHDRAW = "withdraw";
  private final static String USER_ID_PATH = "userId";
  private final static String ACCOUNT_ID_PATH = "accountId";
  private final static String ACCOUNT_TITLE_PATH = "accountTitle";


  private static Logger log = Logger.getLogger(AccountService.class);

  private final AccountService accountService;
  private final ResponseMapper responseMapper;
  private final ObjectMapper objectMapper;

  public AccountServlet(AccountService accountService, ResponseMapper responseMapper, ObjectMapper objectMapper) {
    this.accountService = accountService;
    this.responseMapper = responseMapper;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("Create account");
    try {
      CreateRequest createRequest = objectMapper.readValue(req.getReader(), CreateRequest.class);
      accountService.create(createRequest);
      resp.setStatus(HttpServletResponse.SC_CREATED);
    } catch (Throwable ex) {
      log.error("Error create account", ex);
      responseMapper.mapError(ex, resp);
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    log.info("Update account");
    try {
      Map<String, Object> pathVariables = getPathVariablesForPUT(req);
      UpdateRequest updateRequest = objectMapper.readValue(req.getReader(), UpdateRequest.class);
      if (pathVariables.containsKey(PATH_DEPOSIT)) {
        accountService.deposit(updateRequest);
      }
      if (pathVariables.containsKey(PATH_WITHDRAW)) {
        accountService.withdraw(updateRequest);
      }
    } catch (Throwable ex) {
      log.error("Error update account", ex);
      responseMapper.mapError(ex, resp);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      Map<String, Object> pathVariables = getPathVariablesForGet(req);
      String json = "";

      if (pathVariables.containsKey(ACCOUNT_ID_PATH)) {
        log.info(String.format("Find account by id"));
        Account account = accountService.findById((UUID) pathVariables.get(ACCOUNT_ID_PATH));
        json = objectMapper.writeValueAsString(account);
      }

      if (pathVariables.containsKey(USER_ID_PATH) && pathVariables.containsKey(ACCOUNT_TITLE_PATH)) {
        log.info(String.format("Find account by userId and title"));
        Account account = accountService.findByUserIdAndTitle((UUID) pathVariables.get(USER_ID_PATH), (String) pathVariables.get(ACCOUNT_TITLE_PATH));
        json = objectMapper.writeValueAsString(account);
      }

      resp.getWriter().println(json);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
    } catch (Throwable e) {
      log.error("Error find account", e);
      responseMapper.mapError(e, resp);
    }
  }

  private Map<String, Object> getPathVariablesForPUT(HttpServletRequest req) {
    if (req.getPathInfo() == null) {
      throw new BadRequestException();
    }

    String[] paths = req.getPathInfo().split("/");
    if (paths.length != 2) {
      throw new BadRequestException();
    }

    Map<String, Object> pathVariables = new HashMap<>();

    if (PATH_DEPOSIT.equals(paths[1]) ||
        PATH_WITHDRAW.equals(paths[1])) {
      pathVariables.put(paths[1], Boolean.TRUE);
    } else {
      throw new BadRequestException();
    }
    return pathVariables;
  }

  private Map<String, Object> getPathVariablesForGet(HttpServletRequest req) {
    if (req.getPathInfo() == null) {
      throw new BadRequestException();
    }

    String[] paths = req.getPathInfo().split("/");
    Map<String, Object> pathVariables = new HashMap<>();
    if (paths.length == 2) {
      pathVariables.put(ACCOUNT_ID_PATH, UUID.fromString(paths[1]));
    } else if (paths.length == 3) {
      pathVariables.put(USER_ID_PATH, UUID.fromString(paths[1]));
      pathVariables.put(ACCOUNT_TITLE_PATH, paths[2]);
    } else {
      throw new BadRequestException();
    }
    return pathVariables;
  }
}
