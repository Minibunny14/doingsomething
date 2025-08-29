package nz.ac.wgtn.swen301.a2;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.jupiter.api.Assertions.*;

public class JsonLayoutTest {

    @Test
    public void testJsonLayoutFormat() {
        JsonLayout layout = new JsonLayout();
        Logger logger = Logger.getLogger("testLogger");
        long timestamp = System.currentTimeMillis();

        LoggingEvent event = new LoggingEvent(
                "testCategory",
                logger,
                timestamp,
                Level.INFO,
                "Test message",
                null
        );

        String json = layout.format(event);
        assertNotNull(json);
        assertTrue(json.startsWith("{"));
        assertTrue(json.endsWith("}"));

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        assertEquals("testLogger", jsonObject.get("name").getAsString());
        assertEquals("INFO", jsonObject.get("level").getAsString());
        assertEquals("Test message", jsonObject.get("message").getAsString());
        assertEquals("main", jsonObject.get("thread").getAsString());
    }

    @Test
    public void testJsonLayoutWithDifferentLevels() {
        JsonLayout layout = new JsonLayout();
        Logger logger = Logger.getLogger("testLogger");
        long timestamp = System.currentTimeMillis();

        Level[] levels = {Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL};

        for (Level level : levels) {
            LoggingEvent event = new LoggingEvent(
                    "testCategory",
                    logger,
                    timestamp,
                    level,
                    "Message for " + level.toString(),
                    null
            );

            String json = layout.format(event);
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            assertEquals(level.toString(), jsonObject.get("level").getAsString());
            assertEquals("Message for " + level.toString(), jsonObject.get("message").getAsString());
        }
    }

    @Test
    public void testJsonLayoutIgnoresThrowable() {
        JsonLayout layout = new JsonLayout();
        assertTrue(layout.ignoresThrowable());
    }

    @Test
    public void testJsonLayoutTimestampFormat() {
        JsonLayout layout = new JsonLayout();
        Logger logger = Logger.getLogger("testLogger");
        long timestamp = 1633046400000L; // Fixed timestamp for testing

        LoggingEvent event = new LoggingEvent(
                "testCategory",
                logger,
                timestamp,
                Level.INFO,
                "Test message",
                null
        );

        String json = layout.format(event);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        String expectedTimestamp = "2021-10-01T00:00:00Z";
        assertEquals(expectedTimestamp, jsonObject.get("timestamp").getAsString());
    }
}