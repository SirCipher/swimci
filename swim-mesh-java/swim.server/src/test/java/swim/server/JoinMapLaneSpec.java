// Copyright 2015-2019 SWIM.AI inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package swim.server;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import swim.actor.ActorSpaceDef;
import swim.api.SwimLane;
import swim.api.SwimRoute;
import swim.api.agent.AbstractAgent;
import swim.api.agent.AgentRoute;
import swim.api.downlink.MapDownlink;
import swim.api.lane.JoinMapLane;
import swim.api.lane.MapLane;
import swim.api.plane.AbstractPlane;
import swim.api.warp.function.DidReceive;
import swim.api.warp.function.WillReceive;
import swim.debug.lang.ThreadTools;
import swim.debug.log.Logger;
import swim.debug.log.MethodInvocationTracker;
import swim.kernel.Kernel;
import swim.observable.function.DidUpdateKey;
import swim.observable.function.WillUpdateKey;
import swim.runtime.downlink.MapDownlinkView;
import swim.runtime.warp.MapDownlinkModem;
import swim.runtime.warp.WarpDownlinkModem;
import swim.service.web.WebServiceDef;
import swim.structure.Value;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class JoinMapLaneSpec {

  static class TestMapLaneAgent extends AbstractAgent {

    @SwimLane("map")
    MapLane<String, String> testMap = this.<String, String>mapLane()
        .observe(new TestMapLaneController());

    class TestMapLaneController implements WillUpdateKey<String, String>, DidUpdateKey<String, String> {

      @Override
      public String willUpdate(String key, String newValue) {
//        System.out.println(nodeUri() + " willUpdate key: " + Format.debug(key) + "; newValue: " + Format.debug(newValue));
        return newValue;
      }

      @Override
      public void didUpdate(String key, String newValue, String oldValue) {
//        System.out.println(nodeUri() + " didUpdate key: " + Format.debug(key) + "; newValue: " + Format.debug(newValue) + "; oldValue: " + Format.debug(oldValue));
      }

    }

  }

  static class TestJoinMapLaneAgent extends AbstractAgent {

    @SwimLane("join")
    JoinMapLane<String, String, String> testJoinMap = this.<String, String, String>joinMapLane()
        .observe(new TestJoinMapLaneController());

    class TestJoinMapLaneController implements WillUpdateKey<String, String>, DidUpdateKey<String, String> {

      @Override
      public String willUpdate(String key, String newValue) {
//        System.out.println(nodeUri() + " willUpdate key: " + Format.debug(key) + "; newValue: " + Format.debug(newValue));
        return newValue;
      }

      @Override
      public void didUpdate(String key, String newValue, String oldValue) {
//        System.out.println(nodeUri() + " didUpdate key: " + Format.debug(key) + "; newValue: " + Format.debug(newValue) + "; oldValue: " + Format.debug(oldValue));
      }

    }

    @Override
    public void didStart() {
      testJoinMap.downlink("xs").hostUri("warp://localhost:53556").nodeUri("/map/xs").laneUri("map").open();
      testJoinMap.downlink("ys").hostUri("warp://localhost:53556").nodeUri("/map/ys").laneUri("map").open();
    }

  }

  static class TestJoinMapPlane extends AbstractPlane {

    @SwimRoute("/map/:name")
    AgentRoute<TestMapLaneAgent> mapRoute;

    @SwimRoute("/join/map/:name")
    AgentRoute<TestJoinMapLaneAgent> joinMapRoute;

  }

  private TestJoinMapPlane plane;
  private Kernel kernel;

  @BeforeMethod
  public void init() {
    kernel = ServerLoader.loadServerStack();
    plane = kernel.openSpace(ActorSpaceDef.fromName("test"))
        .openPlane("test", TestJoinMapPlane.class);

    kernel.openService(WebServiceDef.standard().port(53556).spaceName("test"));
    kernel.start();
  }

  @AfterMethod
  public void close() {
    kernel.stop();
  }

  /**
   * Test that the lane can accept and process a high rate of entries in a small period of time. This tests against a
   * previous bug that would cause the lane to stop processing entries.
   *
   * @throws InterruptedException if the thread is interrupted while waiting for a countdown
   */
  @Test(invocationCount = 100)//(groups = {"slow"})
  public void testInsertion() throws InterruptedException, IOException {
    ThreadTools.registerThreadDumpOnShutdown();
    Logger.squashSequentialInvocations(true);

    final int insertionCount = 10000;

    final CountDownLatch willReceive = new CountDownLatch(insertionCount);
    final CountDownLatch didReceive = new CountDownLatch(insertionCount);

    final MapDownlink<String, String> xs = getDownlink("/map/xs", "map", null);
    final MapDownlink<String, String> ys = getDownlink("/map/ys", "map", null);

    class Observer implements WillReceive, DidReceive {

      @Override
      public void didReceive(Value body) {
//        System.out.println("Did receive: "+ Format.debug(body));
        didReceive.countDown();
      }

      @Override
      public void willReceive(Value body) {
//        System.out.println("Will receive: "+ Format.debug(body));
        willReceive.countDown();
      }

    }

    final MapDownlink<String, String> join = getDownlink("/join/map/all", "join", new Observer());

    for (int i = 0; i < insertionCount; i++) {
      String ins = Integer.toString(i);
      xs.put(ins, ins);

      if (i % 10000 == 0) {
        System.out.println(i);
      }
    }

    System.out.println("Awaiting will receive");

//    didReceive.await(10, TimeUnit.SECONDS);
    willReceive.await(10, TimeUnit.SECONDS);

    Logger.flush(true);
    MethodInvocationTracker.printMetrics();

    System.out.println("MapDownlinkView#putCount: " + MapDownlinkView.putCount);
    System.out.println("WarpDownlinkModem#cueUpCount: " + WarpDownlinkModem.cueUpCount.get());
    System.out.println("WarpDownlinkModem#didBreakCount: " + WarpDownlinkModem.didBreakCount.get());
    System.out.println("WarpDownlinkModem#didFeedUpCount: " + WarpDownlinkModem.didFeedUpCount.get());
    System.out.println("WarpDownlinkModem#conditionNotMetCount: " + WarpDownlinkModem.conditionNotMetCount.get());

    System.out.println("MapDownlinkModem#feedUpMethodCount: " + MapDownlinkModem.feedUpMethodCount);
    System.out.println("MapDownlinkModem#feedUpQueueNotEmpty: " + MapDownlinkModem.feedUpQueueNotEmpty);
    System.out.println("MapDownlinkModem#nextUpCueCount: " + MapDownlinkModem.nextUpCueCount.get());
    System.out.println("MapDownlinkModem#nullCount: " + MapDownlinkModem.nullCount.get());
    System.out.println("MapDownlinkModem#conditionCount: " + MapDownlinkModem.conditionCount.get());
    System.out.println("MapDownlinkModem#nextUpCueNullCount: " + MapDownlinkModem.nextUpCueNullCount);
    System.out.println("MapDownlinkModem#pushNotNullCount: " + MapDownlinkModem.pushNotNullCount);

    assertEquals(willReceive.getCount(), 0);
    assertEquals(didReceive.getCount(), 0);
  }

  @Test
  public void testLinkToJoinMapLane() throws InterruptedException {
    final MapDownlink<String, String> xs = getDownlink("/map/xs", "map", null);
    final MapDownlink<String, String> ys = getDownlink("/map/ys", "map", null);

    final MapDownlink<String, String> join = getDownlink("/join/map/all", "join", (WillUpdateKey) (key, newValue) -> {
//      System.out.println("join link willUpdate key: " + Format.debug(key) + "; newValue: " + Format.debug(newValue));
      return newValue;
    });

    int threads = 1;
    CountDownLatch countDownLatch = new CountDownLatch(threads);
    Random random = new Random();
    ExecutorService executor = Executors.newFixedThreadPool(100);

    for (int i = 0; i < threads; i++) {
      executor.execute(() -> {
        for (int j = 0; j < 1_000_000; j++) {
          int r = random.nextInt();
          xs.put(Integer.toString(r), Integer.toString(r));

          if (j % 1000 == 0) {
            System.out.println(j);
          }

//      Thread.sleep(1);
        }

//        countDownLatch.countDown();
      });
    }

//    countDownLatch.await();

    Thread.sleep(30000);

    System.out.println("MapDownlinkView#putCount: " + MapDownlinkView.putCount);
    System.out.println("WarpDownlinkModem#cueUpCount: " + WarpDownlinkModem.cueUpCount.get());
    System.out.println("WarpDownlinkModem#didBreakCount: " + WarpDownlinkModem.didBreakCount.get());
    System.out.println("WarpDownlinkModem#didFeedUpCount: " + WarpDownlinkModem.didFeedUpCount.get());
    System.out.println("WarpDownlinkModem#conditionNotMetCount: " + WarpDownlinkModem.conditionNotMetCount.get());

    System.out.println("MapDownlinkModem#feedUpMethodCount: " + MapDownlinkModem.feedUpMethodCount);
    System.out.println("MapDownlinkModem#feedUpQueueNotEmpty: " + MapDownlinkModem.feedUpQueueNotEmpty);
    System.out.println("MapDownlinkModem#nextUpCueCount: " + MapDownlinkModem.nextUpCueCount.get());
    System.out.println("MapDownlinkModem#nullCount: " + MapDownlinkModem.nullCount.get());
    System.out.println("MapDownlinkModem#conditionCount: " + MapDownlinkModem.conditionCount.get());
    System.out.println("MapDownlinkModem#nextUpCueNullCount: " + MapDownlinkModem.nextUpCueNullCount);
  }

  private MapDownlink<String, String> getDownlink(String nodeUri, String laneUri, Object observer) {
    CountDownLatch didSyncLatch = new CountDownLatch(1);
    MapDownlink<String, String> downlink = plane.downlinkMap()
        .keyClass(String.class)
        .valueClass(String.class)
        .hostUri("warp://localhost:53556/")
        .nodeUri(nodeUri)
        .laneUri(laneUri)
        .didSync(didSyncLatch::countDown);

    if (observer != null) {
      downlink.observe(observer);
    }

    downlink.open();

    try {
      didSyncLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      fail();
    }

    return downlink;
  }

}
