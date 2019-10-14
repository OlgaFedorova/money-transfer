package ofedorova.application.transfer;

import ofedorova.application.exception.FieldNullException;
import ofedorova.application.exception.RequestNullException;

public class TransferRequestValidator {

  public void validate(TransferRequest transferRequest) {
    if (transferRequest == null) {
      throw new RequestNullException();
    }

    if (transferRequest.getAccountIdFrom() == null) {
      throw new FieldNullException("accountIdFrom");
    }

    if (transferRequest.getAccountIdTo() == null) {
      throw new FieldNullException("accountIdTo");
    }

    if (transferRequest.getAmount() == null) {
      throw new FieldNullException("amount");
    }
  }

}
