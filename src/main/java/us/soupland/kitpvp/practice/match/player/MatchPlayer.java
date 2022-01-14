package us.soupland.kitpvp.practice.match.player;

import lombok.Data;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.profile.ProfileManager;

import java.util.UUID;

@Data
public class MatchPlayer {

    private Player player;
    private UUID uuid;
    private boolean alive, disconnected;
    private int potionsThrown, potionsMissed, hits, combo, longestCombo;

    public MatchPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public int getPlayerElo() {
        return ProfileManager.getProfile(player).getStat(PlayerStat.ELO);
    }

    public void setPlayerElo(int i) {
        ProfileManager.getProfile(player).setStat(PlayerStat.ELO, i);
    }

    public void incrementPotionsThrown() {
        potionsThrown++;
    }

    public void incrementPotionsMissed() {
        potionsMissed++;
    }

    public void resetCombo() {
        combo = 0;
    }

    public void handlePlayerHit() {
        hits++;
        combo++;

        if (combo > longestCombo) {
            longestCombo = combo;
        }
    }

    public double getPotionAccuracy() {
        return (getPotionsMissed() == 0 ? 100.0 : getPotionsThrown() == getPotionsMissed() ? 50.0 : Math.round(100.0D - (((double) potionsMissed / (double) potionsThrown) * 100.0D)));
    }

}