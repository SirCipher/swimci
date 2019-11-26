package swim.io.http;

public class Testy {

  public static void main(String[] args) {
    for (int i = 0; i < 1000; i++) {
      System.out.println(i + 1);
      SecureHttpSocketSpec httpSocketDisabled = new SecureHttpSocketSpec();
      httpSocketDisabled.testRequestResponse();
    }
  }

}
