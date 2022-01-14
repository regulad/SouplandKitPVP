package us.soupland.kitpvp.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.utilities.cooldown.CustomEvent;

import java.util.List;

@AllArgsConstructor
@Getter
public class GameStartedEvent extends CustomEvent {

    private Game game;
    private List<Player> players;

}
