package ofedorova.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.Application;
import ofedorova.application.account.CreateRequest;
import ofedorova.application.account.UpdateRequest;
import ofedorova.domain.account.Account;
import ofedorova.domain.user.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static ofedorova.infrastructure.rest.ServletTest.*;


public class AccountServletTest {

  private static final int PORT = 8091;
  private static final Application APPLICATION = new Application(PORT);
  private static final URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost").setPort(PORT);
  private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static HttpClient client;

  @BeforeClass
  public static void init() throws Exception {
    APPLICATION.init();
    APPLICATION.start();

    client = HttpClients.custom()
        .setConnectionManager(connManager)
        .setConnectionManagerShared(true)
        .build();

  }

  @AfterClass
  public static void destroy() {
    HttpClientUtils.closeQuietly(client);
  }

  @Test
  public void whenCreateAccountErrorCauseRequestNull() throws Exception {
    URI uri = builder.setPath(POST_ACCOUNTS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("null"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Request mustn't be null"));
  }

  @Test
  public void whenCreateAccountErrorCauseUserIdNull() throws Exception {
    URI uri = builder.setPath(POST_ACCOUNTS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"userId\": null, \"title\": \"Account 1\"}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field userId mustn't be null"));
  }

  @Test
  public void whenCreateAccountErrorCauseTitleNull() throws Exception {
    URI uri = builder.setPath(POST_ACCOUNTS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"userId\": \"48d82288-d616-4072-9c58-07f8b91d1917\", \"title\": null}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field title mustn't be null"));
  }

