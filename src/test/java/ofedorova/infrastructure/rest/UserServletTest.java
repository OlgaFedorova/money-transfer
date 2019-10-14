package ofedorova.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ofedorova.Application;
import ofedorova.application.user.CreateRequest;
import ofedorova.domain.user.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import java.net.URI;

public class UserServletTest extends ServletTest {

  private static final int PORT = 8092;
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
  public void whenCreateUserErrorCauseRequestNull() throws Exception {
    URI uri = builder.setPath(POST_USERS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("null"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Request mustn't be null"));
  }

  @Test
  public void whenCreateUserErrorCauseNameNull() throws Exception {
    URI uri = builder.setPath(POST_USERS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity("{\"name\": null}"));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Field name mustn't be null"));
  }

  @Test
  public void whenCreateErrorCauseUserExist() throws Exception {
    CreateRequest createRequest = new CreateRequest();
    createRequest.setName("user1");

    URI uri = builder.setPath(POST_USERS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(createRequest)));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_CREATED);

    HttpResponse nextResponse = client.execute(request);
    Assert.assertTrue(nextResponse.getStatusLine().getStatusCode() == HttpServletResponse.SC_BAD_REQUEST);
    Assert.assertTrue(EntityUtils.toString(nextResponse.getEntity()).contains("Entity user with field name = user1 already exist"));
  }

  @Test
  public void whenCreateAndGetSuccessUseCase() throws Exception {
    CreateRequest createRequest = new CreateRequest();
    createRequest.setName("user2");

    whenCreateSuccess(createRequest);
    whenGetSuccess(createRequest);
  }

  private void whenCreateSuccess(CreateRequest createRequest) throws Exception {
    URI uri = builder.setPath(POST_USERS).build();
    HttpPost request = new HttpPost(uri);
    request.setHeader("Content-type", "application/json");
    request.setEntity(new StringEntity(objectMapper.writeValueAsString(createRequest)));
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_CREATED);
  }

  private void whenGetSuccess(CreateRequest createRequest) throws Exception {
    URI uri = builder.setPath(String.format(GET_USERS, createRequest.getName())).build();
    HttpGet request = new HttpGet(uri);
    HttpResponse response = client.execute(request);
    Assert.assertTrue(response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK);

    User user = objectMapper.readValue(EntityUtils.toString(response.getEntity()), User.class);
    Assert.assertEquals(createRequest.getName(), user.getName());
    Assert.assertTrue(user.getId() != null);
    Assert.assertTrue(user.getAccounts() != null && user.getAccounts().isEmpty());
  }

}
