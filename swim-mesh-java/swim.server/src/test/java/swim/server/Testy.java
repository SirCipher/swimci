package swim.server;

import java.lang.reflect.InvocationTargetException;

public class Testy {

  public static void main(String[] args) throws InterruptedException, InvocationTargetException, IllegalAccessException {
    for (int i = 0; i < 10000; i++) {
      System.out.println("Test: " + (i + 1));
      ListDownlinkSpec listDownlinkSpec = new ListDownlinkSpec();
      try {
        listDownlinkSpec.testInsert();
      } catch (Exception | Error e){
        e.printStackTrace();
      }
      System.out.println("------------------------------------------------");
    }
  }

}
