package swim.io;

import java.lang.reflect.InvocationTargetException;

public class Testy {

  public static void main(String[] args) throws InterruptedException, InvocationTargetException, IllegalAccessException {
    TcpModemSpec tcpModemSpec = new TcpModemSpec();
    for (int i = 0; i < 100; i++) {
      System.out.println("Test: " + (i + 1));
      tcpModemSpec.testTransmitMultipleLines();

      System.out.println("------------------------------------------------");
    }
  }

}
