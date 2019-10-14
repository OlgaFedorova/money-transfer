package ofedorova.domain.account;

import java.util.UUID;

public interface AccountRepository {

  Account findById(UUID id);

  Account findByUserIdAndTitle(UUID userId, String title);

  void create(Account account);
}
