package swim.debug.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Logger {

  private static final Queue<LogEntry> MESSAGES = new ConcurrentLinkedQueue<>();
  private static boolean haveLogged = false;
  public static final String LOG_FILE_NAME = "swim.log";
  private static int bufferSize = 100_000;
  private static PrintWriter printWriter;

  private Logger() {
    throw new AssertionError();
  }

  static {
    try {
      clear();

      printWriter = new PrintWriter(Logger.LOG_FILE_NAME);

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        flush(true);
        printWriter.close();
      }));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Failed to create file", e);
    }
  }

  private static void open() {
    try {
      printWriter = new PrintWriter(Logger.LOG_FILE_NAME);
      // Clear the contents first
      printWriter.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Failed to create file", e);
    }
  }

  public static void clear() {
    open();
    Logger.MESSAGES.clear();
  }

  public static void info(final String message) {
    log(message, "INFO");
  }

  public static void trace(final String message) {
    log(message, "TRACE");
  }

  public static void warn(final String message) {
    log(message, "WARN");
  }

  // TODO
  private static void error(final String message, final Throwable throwable) {
    log(message, "ERROR");
  }

  // TODO
  private static void fatal(final String message, final Throwable throwable) {
    log(message, "FATAL");
  }

  private static void log(final String message, final String level) {
    if (!haveLogged) {
      haveLogged = true;
    }

    final Thread thread = Thread.currentThread();
    final int METHOD_CALLER_INDEX = 3;

    MESSAGES.add(new LogEntry("[" + level + "] " + thread.getName() + "@" + thread.getStackTrace()[METHOD_CALLER_INDEX] + " " + message));
    flush(false);
  }

  public static void setBufferSize(int newBufferSize) {
    if (haveLogged) {
      throw new IllegalStateException("Cannot change the buffer size after logging");
    }

    Logger.bufferSize = newBufferSize;
  }

  public static void flush(boolean force) {
    if (!force){// && Logger.MESSAGES.size() != bufferSize) {
      return;
    }

    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    System.out.println("Writing log files...");

    for (LogEntry entry : Logger.MESSAGES) {
      Date timestamp = new Date(entry.getMilliTime());
      String res = sdf.format(timestamp);
      entry.setMessage(entry.getNanoTime() + ", " + entry.getMessage().replace("%d", res));

      printWriter.println(entry.toString());
    }

    Logger.MESSAGES.clear();
  }

}
