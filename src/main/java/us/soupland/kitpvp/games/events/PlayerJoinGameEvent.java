package us.soupland.kitpvp.games.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;

@Getter
public class PlayerJoinGameEvent extends PlayerBase {

    private Game game;

    public PlayerJoinGameEvent(Player player, Game game) {
        super(player);
        this.game = game;
    }
}