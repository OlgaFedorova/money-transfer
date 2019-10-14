package ofedorova;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.application.account.AccountRequestValidator;
import ofedorova.application.account.AccountService;
import ofedorova.application.transfer.TransferRequestValidator;
import ofedorova.application.transfer.TransferService;
import ofedorova.application.user.UserRequestValidator;
import ofedorova.application.user.UserService;
import ofedorova.domain.account.AccountRepository;
import ofedorova.domain.user.UserRepository;
import ofedorova.infrastructure.repository.AccountRepositoryImpl;
import ofedorova.infrastructure.repository.UserRepositoryImpl;
import ofedorova.infrastructure.rest.AccountServlet;
import ofedorova.infrastructure.rest.TransferServlet;
import ofedorova.infrastructure.rest.UserServlet;
import ofedorova.infrastructure.util.ResponseMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;

public class Application {

  private final Map<Class<?>, Object> beans = new HashMap<>();
  private final Server server;

  public Application(int port) {
    this.server = new Server(port);
  }

  private void initCommon() {
    beans.putIfAbsent(ResponseMapper.class, new ResponseMapper());
    beans.putIfAbsent(ServletContextHandler.class, new ServletContextHandler(ServletContextHandler.SESSIONS));
    beans.putIfAbsent(ObjectMapper.class, new ObjectMapper());
    UserRepository userRepository = new UserRepositoryImpl();
    beans.put(UserRepository.class, userRepository);
    AccountRepository accountRepository = new AccountRepositoryImpl((UserRepository) beans.get(UserRepository.class));
    beans.put(AccountRepository.class, accountRepository);
  }

  private void initUserApi() {
    initCommon();

    UserRequestValidator userRequestValidator = new UserRequestValidator();
    UserService userService = new UserService(userRequestValidator, (UserRepository) beans.get(UserRepository.class));
    UserServlet userServlet = new UserServlet(userService, (ResponseMapper) beans.get(ResponseMapper.class), (ObjectMapper) beans.get(ObjectMapper.class));

    ServletContextHandler context = (ServletContextHandler) beans.get(ServletContextHandler.class);
    context.addServlet(new ServletHolder(userServlet), "/users/*");
  }

  private void initAccountApi() {
    initCommon();

    AccountRequestValidator accountRequestValidator = new AccountRequestValidator();
    AccountService accountService = new AccountService(accountRequestValidator, (AccountRepository) beans.get(AccountRepository.class));
    AccountServlet accountServlet = new AccountServlet(accountService, (ResponseMapper) beans.get(ResponseMapper.class), (ObjectMapper) beans.get(ObjectMapper.class));

    ServletContextHandler context = (ServletContextHandler) beans.get(ServletContextHandler.class);
    context.addServlet(new ServletHolder(accountServlet), "/users/accounts/*");
  }

  private void initTransferApi() {
    initCommon();

    TransferRequestValidator transferRequestValidator = new TransferRequestValidator();
    TransferService transferService = new TransferService((AccountRepository) beans.get(AccountRepository.class), transferRequestValidator);
    TransferServlet transferServlet = new TransferServlet(transferService, (ResponseMapper) beans.get(ResponseMapper.class), (ObjectMapper) beans.get(ObjectMapper.class));

    ServletContextHandler context = (ServletContextHandler) beans.get(ServletContextHandler.class);
    context.addServlet(new ServletHolder(transferServlet), "/transfer");
  }

  public void init() {
    initUserApi();
    initAccountApi();
    initTransferApi();
  }

  public void start() throws Exception {
    ServletContextHandler context = (ServletContextHandler) beans.get(ServletContextHandler.class);
    server.setHandler(context);

    server.start();
  }

  public void join() throws InterruptedException {
    server.join();
  }

  public void destroy() {
    server.destroy();
  }
}
