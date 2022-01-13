package us.soupland.kitpvp.games.events;

import lombok.Getter;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;
import us.soupland.kitpvp.games.Game;
import org.bukkit.entity.Player;

@Getter
public class PlayerWinGameEvent extends PlayerBase {

    private Game game;

    public PlayerWinGameEvent(Player player, Game game) {
        super(player);
        this.game = game;
    }
}