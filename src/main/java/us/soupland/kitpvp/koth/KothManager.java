package us.soupland.kitpvp.koth;

import lombok.Data;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.koth.selection.KothListener;
import us.soupland.kitpvp.utilities.chat.ColorText;

@Data
public class KothManager {

    public KothManager() {
        Bukkit.getPluginManager().registerEvents(new KothListener(), KitPvP.getInstance());
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Koth koth : Koth.getKoths().values()) {
                    if (!koth.isActive()) {
                        continue;
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isFlying()) {
                            continue;
                        }
                        if (koth.contains(player.getLocation())) {
                            if (koth.getCapper() == null) {
                                koth.setCapper(player);
                            }

                            if (koth.getRemaining() % 15 == 0 && koth.getRemaining() != koth.getSeconds() || koth.getRemaining() <= 5) {
                                Bukkit.broadcastMessage(ColorText.translate("&4&l[KOTH] &eSomeone is trying to control the &cKoth&e. &7(" + DurationFormatUtils.formatDurationWords(koth.getRemaining() * 1000, true, true) + ')'));
                            }
                        } else {
                            if (koth.getCapper() == player) {
                                Bukkit.broadcastMessage(ColorText.translate("&4&l[KOTH] " + player.getName() + " &ehas been knocked. &7(" + DurationFormatUtils.formatDurationWords(koth.getRemaining() * 1000, true, true) + ')'));

                                koth.setCapper(null);
                                koth.setRemaining(koth.getSeconds());
                            }
                        }
                    }

                    if (koth.getCapper() != null) {
                        koth.setRemaining(koth.getRemaining() - 1);
                    }

                    if (koth.getRemaining() <= 0L) {
                        if (koth.getCapper() != null) {
                            Bukkit.broadcastMessage(ColorText.translate("&4&l[KOTH] &eEvent has been captured by &c" + koth.getCapper().getName()));

                        }

                        koth.setCapper(null);
                        koth.setRemaining(koth.getSeconds());
                        koth.setActive(false);
                    }
                }
            }
        }.runTaskTimerAsynchronously(KitPvP.getInstance(), 2L, 20L);
    }

}