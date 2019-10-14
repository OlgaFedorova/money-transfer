package ofedorova.application.transfer;

import ofedorova.application.exception.FieldNullException;

class TransferRequestValidator {

  public void validate(TransferRequest transferRequest) {
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
