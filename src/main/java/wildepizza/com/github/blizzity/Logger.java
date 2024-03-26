package wildepizza.com.github.blizzity;

import java.util.Arrays;

public class Logger {
    public static void exception(Exception exception) {
        System.out.println(
                "Cause:" + exception.getCause().toString()
                + "\nMessage" + exception.getMessage()
                + "\nStacktrace:" + Arrays.toString(exception.getStackTrace())
        );
    }
}
