package swim.server;

import swim.debug.log.Logger;
import java.io.IOException;

public class Testy {

  public static void main(String[] args) throws InterruptedException, IOException {
    JoinMapLaneSpec joinMapLaneSpec = new JoinMapLaneSpec();

    for (int i = 0; i < 1; i++) {
      Logger.info("Running test: " + i);

      System.out.println("-------------------------------------------------------------------------------------------------------------");
      System.out.println("Test no: " + i);
      joinMapLaneSpec.init();
      joinMapLaneSpec.testInsertion();
      joinMapLaneSpec.close();
    }

  }

}
