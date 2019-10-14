package ofedorova.application.exception;

import ofedorova.domain.exception.ValidationException;

public class EntityNotExistException extends ValidationException {

  public EntityNotExistException(String entity, String field, String value) {
    super(String.format("Entity %s with %s = %s not exist ", entity, field, value));
  }
}
