package ofedorova.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.Application;
import ofedorova.application.account.UpdateRequest;
import ofedorova.application.transfer.TransferRequest;
import ofedorova.domain.account.Account;
import ofedorova.domain.user.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

import static ofedorova.infrastructure.rest.ServletTest.PUT_ACCOUNTS_DEPOSIT;
import static ofedorova.infrastructure.rest.ServletTest.PUT_TRANSFER;
import static ofedorova.infrastructure.rest.ServletTest.createAndGetUserWithAccount;
import static ofedorova.infrastructure.rest.ServletTest.getAccountById;

public class TransferServletTest {

  private static final int PORT = 8093;
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
  public void whenTransferErrorCauseRequestNull() throws Exception {
    URI uri = builder.setPath(PUT_TRANSFER).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("null"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Request mustn't be null"));
  }

  @Test
  public void whenTransferErrorCauseAccountIdFromNull() throws Exception {
    URI uri = builder.setPath(PUT_TRANSFER).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountIdFrom\": null, \"accountIdTo\": \"e807fc5e-4546-4cfe-9ea4-7c69aed5c05f\", \"amount\": 333.78}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field accountIdFrom mustn't be null"));
  }

  @Test
  public void whenTransferErrorCauseAccountIdToNull() throws Exception {
    URI uri = builder.setPath(PUT_TRANSFER).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountIdFrom\": \"48d82288-d616-4072-9c58-07f8b91d1917\", \"accountIdTo\": null, \"amount\": 333.78}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field accountIdTo mustn't be null"));
  }

  @Test
  public void whenTransferErrorCauseAmountNull() throws Exception {
    URI uri = builder.setPath(PUT_TRANSFER).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountIdFrom\": \"48d82288-d616-4072-9c58-07f8b91d1917\", \"accountIdTo\": \"e807fc5e-4546-4cfe-9ea4-7c69aed5c05f\", \"amount\": null}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field amount mustn't be null"));
  }

  @Test
  public void whenTransferErrorCauseFirstAccountNotExistNull() throws Exception {
    URI uri = builder.setPath(PUT_TRANSFER).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountIdFrom\": \"48d82288-d616-4072-9c58-07f8b91d1917\", \"accountIdTo\": \"e807fc5e-4546-4cfe-9ea4-7c69aed5c05f\", \"amount\": 333.78}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_NOT_FOUND);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Entity account with id = 48d82288-d616-4072-9c58-07f8b91d1917 not exist"));
  }

  @Test
  public void whenTransferErrorCauseSecondAccountNotExistNull() throws Exception {
    User user = createAndGetUserWithAccount("user11", "account1", client, builder);
    Account account = user.getAccounts().iterator().next();

    URI uri = builder.setPath(PUT_TRANSFER).build();
    HttpPut request = new HttpPut(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"accountIdFrom\": \"" + account.getId() + "\", \"accountIdTo\": \"e807fc5e-4546-4cfe-9ea4-7c69aed5c05f\", \"amount\": 333.78}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_NOT_FOUND);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Entity account with id = e807fc5e-4546-4cfe-9ea4-7c69aed5c05f not exist"));
  }

  @Test
  public void whenTransferErrorCauseMoneyNoyEnoughNull() throws Exception {
    User userFrom = createAndGetUserWithAccount("user12", "account1", client, builder);
    Account accountFrom = userFrom.getAccounts().iterator().next();

    User userTo = createAndGetUserWithAccount("user13", "account1", client, builder);
    Account accountTo = userTo.getAccounts().iterator().next();


    UpdateRequest updateRequest = new UpdateRequest();
    updateRequest.setAccountId(accountFrom.getId());
    updateRequest.setAmount(1111.11);

    HttpPut httpDeposit = new HttpPut(builder.setPath(PUT_ACCOUNTS_DEPOSIT).build());
    httpDeposit.setHeader("Content-type", "application/json");
    httpDeposit.setEntity(new StringEntity(objectMapper.writeValueAsString(updateRequest)));
    client.execute(httpDeposit);

    TransferRequest transferRequest = new TransferRequest();
    transferRequest.setAccountIdFrom(accountFrom.getId());
    transferRequest.setAccountIdTo(accountTo.getId());
    transferRequest.setAmount(333.78);

    HttpPut httpTransfer = new HttpPut(builder.setPath(PUT_TRANSFER).build());
    httpTransfer.setHeader("Content-type", "application/json");
    httpTransfer.setEntity(new StringEntity(objectMapper.writeValueAsString(transferRequest)));
    HttpResponse response = client.execute(httpTransfer);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK);

    accountFrom = getAccountById(accountFrom.getId(), client, builder);
    accountTo = getAccountById(accountTo.getId(), client, builder);

    Assert.assertEquals(BigDecimal.valueOf(updateRequest.getAmount()).subtract(BigDecimal.valueOf(transferRequest.getAmount())), accountFrom.getAmount());
    Assert.assertEquals(BigDecimal.valueOf(transferRequest.getAmount()), accountTo.getAmount());
  }
}
