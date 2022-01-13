package us.soupland.kitpvp.sidebar.scoreboard.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;
import us.soupland.kitpvp.sidebar.scoreboard.Aridi;

@Getter
public class PlayerUnloadScoreboardEvent extends PlayerBase {

    private Aridi scoreboard;

    public PlayerUnloadScoreboardEvent(Player player, Aridi scoreboard) {
        super(player);
        this.scoreboard = scoreboard;
    }
}