package us.soupland.kitpvp.games.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import us.soupland.kitpvp.utilities.cooldown.CustomEvent;
import us.soupland.kitpvp.games.Game;

@Getter
@AllArgsConstructor
public class GameStartingEvent extends CustomEvent {

    private Game game;
}