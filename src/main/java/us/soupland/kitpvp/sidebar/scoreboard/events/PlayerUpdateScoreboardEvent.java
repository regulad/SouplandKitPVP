package us.soupland.kitpvp.sidebar.scoreboard.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;
import us.soupland.kitpvp.sidebar.scoreboard.Aridi;

@Getter
public class PlayerUpdateScoreboardEvent extends PlayerBase {

    private Aridi scoreboard;

    public PlayerUpdateScoreboardEvent(Player player, Aridi scoreboard) {
        super(player);
        this.scoreboard = scoreboard;
    }
}