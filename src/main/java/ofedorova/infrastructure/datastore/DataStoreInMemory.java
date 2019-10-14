package ofedorova.infrastructure.datastore;

import ofedorova.domain.account.Account;
import ofedorova.domain.user.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataStoreInMemory {

  public static final Map<UUID, Account> ACCOUNTS_BY_ID = new ConcurrentHashMap<>();

  public static final Map<String, User> USERS_BY_NAME = new ConcurrentHashMap<>();
  public static final Map<UUID, User> USERS_BY_ID = new ConcurrentHashMap<>();
}
