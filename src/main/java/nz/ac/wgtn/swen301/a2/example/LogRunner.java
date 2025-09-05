package nz.ac.wgtn.swen301.a2.example;

import nz.ac.wgtn.swen301.a2.MemAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import java.util.Random;

public class LogRunner {
    private static final Logger LOGGER = Logger.getLogger(LogRunner.class);
    private static final Random RANDOM = new Random();
    private static final Level[] LEVELS = {
            Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL
    };
    private static final String[] MESSAGES = {
            "Debug message", "Info message", "Warning message",
            "Error occurred", "Fatal error", "Processing complete",
            "User logged in", "Database connection established",
            "Cache cleared", "Request processed"
    };

    public static void main(String[] args) throws InterruptedException {
        // ✅ Create and attach MemAppender
        MemAppender memAppender = new MemAppender();
        memAppender.setName("MainMemoryAppender");
        LOGGER.addAppender(memAppender);

        long endTime = System.currentTimeMillis() + 120000; // 2 minutes

        while (System.currentTimeMillis() < endTime) {
            Level level = LEVELS[RANDOM.nextInt(LEVELS.length)];
            String message = MESSAGES[RANDOM.nextInt(MESSAGES.length)];

            LOGGER.log(level, message);

            Thread.sleep(1000); // 1 second
        }

        // ✅ Optionally, export logs at the end
        memAppender.export("out.json");
        System.out.println("Logs exported to out.json");
    }
}
