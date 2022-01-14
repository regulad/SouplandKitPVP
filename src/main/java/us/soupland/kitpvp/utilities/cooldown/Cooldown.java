package us.soupland.kitpvp.utilities.cooldown;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.task.TaskUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class Cooldown {

    @Getter
    private static Map<String, Cooldown> cooldownMap = new HashMap<>();
    private Map<UUID, Long> longMap;
    private String name, displayName, expiredMessage;
    private long duration;

    public Cooldown(String name, long duration) {
        this(name, duration, null, null);
    }

    public Cooldown(String name, long duration, String displayName, String expiredMessage) {
        this.longMap = new HashMap<>();
        this.name = name;
        this.duration = duration;
        this.displayName = ((displayName == null) ? name : displayName);
        if (expiredMessage != null) {
            this.expiredMessage = expiredMessage;
        }
        cooldownMap.put(name, this);
    }

    public void setCooldown(Player player) {
        this.setCooldown(player, false);
    }

    public void setCooldown(Player player, boolean announce) {
        CooldownStartingEvent event = new CooldownStartingEvent(player, this);
        if (!event.call()) {
            if (event.getReason() != null) {
                player.sendMessage(ColorText.translateAmpersand(event.getReason()));
            }
            return;
        }
        this.longMap.put(player.getUniqueId(), System.currentTimeMillis() + this.duration);
        if (new CooldownStartedEvent(player, this).call()) {
            if (this.expiredMessage != null && announce) {
                TaskUtil.runTaskLater(() -> {
                    if (player.isOnline() && isOnCooldown(player)) {
                        for (String s : expiredMessage.split("\n")) {
                            player.sendMessage(ColorText.translateAmpersand(s));
                        }
                        new CooldownExpiredEvent(player, this).call();
                    }
                }, (int) this.duration / 1000 * 20L);
            }
        }
    }

    public long getDuration(Player player) {
        long toReturn;
        if (this.longMap.containsKey(player.getUniqueId()) && (toReturn = this.longMap.get(player.getUniqueId()) - System.currentTimeMillis()) > 0L) {
            return toReturn;
        }
        return 0L;
    }

    public boolean isOnCooldown(Player player) {
        return this.getDuration(player) > 0L;
    }

    public boolean remove(Player player) {
        if (isOnCooldown(player)) {
            this.longMap.remove(player.getUniqueId());
            new CooldownExpiredEvent(player, this).setForced(true).call();
        }
        return isOnCooldown(player);
    }
}
