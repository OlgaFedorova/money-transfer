package ofedorova.application.exception;

import ofedorova.domain.exception.ValidationException;

public class EntityExistException extends ValidationException {

  public EntityExistException(String entity, String field, String value) {
    super(String.format("Entity %s with field %s = %s already exist", entity, field, value));
  }
}
