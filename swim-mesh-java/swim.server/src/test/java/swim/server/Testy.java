package swim.server;

import java.io.IOException;

public class Testy {

  public static void main(String[] args) throws IOException, InterruptedException {
    JoinMapLaneSpec joinMapLaneSpec = new JoinMapLaneSpec();

    for (int i = 0; i < 1; i++) {
      System.out.println("-------------------------------------------------------------------------------------------------------------");
      System.out.println("Test no: " + i);
      joinMapLaneSpec.init();
      joinMapLaneSpec.testInsertion();
      joinMapLaneSpec.close();
    }
  }

}
