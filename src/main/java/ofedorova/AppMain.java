package ofedorova;

public class AppMain {

  public static void main(String[] args) throws Exception {
    Application application = new Application(8080);
    try {
      application.init();
      application.start();
      application.join();
    } finally {
      application.destroy();
    }
  }
}
