package ofedorova.infrastructure.rest.error;

public class BadRequestException extends RuntimeException {

  public BadRequestException() {
    super("The request not supported");
  }
}
