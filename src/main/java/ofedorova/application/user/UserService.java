package ofedorova.application.user;

import ofedorova.domain.user.User;
import ofedorova.domain.user.UserRepository;

public class UserService {

  private final UserRequestValidator requestValidator;
  private final UserRepository userRepository;

  public UserService(UserRequestValidator requestValidator, UserRepository userRepository) {
    this.requestValidator = requestValidator;
    this.userRepository = userRepository;
  }

  public void create(CreateRequest createRequest) {
    requestValidator.validate(createRequest);
    User user = new User(createRequest.getName());
    userRepository.create(user);
  }

  public User findByName(String name) {
    return userRepository.findByName(name);
  }
}
