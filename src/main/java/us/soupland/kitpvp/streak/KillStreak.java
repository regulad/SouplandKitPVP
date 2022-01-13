package us.soupland.kitpvp.streak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public abstract class KillStreak {
    private int killStreak;
    private String display;

    public abstract boolean execute(Player player);
}