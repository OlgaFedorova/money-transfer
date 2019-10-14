package ofedorova.domain.account.exception;

import ofedorova.domain.exception.ValidationException;

public class NotMoneyEnoughException extends ValidationException {

  public NotMoneyEnoughException() {
    super("Not money enough!");
  }
}
