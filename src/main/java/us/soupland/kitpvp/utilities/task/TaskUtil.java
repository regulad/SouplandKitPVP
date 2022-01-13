package us.soupland.kitpvp.utilities.task;

import org.bukkit.scheduler.BukkitRunnable;
import us.soupland.kitpvp.KitPvP;

public class TaskUtil {

    public static void runTaskAsync(Runnable runnable) {
        KitPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(KitPvP.getInstance(), runnable);
    }

    public static void runTaskLater(Runnable runnable, long delay) {
        KitPvP.getInstance().getServer().getScheduler().runTaskLater(KitPvP.getInstance(), runnable, delay);
    }

    public static void runTaskTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(KitPvP.getInstance(), delay, timer);
    }

    public static void runTaskTimer(Runnable runnable, long delay, long timer) {
        KitPvP.getInstance().getServer().getScheduler().runTaskTimer(KitPvP.getInstance(), runnable, delay, timer);
    }

    public static void runTask(Runnable runnable) {
        KitPvP.getInstance().getServer().getScheduler().runTask(KitPvP.getInstance(), runnable);
    }
}