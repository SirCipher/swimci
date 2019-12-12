package swim.debug.log;

public final class LogEntry {

  private final long nanoTime = System.nanoTime();
  private final long milliTime;
  private String message;

  public LogEntry(String message) {
    this.milliTime = System.currentTimeMillis();
    this.message = "[%d] " + message;
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