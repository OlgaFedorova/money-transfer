package ofedorova.infrastructure.util;

import ofedorova.application.exception.EntityNotExistException;
import ofedorova.domain.exception.ValidationException;
import ofedorova.infrastructure.rest.error.BadRequestException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseMapper {

  public void mapError(Throwable ex, HttpServletResponse resp) throws IOException {

    if (ex instanceof EntityNotExistException) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else if (ex instanceof ValidationException) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else if (ex instanceof BadRequestException) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    resp.getWriter().println(ex.getMessage());
  }

}
