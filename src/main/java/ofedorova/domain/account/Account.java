package ofedorova.domain.account;

import ofedorova.domain.account.exception.NotMoneyEnoughException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class Account {
  private UUID id;
  private UUID userId;
  private String title;
  private AtomicReference<BigDecimal> amount;

  public Account() {
  }

  public Account(UUID userId, String title) {
    this.id = UUID.randomUUID();
    this.userId = userId;
    this.title = title;
    this.amount = new AtomicReference<>(BigDecimal.valueOf(0));
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getTitle() {
    return title;
  }

  public BigDecimal getAmount() {
    return amount.get();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Account account = (Account) o;
    return userId.equals(account.userId) &&
        title.equals(account.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, title);
  }

  public void deposit(double sum) {
    BigDecimal currentAmount;
    BigDecimal newAmount;
    do {
      currentAmount = amount.get();
      newAmount = currentAmount.add(BigDecimal.valueOf(sum));
    } while (!amount.compareAndSet(currentAmount, newAmount));
  }

  public void withdraw(double sum) {
    BigDecimal currentAmount;
    BigDecimal newAmount;
    do {
      currentAmount = amount.get();
      newAmount = currentAmount.subtract(BigDecimal.valueOf(sum));
      if (newAmount.compareTo(BigDecimal.ZERO) == -1) {
        throw new NotMoneyEnoughException();
      }
    } while (!amount.compareAndSet(currentAmount, newAmount));
  }

  public void transfer(Account account, double sum) {
    withdraw(sum);
    account.deposit(sum);
  }
}
