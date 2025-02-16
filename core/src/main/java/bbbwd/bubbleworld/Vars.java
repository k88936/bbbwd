package bbbwd.bubbleworld;

import bbbwd.bubbleworld.core.*;
import bbbwd.bubbleworld.game.systems.logic.GlobalVars;
import com.artemis.World;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.logging.*;

public class Vars {
    public static final float GRID_SIZE = 0.125f;
    public static World ecs;
    public static Control control;
    public static Resources resources;
    public static ContentLoader contentLoader;
    public static Renderer renderer;

    public static GameState state;
    public static GlobalVars logicVars;
    static {
//        try {
//            Handler handler = new FileHandler("log.txt");
//            handler.setFormatter(new Formatter() {
//                @Override
//                public String format(LogRecord record) {
//                    String source;
//                    if (record.getSourceClassName() != null) {
//                        source = record.getSourceClassName();
//                        if (record.getSourceMethodName() != null) {
//                            source += " " + record.getSourceMethodName();
//                        }
//                    } else {
//                        source = record.getLoggerName();
//                    }
//                    String message = formatMessage(record);
//                    String throwable = "";
//                    if (record.getThrown() != null) {
//                        StringWriter sw = new StringWriter();
//                        PrintWriter pw = new PrintWriter(sw);
//                        pw.println();
//                        record.getThrown().printStackTrace(pw);
//                        pw.close();
//                        throwable = sw.toString();
//                    }
//                    return String.format("%1$s%2$s: %3$s%4$s%n",
//                        source,
//                        record.getLevel().getLocalizedName(),
//                        message,
//                        throwable);
//                }
//            });
//            logger.addHandler(handler);
//            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
//            Runtime runtime = Runtime.getRuntime();
//            logger.info("OS: " + osBean.getName() + " " + osBean.getVersion() + " " + osBean.getArch() + " CPU: " + runtime.availableProcessors());
//            logger.info("MaxMemory: " + runtime.maxMemory() / 1024 / 1024 + "MB" +
//                " TotalMemory: " + runtime.totalMemory() / 1024 / 1024 + "MB" +
//                " FreeMemory: " + runtime.freeMemory() / 1024 / 1024 + "MB");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}
