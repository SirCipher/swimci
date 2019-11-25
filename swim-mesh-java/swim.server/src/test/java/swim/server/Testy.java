package swim.server;

public class Testy {

  public static void main(String[] args) throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
      System.out.println("Test: " + (i + 1));
      new ListDownlinkSpec().testInsert();
    }
  }

}
