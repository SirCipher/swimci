package swim.concurrent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Lolz {

  public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, InterruptedException {
    for (int i = 0; i < 100; i++) {
      System.out.println("-------------------------------------");
      System.out.println("Run: " + (i + 1));

      SyncSpec syncSpec = new SyncSpec();
      syncSpec.awaitTimeout();
//      for (Method m : syncSpec.getClass().getDeclaredMethods()) {
//        m.invoke(syncSpec);
//      }
    }
  }

}
