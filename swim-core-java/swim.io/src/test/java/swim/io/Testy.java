package swim.io;

import java.lang.reflect.InvocationTargetException;

public class Testy {

  public static void main(String[] args) throws InterruptedException, InvocationTargetException, IllegalAccessException {
    for (int i = 0; i < 10000; i++) {
      System.out.println("Test: " + (i + 1));
      TcpModemSpec tcpModemSpec = new TcpModemSpec();
      tcpModemSpec.testTransmitMultipleLines();

      System.out.println("------------------------------------------------");
    }
  }

}
