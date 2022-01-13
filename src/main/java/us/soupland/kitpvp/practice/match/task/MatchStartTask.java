package us.soupland.kitpvp.practice.match.task;

import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.practice.match.MatchState;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartTask extends BukkitRunnable {

    private Match match;
    private int ticks;

    public MatchStartTask(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        int i = 5 - ticks;
        if (match.getState() == MatchState.ENDING) {
            cancel();
            return;
        }
        if (match.getLadder().getName().equalsIgnoreCase("Gapple") || match.getLadder().getName().equalsIgnoreCase("Combo")) {
            if (i == 1) {
                for (Player player : new Player[]{match.getFirstPlayer(), match.getSecondPlayer()}) {
                    if (player != null) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200000, 1));
                    }
                }
            }
        }
        if (match.getLadder().getName().toLowerCase().contains("sumo")) {
            if (i == 2) {
                for (Player player : new Player[]{match.getFirstPlayer(), match.getSecondPlayer()}) {
                    if (player != null) {
                        PlayerUtils.managePlayerMovement(player, true);
                    }
                }
                match.setState(MatchState.FIGHTING);
                match.setStartTimestamp(System.currentTimeMillis());
                match.broadcastMessage("&aThe round has started.");
                match.playSound(Sound.NOTE_BASS);
                cancel();
                return;
            }

            match.broadcastMessage("&4&l[Match] &fStarting in &c" + (i - 2) + " second" + (i - 2 == 1 ? "" : "s") + "&f!");
            match.playSound(Sound.NOTE_PLING);
        } else {
            if (i == 0) {
                match.setState(MatchState.FIGHTING);
                match.setStartTimestamp(System.currentTimeMillis());
                match.broadcastMessage("&aThe match has started!");
                match.playSound(Sound.NOTE_BASS);
                cancel();
                return;
            }

            match.broadcastMessage("&4&l[Match] &fStarting in &c" + i + " second" + (i == 1 ? "" : "") + "&f!");
            match.playSound(Sound.NOTE_PLING);
        }
        ticks++;
    }
}