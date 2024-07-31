package cc.mousse.steward.surface.util;

import cc.mousse.steward.surface.Application;
import java.util.logging.Logger;

/**
 * @author MochaMousse
 */
public class LogUtil {
  private static final Logger SERVER_LOG = Application.getInstance().getLogger();

  private LogUtil() {}

  public static void warn(String message, Object... args) {
    String str = StrUtil.replace(message, args);
    SERVER_LOG.severe(str);
  }

  public static void warn(Exception e) {
    SERVER_LOG.severe(e.getMessage());
    e.printStackTrace();
  }

  public static void info(String message, Object... args) {
    String str = StrUtil.replace(message, args);
    SERVER_LOG.info(str);
  }
}
