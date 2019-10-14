package ofedorova.application.transfer;

import java.util.UUID;

public class TransferRequest {

  private UUID accountIdFrom;
  private UUID accountIdTo;
  private Double amount;

  public UUID getAccountIdFrom() {
    return accountIdFrom;
  }

  public void setAccountIdFrom(UUID accountIdFrom) {
    this.accountIdFrom = accountIdFrom;
  }

  public UUID getAccountIdTo() {
    return accountIdTo;
  }

  public void setAccountIdTo(UUID accountIdTo) {
    this.accountIdTo = accountIdTo;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
