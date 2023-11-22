// This class manages the logging configuration for the sales application.

import java.util.logging.Level;
import java.util.logging.LogManager;

public class SalesAppManager {
    static {
        // Resetting the default logging configuration.
        LogManager.getLogManager().reset();
        
        // Setting the global log level to INFO.
        LogManager.getLogManager().getLogger("").setLevel(Level.INFO);
    }
}
