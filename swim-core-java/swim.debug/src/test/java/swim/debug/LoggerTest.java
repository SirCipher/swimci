package swim.debug;

import org.testng.annotations.Test;
import swim.debug.log.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class LoggerTest {

  @Test
  public void testLog() {
    Logger.info("wuzgd");
    Logger.flush(true);

    checkFileLineCount(1);
  }

  private void checkFileLineCount(int expected) {
    FileInputStream fis = null;
    try {
      File file = new File(Logger.LOG_FILE_NAME);
      System.out.println(file.getAbsolutePath());
      fis = new FileInputStream(file);
      System.out.println("Opened file");
    } catch (FileNotFoundException e) {
      fail("Failed to open log file", e);
    }

    Scanner sc = new Scanner(fis);

    int actualCount = 0;
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      System.out.println(line);
      actualCount++;
    }

    assertEquals(actualCount, expected);
  }

}