package ofedorova.application.exception;

    import ofedorova.domain.exception.ValidationException;

public class RequestNullException extends ValidationException {

  public RequestNullException() {
    super(String.format("Request mustn't be null"));
  }
}
