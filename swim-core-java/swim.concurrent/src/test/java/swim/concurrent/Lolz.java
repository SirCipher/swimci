package swim.concurrent;

public class Lolz {

  public static void main(String[] args) {
    for (int i = 0; i < 100; i++) {
      System.out.println("-------------------------------------");
      System.out.println("Run: " + (i + 1));

      TestTheaterSpec testTheaterSpec = new TestTheaterSpec();
      testTheaterSpec.invokeIntrospectionCallbacks();
    }
  }

}
