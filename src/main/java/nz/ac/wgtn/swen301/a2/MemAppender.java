package nz.ac.wgtn.swen301.a2;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.management.*;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemAppender extends AppenderSkeleton implements MemAppenderMBean {

    private String name;
    private long maxSize = 1000;
    private long discardedLogCount = 0;

    private final List<LoggingEvent> logs = new ArrayList<>();
    private final PatternLayout patternLayout = new PatternLayout();

    public MemAppender() {
        this.name = "MemAppender";
        registerMBean();
    }

    @Override
    public void setName(String name) {
        this.name = name;
        reRegisterMBean();
    }

    public String getNameProperty() {
        return super.getName();
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        enforceMaxSize();
    }

    public long getMaxSize() {
        return maxSize;
    }

    public long getDiscardedLogCount() {
        return discardedLogCount;
    }

    public List<LoggingEvent> getCurrentLogs() {
        return Collections.unmodifiableList(logs);
    }

    @Override
    protected void append(LoggingEvent event) {
        if (logs.size() >= maxSize) {
            logs.remove(0);
            discardedLogCount++;
        }
        logs.add(event);
    }

    public void export(String fileName) {
        JsonArray jsonArray = new JsonArray();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        for (LoggingEvent event : logs) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", event.getLoggerName());
            jsonObject.addProperty("level", event.getLevel().toString());

            Instant instant = Instant.ofEpochMilli(event.timeStamp);
            String timestamp = DateTimeFormatter.ISO_INSTANT.format(instant);
            jsonObject.addProperty("timestamp", timestamp);

            jsonObject.addProperty("thread", event.getThreadName());
            jsonObject.addProperty("message", event.getRenderedMessage());

            jsonArray.add(jsonObject);
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export logs to file: " + fileName, e);
        }
    }

    private void enforceMaxSize() {
        while (logs.size() > maxSize) {
            logs.remove(0);
            discardedLogCount++;
        }
    }

    @Override
    public void close() {
        logs.clear();
        unregisterMBean();
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    // MBean methods
    @Override
    public String[] getLogs() {
        String[] logStrings = new String[logs.size()];
        for (int i = 0; i < logs.size(); i++) {
            logStrings[i] = patternLayout.format(logs.get(i));
        }
        return logStrings;
    }

    @Override
    public long getLogCount() {
        return logs.size();
    }

    // MBean registration
    private void registerMBean() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("nz.ac.wgtn.swen301.a2:type=MemAppender,name=" + this.name);
            mbs.registerMBean(this, name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register MBean", e);
        }
    }

    private void reRegisterMBean() {
        unregisterMBean();
        registerMBean();
    }

    private void unregisterMBean() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("nz.ac.wgtn.swen301.a2:type=MemAppender,name=" + this.name);
            if (mbs.isRegistered(name)) {
                mbs.unregisterMBean(name);
            }
        } catch (Exception e) {
            // Ignore if MBean wasn't registered
        }
    }


}