package us.soupland.kitpvp.games.events;

import lombok.Getter;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;
import us.soupland.kitpvp.games.Game;
import org.bukkit.entity.Player;

public class PlayerLeaveGameEvent extends PlayerBase {

    @Getter
    private Game game;

    public PlayerLeaveGameEvent(Player player, Game game) {
        super(player);
        this.game = game;
    }
}