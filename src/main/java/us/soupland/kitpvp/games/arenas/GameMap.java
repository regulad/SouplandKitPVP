package us.soupland.kitpvp.games.arenas;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.List;

@Data
@RequiredArgsConstructor
public class GameMap {

    @NonNull
    private String game;

    @NonNull
    private List<Location> locations;

}
