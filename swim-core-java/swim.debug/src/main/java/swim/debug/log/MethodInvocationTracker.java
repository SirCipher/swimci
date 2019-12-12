package swim.debug.log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodInvocationTracker {

  private static final Map<String, MethodInvocationMetric> methods = new ConcurrentHashMap<>();

  public static class MethodInvocationMetric {

    private final String methodName;
    private final Map<String, AtomicInteger> callerCount = new HashMap<>();
    private AtomicInteger totalCount = new AtomicInteger();

    MethodInvocationMetric(String methodName) {
      this.methodName = methodName;
    }

    void incrementCount(String caller) {
      final AtomicInteger atomicInteger = callerCount.get(caller);

      if (atomicInteger == null) {
        callerCount.put(caller, new AtomicInteger(1));
      } else {
        atomicInteger.incrementAndGet();
      }

      totalCount.incrementAndGet();
    }

    @Override
    public String toString() {
      StringBuilder msg = new StringBuilder("\t" + methodName + " invoked " + totalCount.get() + " times by:\n");
      for (Map.Entry<String, AtomicInteger> e : callerCount.entrySet()) {
        msg.append("\t\t")
            .append(e.getKey())
            .append(": ")
            .append(e.getValue())
            .append("\n");
      }

      return msg.toString();
    }
  }

  public static void printMetrics() {
    System.out.println("Tracked method statistics:");
    for (Map.Entry<String, MethodInvocationMetric> e : methods.entrySet()) {
      System.out.println(e.getValue());
    }
  }

  public static void log() {
    final Thread thread = Thread.currentThread();
    final String caller = thread.getStackTrace()[2].toString();
    final String methodCaller = thread.getStackTrace()[3].toString();

    if (methods.containsKey(caller)) {
      final MethodInvocationMetric metric = methods.get(caller);
      metric.incrementCount(methodCaller);

    } else {
      final MethodInvocationMetric metric = new MethodInvocationMetric(caller);
      metric.incrementCount(methodCaller);
      methods.put(caller, metric);
    }
  }

}
