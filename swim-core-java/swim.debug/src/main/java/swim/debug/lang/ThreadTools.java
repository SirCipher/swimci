package swim.debug.lang;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadTools {

  public static void registerThreadDumpOnShutdown() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Dumping threads on shutdown...");
      dumpThreads();
    }));
  }

  public static void dumpThreads() {
    final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    final ThreadInfo[] infos = bean.dumpAllThreads(true, true);

    for (final ThreadInfo info : infos) {
      System.out.println(info);
    }
  }

  public static void dumpCurrentThread() {
    final Thread thread = Thread.currentThread();

    System.out.println("-------------------------------------");
    System.out.println("Thread dump for: " + thread.getName());

    for (final StackTraceElement ste : thread.getStackTrace()) {
      System.out.println(ste);
    }
  }

}
