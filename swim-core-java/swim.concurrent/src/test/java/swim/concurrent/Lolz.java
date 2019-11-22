package swim.concurrent;

import java.lang.reflect.InvocationTargetException;

public class Lolz {

  public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InterruptedException {
    for (int i = 0; i < 100; i++) {
      System.out.println("-------------------------------------");
      System.out.println("Run: " + (i + 1));

      TestTheaterSpec testTheaterSpec = new TestTheaterSpec();
      testTheaterSpec.awaitSyncContTimeout();
//      for (Method m : syncSpec.getClass().getDeclaredMethods()) {
//        m.invoke(syncSpec);
//      }
    }
  }

}
