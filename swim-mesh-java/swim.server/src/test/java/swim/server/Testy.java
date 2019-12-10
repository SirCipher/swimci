package swim.server;

public class Testy {

  public static void main(String[] args) throws InterruptedException {
    JoinMapLaneSpec joinMapLaneSpec = new JoinMapLaneSpec();
    joinMapLaneSpec.init();
    joinMapLaneSpec.testLinkToJoinMapLane();
    joinMapLaneSpec.close();
  }

}
