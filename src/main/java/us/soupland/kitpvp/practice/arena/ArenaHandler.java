package us.soupland.kitpvp.practice.arena;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.practice.ladder.Ladder;
import us.soupland.kitpvp.utilities.KitPvPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaHandler {

    @Getter
    private static List<Arena> arenaMap = new ArrayList<>();

    public static void delete(Arena arena) {
        arenaMap.remove(arena);
        KitPvP.getInstance().getPvPDatabase().getArenas().deleteOne(Filters.eq("name", arena.getName()));
    }

    public static Arena getRandom(Ladder ladder) {
        List<Arena> arenas = arenaMap.stream().filter(arena -> arena.getFirstPosition() != null && arena.getSecondPosition() != null && arena.getLadders().contains(ladder.getName()) && !arena.isActive()).collect(Collectors.toList());

        if (arenas.isEmpty()) {
            return null;
        }

        return arenas.get(KitPvPUtils.getRandomNumber(arenas.size()));
    }

    public static Arena getByName(String name) {
        for (Arena arena : arenaMap) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    void registerArena(Arena arena) {
        if (!arenaMap.contains(arena)) {
            arenaMap.add(arena);
        }
    }
}