package ofedorova.application.user;

import ofedorova.application.exception.FieldNullException;
import ofedorova.application.exception.RequestNullException;

public class UserRequestValidator {

  public void validate(CreateRequest request) {
    if (request == null) {
      throw new RequestNullException();
    }

    if (request.getName() == null) {
      throw new FieldNullException("name");
    }
  }
}
