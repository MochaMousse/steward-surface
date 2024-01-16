package cc.mousse.steward.surface;

import cc.mousse.steward.surface.util.CacheUtil;
import cc.mousse.steward.surface.util.LogUtil;
import cc.mousse.steward.surface.util.SkinUtil;
import java.io.File;
import lombok.Getter;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineskin.data.Texture;

/**
 * @author PhineasZ
 */
public final class Application extends JavaPlugin implements Listener {
  public static final String PLUGIN_NAME = "steward-surface";
  @Getter private static Application instance;
  @Getter private static String textureUrl;
  @Getter private static String profileUrl;
  @Getter private static String filePath;
  private SkinsRestorer skinsRestorer;

  @Override
  public void onEnable() {
    // Plugin startup logic
    setInstance(this);
    getServer().getPluginManager().registerEvents(this, this);
    skinsRestorer = SkinsRestorerProvider.get();
    saveDefaultConfig();
    reloadConfig();
    setProfileUrl(getConfig().getString("profile-url"));
    setTextureUrl(getConfig().getString("texture-url"));
    boolean textureUrlNotfound = textureUrl == null || textureUrl.isEmpty();
    boolean profileUrlNotfound = profileUrl == null || profileUrl.isEmpty();
    if (textureUrlNotfound || profileUrlNotfound) {
      LogUtil.warn("配置文件不完整");
    }
    setFilePath(getDataFolder().getPath());
    CacheUtil.init(new File(filePath.concat("cache.json")));
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            this,
            () -> {
              Player player = event.getPlayer();
              String playerName = player.getName();
              Texture texture = SkinUtil.getTexture(playerName);
              if (texture != null && player.isOnline()) {
                skinsRestorer
                    .getSkinApplier(Player.class)
                    .applySkin(player, SkinProperty.of(texture.value, texture.signature));
                LogUtil.info("已为{}加载皮肤", playerName);
              }
            });
  }

  public static void setInstance(Application instance) {
    Application.instance = instance;
  }

  public static void setTextureUrl(String textureUrl) {
    Application.textureUrl = textureUrl;
  }

  public static void setProfileUrl(String profileUrl) {
    Application.profileUrl = profileUrl;
  }

  public static void setFilePath(String filePath) {
    Application.filePath = filePath.concat("/");
  }
}
