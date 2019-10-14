package ofedorova.application.account;

import java.util.UUID;

public class UpdateRequest {

  private UUID accountId;
  private Double amount;

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
