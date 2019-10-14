package ofedorova.application.transfer;

import java.util.UUID;

public class TransferRequest {

  private UUID accountIdFrom;
  private UUID accountIdTo;
  private Double amount;

  public TransferRequest(UUID accountIdFrom, UUID accountIdTo, double amount) {
    this.accountIdFrom = accountIdFrom;
    this.accountIdTo = accountIdTo;
    this.amount = amount;
  }

  public UUID getAccountIdFrom() {
    return accountIdFrom;
  }

  public UUID getAccountIdTo() {
    return accountIdTo;
  }

  public Double getAmount() {
    return amount;
  }
}
