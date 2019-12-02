package swim.deflate;

import org.testng.TestException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DeflateUtil {

    static byte[] readResource(String resource, boolean removeCr) {
        try (InputStream input = DeflateSpec.class.getResourceAsStream(resource)) {
            final byte[] buffer = new byte[4096];
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int count;

            while (true) {
                count = input.read(buffer);
                if (count <= 0) {
                    break;
                }
                output.write(buffer, 0, count);
            }

            return removeCarriageReturns(output, removeCr);
        } catch (IOException cause) {
            throw new TestException(cause);
        }
    }

    private static byte[] removeCarriageReturns(ByteArrayOutputStream output, boolean deflate) {
        if (!System.getProperty("os.name").startsWith("Windows") || !deflate) {
            return output.toByteArray();
        }

        String str = new String(output.toByteArray());
        return str.replace("\r", "").getBytes();
    }

}
