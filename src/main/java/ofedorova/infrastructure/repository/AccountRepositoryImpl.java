package ofedorova.infrastructure.repository;

import ofedorova.application.exception.EntityExistException;
import ofedorova.application.exception.EntityNotExistException;
import ofedorova.domain.account.Account;
import ofedorova.domain.account.AccountRepository;
import ofedorova.domain.user.User;
import ofedorova.domain.user.UserRepository;

import java.util.UUID;

import static ofedorova.infrastructure.datastore.DataStoreInMemory.ACCOUNTS_BY_ID;

public class AccountRepositoryImpl implements AccountRepository {

  private final static String ENTITY_NAME = "account";
  private static final String UNIQUE_ID_KEY = "id";
  private static final String UNIQUE_TITLE_KEY = "title";

  private final UserRepository userRepository;

  public AccountRepositoryImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Account findById(UUID id) {
    if (!ACCOUNTS_BY_ID.containsKey(id)) {
      throw new EntityNotExistException(ENTITY_NAME, UNIQUE_ID_KEY, id.toString());
    }
    return ACCOUNTS_BY_ID.get(id);
  }

  @Override
  public Account findByUserIdAndTitle(UUID userId, String title) {
    User user = userRepository.findById(userId);
    Account account = null;
    for (Account ac : user.getAccounts()) {
      if (ac.getTitle().equals(title)) {
        account = ac;
        break;
      }
    }
    if (account == null) {
      throw new EntityNotExistException(ENTITY_NAME, UNIQUE_TITLE_KEY, title);
    }

    return account;
  }

  @Override
  public void create(Account account) {
    if (ACCOUNTS_BY_ID.containsKey(account.getId())) {
      throw new EntityExistException(ENTITY_NAME, UNIQUE_ID_KEY, account.getId().toString());
    }
    User user = userRepository.findById(account.getUserId());
    if (user.getAccounts().contains(account)) {
      throw new EntityExistException(ENTITY_NAME, UNIQUE_TITLE_KEY, account.getTitle());
    }
    user.getAccounts().add(account);

    ACCOUNTS_BY_ID.putIfAbsent(account.getId(), account);
  }
}
