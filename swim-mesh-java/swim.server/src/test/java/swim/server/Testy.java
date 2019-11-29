package swim.server;

import java.lang.reflect.InvocationTargetException;

public class Testy {

  public static void main(String[] args) throws InterruptedException, InvocationTargetException, IllegalAccessException {
    PingPongSpec ping = new PingPongSpec();
    for (int i = 0; i < 10000; i++) {
      System.out.println("Test: " + (i + 1));
      ping.testCommandPingPong();
      System.out.println("------------------------------------------------");
    }
  }

}
