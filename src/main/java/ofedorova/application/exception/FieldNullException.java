package ofedorova.application.exception;

import ofedorova.domain.exception.ValidationException;

public class FieldNullException extends ValidationException {

  public FieldNullException(String field) {
    super(String.format("Field %s mustn't be null", field));
  }
}
