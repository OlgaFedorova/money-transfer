package ofedorova.infrastructure.repository;

import ofedorova.application.exception.EntityExistException;
import ofedorova.application.exception.EntityNotExistException;
import ofedorova.domain.user.User;
import ofedorova.domain.user.UserRepository;

import java.util.UUID;

import static ofedorova.infrastructure.datastore.DataStoreInMemory.USERS_BY_ID;
import static ofedorova.infrastructure.datastore.DataStoreInMemory.USERS_BY_NAME;


public class UserRepositoryImpl implements UserRepository {

  private static final String ENTITY_NAME = "user";
  private static final String UNIQUE_NAME_KEY = "name";
  private static final String UNIQUE_ID_KEY = "id";

  public void create(User user) {
    if (USERS_BY_NAME.containsKey(user.getName())) {
      throw new EntityExistException(ENTITY_NAME, UNIQUE_NAME_KEY, user.getName());
    }
    if (USERS_BY_ID.containsKey(user.getId())) {
      throw new EntityExistException(ENTITY_NAME, UNIQUE_ID_KEY, user.getId().toString());
    }
    USERS_BY_NAME.putIfAbsent(user.getName(), user);
    USERS_BY_ID.putIfAbsent(user.getId(), user);
  }

  @Override
  public User findByName(String name) {
    if (!USERS_BY_NAME.containsKey(name)) {
      throw new EntityNotExistException(ENTITY_NAME, UNIQUE_NAME_KEY, name);
    }
    return USERS_BY_NAME.get(name);
  }

  @Override
  public User findById(UUID id) {
    if (!USERS_BY_ID.containsKey(id)) {
      throw new EntityNotExistException(ENTITY_NAME, UNIQUE_ID_KEY, id.toString());
    }
    return USERS_BY_ID.get(id);
  }
}
