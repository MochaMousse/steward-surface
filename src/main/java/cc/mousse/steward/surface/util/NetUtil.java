package cc.mousse.steward.surface.util;

import cc.mousse.steward.surface.Application;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author MochaMousse
 */
public class NetUtil {
  private NetUtil() {}

  public static void downloadImage(String playerName, String texture)
      throws IOException, URISyntaxException {
    URL url =
        new URI(StrUtil.replace(Application.getTextureUrl().replace("%texture%", texture))).toURL();
    try (InputStream in = url.openStream()) {
      Files.copy(
          in,
          Path.of(Application.getFilePath().concat(playerName).concat(".png")),
          StandardCopyOption.REPLACE_EXISTING);
    }
  }
}
