package us.soupland.kitpvp.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.ProfileManager;

public class PracticeManager {

    public int getPlayersInSpawn() {
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ProfileManager.getProfile(player).getPlayerState().name().contains("PRACTICE")) {
                i++;
            }
        }
        return i;
    }

    public int getPlayersInMatch() {
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ProfileManager.getProfile(player).getPlayerState() == PlayerState.DUELPRACTICE) {
                i++;
            }
        }
        return i;
    }

    public int getPlayersInQueue() {
        int i = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ProfileManager.getProfile(player).getPlayerState() == PlayerState.QUEUEING) {
                i++;
            }
        }
        return i;
    }

}