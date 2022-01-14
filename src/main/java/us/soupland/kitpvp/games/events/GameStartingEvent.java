package us.soupland.kitpvp.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.utilities.cooldown.CustomEvent;

@Getter
@AllArgsConstructor
public class GameStartingEvent extends CustomEvent {

    private Game game;
}