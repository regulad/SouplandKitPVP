package us.soupland.kitpvp.sidebar.scoreboard.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.sidebar.scoreboard.Aridi;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;

@Getter
public class PlayerLoadScoreboardEvent extends PlayerBase {

    private Aridi scoreboard;

    public PlayerLoadScoreboardEvent(Player player, Aridi scoreboard) {
        super(player);
        this.scoreboard = scoreboard;
    }
}