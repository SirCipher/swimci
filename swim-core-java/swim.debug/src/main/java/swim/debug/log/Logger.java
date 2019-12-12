package swim.debug.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class Logger {

  private static volatile boolean haveLogged = false;
  private static volatile boolean highLightSequentialInvocations = true;
  private static volatile boolean squashSequentialInvocations = false;
  private volatile static String lastMessage;

  private static final Deque<LogEntry> MESSAGES = new ConcurrentLinkedDeque<>();
  private static int bufferSize = 100_000;
  private static PrintWriter printWriter;

  public static final String LOG_FILE_NAME = "swim.log";

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
  public static void squashSequentialInvocations(boolean squashSequentialInvocations) {
    Logger.squashSequentialInvocations = squashSequentialInvocations;
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

    if (squashSequentialInvocations) {
      if (message.equals(lastMessage)) {
        final LogEntry logEntry = MESSAGES.peekLast();
        if (logEntry != null) {
          logEntry.incrementCount();
          return;
        }
      } else {
        lastMessage = message;
      }
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

  public static synchronized void flush(final boolean force) {
    if (!force) {// && Logger.MESSAGES.size() != bufferSize) {
      return;
    }

    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    System.out.println("Flushing " + Logger.MESSAGES.size() + " messages");

    for (LogEntry entry : Logger.MESSAGES) {
      final Date timestamp = new Date(entry.getMilliTime());
      final String res = sdf.format(timestamp);

      String msg = entry.getNanoTime() + ", " + entry.getMessage().replace("%d", res);
      int count = entry.getCount();
      msg = msg.replace("[%c]", "[" + count + "] ")
          + (highLightSequentialInvocations && count > 1 ? " <----------" : "");

      entry.setMessage(msg);

      printWriter.println(entry.toString());
    }

    printWriter.flush();

    Logger.MESSAGES.clear();
  }

}