  @Test
  public void whenCreateErrorCauseAccountExist() throws Exception {
    User user = createAndGetUser("user3", client, builder);

    CreateRequest createRequest = new CreateRequest();
    createRequest.setTitle("Account1");
    createRequest.setUserId(user.getId());

    URI uri = builder.setPath(POST_ACCOUNTS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(createRequest)));
    client.execute(request);


    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Entity account with field title = Account1 already exist"));
  }

  @Test
  public void whenCreateSuccess() throws Exception {
    User user = createAndGetUser("user4", client, builder);

    CreateRequest createRequest = new CreateRequest();
    createRequest.setTitle("Account1");
    createRequest.setUserId(user.getId());

    URI uri = builder.setPath(POST_ACCOUNTS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(createRequest)));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_CREATED);
  }

  @Test
  public void whenByIdErrorCauseNotExist() throws Exception {
    UUID uuid = UUID.randomUUID();
    URI uri = builder.setPath(String.format(GET_ACCOUNTS_BY_ID, uuid)).build();
    HttpGet httpGet = new HttpGet(uri);
    HttpResponse response = client.execute(httpGet);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_NOT_FOUND);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Entity account with id = " + uuid + " not exist"));
  }

  @Test
  public void whenCreateAndGetByIdSuccess() throws Exception {
    User user = createAndGetUserWithAccount("user5", "Account1", client, builder);

    Account expected =  user.getAccounts().iterator().next();

    URI uri = builder.setPath(String.format(GET_ACCOUNTS_BY_ID, expected.getId())).build();
    HttpGet httpGet = new HttpGet(uri);
    HttpResponse response = client.execute(httpGet);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK);

    Account account = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Account.class);
    Assert.assertEquals(expected.getTitle(), account.getTitle());
    Assert.assertEquals(expected.getUserId(), account.getUserId());
    Assert.assertEquals(BigDecimal.ZERO, account.getAmount());
    Assert.assertEquals(expected.getId(), account.getId());
  }

  @Test
  public void whenCreateAndGetByUserIdAndTitleErrorCauseNotExist() throws Exception {
    User user = createAndGetUser("user6", client, builder);
    String accountTitle = "Account1";

    URI uri = builder.setPath(String.format(GET_ACCOUNTS_BY_USER_ID_AND_TITLE, user.getId(), accountTitle)).build();
    HttpGet httpGet = new HttpGet(uri);
    HttpResponse response = client.execute(httpGet);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_NOT_FOUND);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Entity account with title = " + accountTitle + " not exist"));
  }

  @Test
  public void whenCreateAndGetByUserIdAndTitleSuccess() throws Exception {
    User user = createAndGetUserWithAccount("user7", "Account1", client, builder);
    Account expected =  user.getAccounts().iterator().next();

    URI uri = builder.setPath(String.format(GET_ACCOUNTS_BY_USER_ID_AND_TITLE, expected.getUserId(), expected.getTitle())).build();
    HttpGet httpGet = new HttpGet(uri);
    HttpResponse response = client.execute(httpGet);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK);

    Account account = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Account.class);
    Assert.assertEquals(expected.getTitle(), account.getTitle());
    Assert.assertEquals(expected.getUserId(), account.getUserId());
    Assert.assertEquals(BigDecimal.ZERO, account.getAmount());
    Assert.assertEquals(expected.getId(), account.getId());
  }

  @Test
  public void whenDepositErrorCauseRequestNull() throws Exception {
    URI uri = builder.setPath(PUT_ACCOUNTS_DEPOSIT).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("null"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Request mustn't be null"));
  }

  @Test
  public void whenDepositErrorCauseAccountIdNull() throws Exception {
    URI uri = builder.setPath(PUT_ACCOUNTS_DEPOSIT).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountId\": null, \"amount\": 1000.56}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field accountId mustn't be null"));
  }

  @Test
  public void whenDepositErrorCauseAmountNull() throws Exception {
    URI uri = builder.setPath(PUT_ACCOUNTS_DEPOSIT).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountId\": \"13cd2f80-2d3c-4fd2-ae0f-200ee3035166\", \"amount\": null}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field amount mustn't be null"));
  }

  @Test
  public void whenWithdrawErrorCauseRequestNull() throws Exception {
    URI uri = builder.setPath(PUT_ACCOUNTS_WITHDRAW).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("null"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Request mustn't be null"));
  }

  @Test
  public void whenWithdrawErrorCauseAccountIdNull() throws Exception {
    URI uri = builder.setPath(PUT_ACCOUNTS_WITHDRAW).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountId\": null, \"amount\": 1000.56}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field accountId mustn't be null"));
  }

  @Test
  public void whenWithdrawErrorCauseAmmountNull() throws Exception {
    URI uri = builder.setPath(PUT_ACCOUNTS_WITHDRAW).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountId\": \"13cd2f80-2d3c-4fd2-ae0f-200ee3035166\", \"amount\": null}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field amount mustn't be null"));
  }

  @Test
  public void whenDepositSuccess() throws Exception {
    User user = createAndGetUserWithAccount("user8", "Account1", client, builder);
    Account account = user.getAccounts().iterator().next();

    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setAccountId(account.getId());
    updateRequest.setAmount(1111.11);

    URI uri = builder.setPath(PUT_ACCOUNTS_DEPOSIT).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(updateRequest)));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK);

    account = getAccountById(account.getId(), client, builder);

    Assert.assertEquals(BigDecimal.valueOf(updateRequest.getAmount()), account.getAmount());
  }

  @Test
  public void whenWithdrawErrorCauseNotEnoughMoney() throws Exception {
    User user = createAndGetUserWithAccount("user9", "Account1", client, builder);
    Account account = user.getAccounts().iterator().next();

    UpdateRequest depositRequest = new UpdateRequest();
    depositRequest.setAccountId(account.getId());
    depositRequest.setAmount(1111.11);

    HttpPut depositPut = new HttpPut(builder.setPath(PUT_ACCOUNTS_DEPOSIT).build());
    depositPut.setHeader("Content-type", "application/json");
    depositPut.setEntity(new StringEntity(objectMapper.writeValueAsString(depositPut)));
    client.execute(depositPut);

    UpdateRequest withdrawRequest = new UpdateRequest();
    withdrawRequest.setAccountId(account.getId());
    withdrawRequest.setAmount(2000.11);

    URI uri = builder.setPath(PUT_ACCOUNTS_WITHDRAW).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(withdrawRequest)));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Not money enough!"));
  }

  @Test
  public void whenWithdrawSuccess() throws Exception {
    User user = createAndGetUserWithAccount("user10", "Account1", client, builder);
    Account account = user.getAccounts().iterator().next();

    UpdateRequest depositRequest = new UpdateRequest();
    depositRequest.setAccountId(account.getId());
    depositRequest.setAmount(1111.11);

    HttpPut depositPut = new HttpPut(builder.setPath(PUT_ACCOUNTS_DEPOSIT).build());
    depositPut.setHeader("Content-type", "application/json");
    depositPut.setEntity(new StringEntity(objectMapper.writeValueAsString(depositRequest)));
    client.execute(depositPut);

    UpdateRequest withdrawRequest = new UpdateRequest();
    withdrawRequest.setAccountId(account.getId());
    withdrawRequest.setAmount(777.11);

    URI uri = builder.setPath(PUT_ACCOUNTS_WITHDRAW).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(withdrawRequest)));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK);

    account = getAccountById(account.getId(), client, builder);

    Assert.assertEquals(BigDecimal.valueOf(depositRequest.getAmount()).subtract(BigDecimal.valueOf(withdrawRequest.getAmount())), account.getAmount());
  }









}
