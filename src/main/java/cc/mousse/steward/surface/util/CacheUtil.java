package cc.mousse.steward.surface.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.mineskin.data.Texture;

/**
 * @author PhineasZ
 */
public class CacheUtil {
  private static File cache;
  private static final Map<String, Texture> TEXTURE_MAP = new HashMap<>();

  private CacheUtil() {}

  public static void init(File cache) {
    if (CacheUtil.cache != null) {
      LogUtil.warn("缓存已存在");
      return;
    }
    if (!cache.exists()) {
      LogUtil.info("缓存文件不存在");
      try {
        if (cache.createNewFile()) {
          LogUtil.info("缓存文件已添加");
          CacheUtil.cache = cache;
        }
      } catch (IOException e) {
        LogUtil.warn("缓存文件添加失败: {}", e.getMessage());
        LogUtil.warn(e);
      }
      return;
    }
    CacheUtil.cache = cache;
    String str;
    try (FileInputStream fis = new FileInputStream(cache)) {
      byte[] bytes = StrUtil.read(fis);
      str = new String(bytes);
    } catch (IOException e) {
      LogUtil.warn("缓存文件读取错误: {}", e.getMessage());
      LogUtil.warn(e);
      return;
    }
    JsonElement element = JsonParser.parseString(str);
    if (!element.isJsonNull()) {
      JsonObject json = element.getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
        String key = entry.getKey();
        Texture texture =
            JsonUtil.toObj(JsonUtil.toStr(entry.getValue()), TypeToken.get(Texture.class));
        TEXTURE_MAP.put(key, texture);
      }
    }
  }

  public static void flush() {
    try (FileOutputStream fos = new FileOutputStream(CacheUtil.cache)) {
      String str = JsonUtil.toStr(CacheUtil.TEXTURE_MAP);
      fos.write(str.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      LogUtil.warn("缓存文件写入错误: {}", e.getMessage());
      LogUtil.warn(e);
    }
  }

  public static Texture get(String texture) {
    return CacheUtil.TEXTURE_MAP.get(texture);
  }

  public static void set(String key, Texture texture) {
    if (texture != null) {
      boolean valueOk = texture.value != null && texture.value.length() >= 10;
      boolean signatureOk = texture.signature != null && texture.signature.length() >= 10;
      if (valueOk && signatureOk) {
        CacheUtil.TEXTURE_MAP.put(key, texture);
      }
    }
  }
}
