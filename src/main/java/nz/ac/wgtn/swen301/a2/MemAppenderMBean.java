package nz.ac.wgtn.swen301.a2;

public interface MemAppenderMBean {
    String[] getLogs();            // formatted with PatternLayout (default pattern)

    long getLogCount();            // current stored logs (not including discarded)

    long getDiscardedLogCount();   // number of discarded (due to maxSize)

    void export(String fileName);  // export current logs to JSON array file
    // expose name via JMX
    String getNameProperty();
}
