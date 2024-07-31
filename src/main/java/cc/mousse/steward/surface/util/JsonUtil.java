package cc.mousse.steward.surface.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author MochaMousse
 */
public class JsonUtil {
  private static final Gson GSON = new Gson();

  private JsonUtil() {}

  public static <T> String toStr(T t) {
    return GSON.toJson(t);
  }

  public static <T> T toObj(String str, TypeToken<T> type) {
    return GSON.fromJson(str, type);
  }
}
