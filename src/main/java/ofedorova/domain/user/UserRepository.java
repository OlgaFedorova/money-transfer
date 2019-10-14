package ofedorova.domain.user;

import java.util.UUID;

public interface UserRepository {

  void create(User user);

  User findByName(String name);

  User findById(UUID id);
}
