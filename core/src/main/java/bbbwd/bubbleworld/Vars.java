package bbbwd.bubbleworld;

import bbbwd.bubbleworld.core.ContentLoader;
import bbbwd.bubbleworld.core.Control;
import bbbwd.bubbleworld.core.Renderer;
import bbbwd.bubbleworld.core.Resources;
import com.artemis.World;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.logging.*;

public class Vars {
    public static final float GRID_SIZE = 0.25f;
    public static final Logger logger = Logger.getGlobal();
    public static World ecs;
    public static Control control;
    public static Resources resources;
    public static ContentLoader contentLoader;
    public static Renderer renderer;

    static {
        try {
            Handler handler = new FileHandler("log.txt");
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    String source;
                    if (record.getSourceClassName() != null) {
                        source = record.getSourceClassName();
                        if (record.getSourceMethodName() != null) {
                            source += " " + record.getSourceMethodName();
                        }
                    } else {
                        source = record.getLoggerName();
                    }
                    String message = formatMessage(record);
                    String throwable = "";
                    if (record.getThrown() != null) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        pw.println();
                        record.getThrown().printStackTrace(pw);
                        pw.close();
                        throwable = sw.toString();
                    }
                    return String.format("%1$s%2$s: %3$s%4$s%n",
                        source,
                        record.getLevel().getLocalizedName(),
                        message,
                        throwable);
                }
            });
            logger.addHandler(handler);
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            logger.info("OS: " + osBean.getName() + " " + osBean.getVersion() + " " + osBean.getArch());
            Runtime runtime = Runtime.getRuntime();
            logger.info("CPU: " + runtime.availableProcessors());
            logger.info("MaxMemory: " + runtime.maxMemory() / 1024 / 1024 + "MB" +
                " TotalMemory: " + runtime.totalMemory() / 1024 / 1024 + "MB" +
                " FreeMemory: " + runtime.freeMemory() / 1024 / 1024 + "MB");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
