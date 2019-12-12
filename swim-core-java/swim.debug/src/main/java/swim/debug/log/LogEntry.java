package swim.debug.log;

import java.util.concurrent.atomic.AtomicInteger;

public final class LogEntry {

  private final long nanoTime = System.nanoTime();
  private final long milliTime;
  private String message;

  private AtomicInteger atomicInteger = new AtomicInteger(1);

  void incrementCount() {
    atomicInteger.incrementAndGet();
  }

  public int getCount() {
    return atomicInteger.get();
  }

  public LogEntry(String message) {
    this.milliTime = System.currentTimeMillis();
    this.message = "[%c][%d]" + message;
  }

  public long getNanoTime() {
    return nanoTime;
  }

  @Override
  public String toString() {
    return message;
  }

  public long getMilliTime() {
    return milliTime;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}