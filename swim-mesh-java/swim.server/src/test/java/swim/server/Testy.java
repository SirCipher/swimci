package swim.server;

import java.lang.reflect.InvocationTargetException;

public class Testy {

  public static void main(String[] args) throws InterruptedException, InvocationTargetException, IllegalAccessException {
    for (int i = 0; i < 1000; i++) {
      System.out.println("Test: " + (i + 1));
      ListDownlinkSpec listDownlinkSpec = new ListDownlinkSpec();
      listDownlinkSpec.testInsert();
      System.out.println("------------------------------------------------");

//      for (Method m : listDownlinkSpec.getClass().getDeclaredMethods()) {
//        if (m.isAnnotationPresent(Test.class)) {
//          System.out.println("Invoking: " + m.getName());
//          m.invoke(listDownlinkSpec);
//        }
//      }
    }
  }

}
