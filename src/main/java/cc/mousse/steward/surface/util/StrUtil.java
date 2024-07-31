package cc.mousse.steward.surface.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author MochaMousse
 */
public class StrUtil {
  private StrUtil() {}

  public static String replace(String str, Object... args) {
    for (Object arg : args) {
      str = str.replaceFirst("\\{}", arg.toString());
    }
    return str;
  }

  public static byte[] read(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[1024];
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int len;
    while ((len = inputStream.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    bos.close();
    return bos.toByteArray();
  }
}
