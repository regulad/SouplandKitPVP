package us.soupland.kitpvp.games.events;

import lombok.Getter;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;
import us.soupland.kitpvp.games.Game;
import org.bukkit.entity.Player;

@Getter
public class PlayerJoinGameEvent extends PlayerBase {

    private Game game;

    public PlayerJoinGameEvent(Player player, Game game) {
        super(player);
        this.game = game;
    }
}