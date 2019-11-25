package swim.io.http;

public class Testy {

  public static void main(String[] args) {
    HttpSocketSpec spec = new HttpSocketSpec();

    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      System.out.println(i);

      spec.testChunkedRequestResponse();
      spec.testPipelinedRequestResponse();
      spec.testRequestResponse();
    }
  }

}
