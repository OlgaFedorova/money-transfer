package ofedorova.application.account;

import java.util.UUID;

public class CreateRequest {

  private UUID userId;
  private String title;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
