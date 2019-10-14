package ofedorova.application.account;

import ofedorova.application.exception.FieldNullException;
import ofedorova.application.exception.RequestNullException;

public class AccountRequestValidator {

  public void validate(CreateRequest request) {
    if (request == null) {
      throw new RequestNullException();
    }
    if (request.getUserId() == null) {
      throw new FieldNullException("userId");
    }
    if (request.getTitle() == null) {
      throw new FieldNullException("title");
    }
  }

  public void validate(UpdateRequest request) {
    if (request == null) {
      throw new RequestNullException();
    }
    if (request.getAccountId() == null) {
      throw new FieldNullException("accountId");
    }
    if (request.getAmount() == null) {
      throw new FieldNullException("amount");
    }
  }
}
