package swim.server;

public class Testy {


  public static void main(String[] args) throws InterruptedException {
    ListDownlinkSpec downlinkSpec = new ListDownlinkSpec();
    downlinkSpec.setTestPlane();
    downlinkSpec.testDrop();
  }

}
