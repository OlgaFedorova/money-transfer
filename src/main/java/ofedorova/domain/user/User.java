package ofedorova.domain.user;

import ofedorova.domain.account.Account;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class User {

  private UUID id;
  private String name;
  private Set<Account> accounts = new CopyOnWriteArraySet<>();

  public User() {}

  public User(String name) {
    this.id = UUID.randomUUID();
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Set<Account> getAccounts() {
    return accounts;
  }
}
