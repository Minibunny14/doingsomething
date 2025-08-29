package nz.ac.wgtn.swen301.a2;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class JsonLayout extends Layout {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String format(LoggingEvent event) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", event.getLoggerName());
        jsonObject.addProperty("level", event.getLevel().toString());

        Instant instant = Instant.ofEpochMilli(event.timeStamp);
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(instant);
        jsonObject.addProperty("timestamp", timestamp);

        jsonObject.addProperty("thread", event.getThreadName());
        jsonObject.addProperty("message", event.getRenderedMessage());

        return GSON.toJson(jsonObject);
    }

    @Override
    public boolean ignoresThrowable() {
        return true;
    }

    @Override
    public void activateOptions() {
        // No options to activate
    }
}