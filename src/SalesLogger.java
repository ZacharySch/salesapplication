// This class manages logging for the sales application.

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class SalesLogger {
    private static final Logger logger = Logger.getLogger(SalesLogger.class.getName());

    static {
        try {
            // Configuring a file handler for logging to a file.
            Handler fileHandler = new FileHandler("D:/Documents/salesapplog.txt");
            fileHandler.setFormatter(new SimpleFormatter());

            // Setting the logging level to WARNING.
            logger.setLevel(Level.WARNING);
            
            // Adding the file handler to the logger.
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to log a warning message.
    public static void logWarning(String message) {
        logger.warning(message);
    }

    // Method to log an exception with a specified log level.
    public static void logException(Level level, String message, Throwable throwable) {
        logger.log(level, message, throwable);
    }
}
