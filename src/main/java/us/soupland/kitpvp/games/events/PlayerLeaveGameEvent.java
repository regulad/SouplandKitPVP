package us.soupland.kitpvp.games.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;

public class PlayerLeaveGameEvent extends PlayerBase {

    @Getter
    private Game game;

    public PlayerLeaveGameEvent(Player player, Game game) {
        super(player);
        this.game = game;
    }
}