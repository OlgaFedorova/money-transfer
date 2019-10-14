package ofedorova.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.application.transfer.TransferRequest;
import ofedorova.application.transfer.TransferService;
import ofedorova.infrastructure.util.ResponseMapper;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TransferServlet extends HttpServlet {

  private static Logger log = Logger.getLogger(TransferServlet.class);

  private final TransferService transferService;
  private final ResponseMapper responseMapper;
  private final ObjectMapper objectMapper;

  public TransferServlet(TransferService transferService, ResponseMapper responseMapper, ObjectMapper objectMapper) {
    this.transferService = transferService;
    this.responseMapper = responseMapper;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    log.info("Transfer money");
    try {
      TransferRequest transferRequest = objectMapper.readValue(req.getReader(), TransferRequest.class);
      transferService.transfer(transferRequest);
    } catch (Throwable ex) {
      log.error("Error transfer money", ex);
      responseMapper.mapError(ex, resp);
    }
  }
}
