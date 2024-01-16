package cc.mousse.steward.surface.util;

import cc.mousse.steward.surface.Application;
import cc.mousse.steward.surface.dto.Profile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.mineskin.MineskinClient;
import org.mineskin.SkinOptions;
import org.mineskin.Variant;
import org.mineskin.Visibility;
import org.mineskin.data.Skin;
import org.mineskin.data.Texture;

/**
 * @author PhineasZ
 */
public class SkinUtil {
  private SkinUtil() {}

  public static Texture getTexture(String playerName) {
    Profile profile = getProfile(playerName);
    if (profile == null) {
      LogUtil.warn("无法读取{}的档案", playerName);
      return null;
    }
    Texture texture = getTextureByProfile(playerName, profile);
    if (texture == null) {
      LogUtil.warn("无法读取{}的皮肤", playerName);
    }
    return texture;
  }

  public static Texture getTextureByProfile(String playerName, Profile profile) {
    String textureKey = profile.getTexture();
    Texture texture = CacheUtil.get(textureKey);
    if (texture != null) {
      return texture;
    }
    try {
      NetUtil.downloadImage(playerName, textureKey);
      File imgFile = new File(Application.getFilePath().concat(playerName).concat(".png"));
      Variant variant =
          "default".equalsIgnoreCase(profile.getModel()) ? Variant.CLASSIC : Variant.SLIM;
      MineskinClient mineskinClient = new MineskinClient(Application.PLUGIN_NAME);
      CompletableFuture<Skin> future =
          mineskinClient.generateUpload(
              imgFile, SkinOptions.create(playerName, variant, Visibility.PRIVATE));
      texture = future.get().data.texture;
      CacheUtil.set(profile.getTexture(), texture);
      CacheUtil.flush();
      return texture;
    } catch (ExecutionException e) {
      LogUtil.warn("MineSkin请求失败: {}", e.getMessage());
      LogUtil.warn(e);
      return null;
    } catch (InterruptedException e) {
      LogUtil.warn("MineSkin请求失败: {}", e.getMessage());
      LogUtil.warn(e);
      Thread.currentThread().interrupt();
      return null;
    } catch (FileNotFoundException e) {
      LogUtil.warn("缓存不存在");
      LogUtil.warn(e);
      Thread.currentThread().interrupt();
      return null;
    } catch (IOException e) {
      LogUtil.warn("BlessingSkin请求失败: {}", e.getMessage());
      LogUtil.warn(e);
      Thread.currentThread().interrupt();
      return null;
    } catch (URISyntaxException e) {
      LogUtil.warn(e);
      return null;
    }
  }

  public static Profile getProfile(String name) {
    try {
      String profileUrl = Application.getProfileUrl().replace("%playername%", name);
      InputStream is = URI.create(profileUrl).toURL().openStream();
      byte[] bytes = StrUtil.read(is);
      JsonObject skins =
          JsonUtil.toObj(new String(bytes), TypeToken.get(JsonObject.class))
              .getAsJsonObject("skins");
      Iterator<Entry<String, JsonElement>> iter = skins.entrySet().iterator();
      if (!iter.hasNext()) {
        return null;
      }
      Entry<String, JsonElement> entry = iter.next();
      return new Profile(entry.getValue().getAsString(), entry.getKey());
    } catch (Exception e) {
      LogUtil.warn(e);
      return null;
    }
  }
}
