package us.soupland.kitpvp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ColorText;

import java.util.UUID;

@AllArgsConstructor
@Getter
public enum Achievement {

    BRAMMKINDS_SON("Brammkinds son", "Eat one million soups."), CHAMPION("Champion", "Win an events"),
    CRIMINAL("Criminal", "Kill 500 Players"), EXECUTIONER("Executioner", "Kill 5000 Players"), GENERAL_KITPVP("General of KitPvP", "Reach a Killstreak of 200"),
    MURDERER("Murderer", "Kill 1000 Players"), NEWCOMER("Newcomer", "Eat 50.000 soups"), OFFICER("Officer", "Reach a Killstreak of 100"),
    ORIGINAL_SOUPER("Original souper", "Eat 500.000 soups"), REGULAR("Learner", "Eat 200.000 soups"), SLAUGHTERER("Slaughterer", "Kill 8000 players"),
    SOLDIER("Soldier", "Reach a Killstreak of 50"), KILL_KATSU("Kill " + Bukkit.getOfflinePlayer(UUID.fromString("0285f685-c5f9-4be3-8b17-533fce184bcc")).getName(), "You must kill an Owner called " + Bukkit.getOfflinePlayer(UUID.fromString("0285f685-c5f9-4be3-8b17-533fce184bcc")).getName()),
    HOSTER("Hoster", "Host an event");

    private String displayName, description;

    public void broadcast(Player player) {
        Bukkit.broadcastMessage(ColorText.translate(player.getDisplayName() + " &bjust completed an achievement! &7(" + getDisplayName() + ')'));
        player.sendMessage(ColorText.translate("&aCongratulations! You just unlocked a new achievement. (" + getDescription() + ')'));

        Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.NOTE_PLING, 1, 1));
    }
}