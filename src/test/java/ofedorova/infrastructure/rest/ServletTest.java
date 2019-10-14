package ofedorova.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.application.account.CreateRequest;
import ofedorova.domain.account.Account;
import ofedorova.domain.user.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.UUID;

public class ServletTest {

  protected static final String POST_USERS = "/users";
  protected static final String GET_USERS = "/users/%s";
  protected static final String POST_ACCOUNTS = "/users/accounts";
  protected static final String GET_ACCOUNTS_BY_ID = "/users/accounts/%s";
  protected static final String GET_ACCOUNTS_BY_USER_ID_AND_TITLE = "/users/accounts/%s/%s";
  protected static final String PUT_ACCOUNTS_DEPOSIT = "/users/accounts/deposit";
  protected static final String PUT_ACCOUNTS_WITHDRAW = "/users/accounts/withdraw";
  protected static final String PUT_TRANSFER = "/transfer";

  private static final ObjectMapper objectMapper = new ObjectMapper();


  protected static User createAndGetUser(String userName, HttpClient client, URIBuilder builder) throws Exception {
    ofedorova.application.user.CreateRequest createUserRequest = new ofedorova.application.user.CreateRequest();
    createUserRequest.setName(userName);

    URI uriCreateUser = builder.setPath(POST_USERS).build();
    HttpPost requestUserCreate = new HttpPost(uriCreateUser);
    requestUserCreate.setHeader("Content-type", "application/json");
    requestUserCreate.setEntity(new StringEntity(objectMapper.writeValueAsString(createUserRequest)));
    client.execute(requestUserCreate);

    URI uriGetUser = builder.setPath(String.format(GET_USERS, createUserRequest.getName())).build();
    HttpResponse responseUser = client.execute(new HttpGet(uriGetUser));

    User user = objectMapper.readValue(EntityUtils.toString(responseUser.getEntity()), User.class);
    return user;
  }

  protected static User createAndGetUserWithAccount(String userName, String accountTitle, HttpClient client, URIBuilder builder) throws Exception {
    User user = createAndGetUser(userName, client, builder);

    CreateRequest createRequest = new CreateRequest();
    createRequest.setTitle(accountTitle);
    createRequest.setUserId(user.getId());

    URI uri = builder.setPath(POST_ACCOUNTS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(createRequest)));
    client.execute(request);

    return getUserByName(userName, client, builder);
  }

  protected static Account getAccountById(UUID id, HttpClient client, URIBuilder builder) throws Exception {
    HttpResponse responseGetAccount = client.execute(new HttpGet(builder.setPath(String.format(GET_ACCOUNTS_BY_ID, id)).build()));
    return objectMapper.readValue(EntityUtils.toString(responseGetAccount.getEntity()), Account.class);
  }

  private static User getUserByName(String name, HttpClient client, URIBuilder builder) throws Exception {
    HttpResponse responseGetUser = client.execute(new HttpGet(builder.setPath(String.format(GET_USERS, name)).build()));
    return objectMapper.readValue(EntityUtils.toString(responseGetUser.getEntity()), User.class);
  }
}
