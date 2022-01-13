package us.soupland.kitpvp.practice.listener;

import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.practice.match.MatchHandler;
import us.soupland.kitpvp.practice.match.MatchState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class ArenaListener implements Listener {

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        int x = event.getBlock().getX();
        int y = event.getBlock().getY();
        int z = event.getBlock().getZ();

        Arena foundArena = null;

        for (Arena arena : ArenaHandler.getArenaMap()) {

            if (!arena.isActive()) {
                continue;
            }

            if (x >= arena.getFirstPosition().getBlockX() && x <= arena.getSecondPosition().getBlockX() && y >= arena.getFirstPosition().getBlockY() && y <= arena.getSecondPosition().getBlockY() &&
                    z >= arena.getFirstPosition().getBlockZ() && z <= arena.getSecondPosition().getBlockZ()) {
                foundArena = arena;
                break;
            }
        }

        if (foundArena == null) {
            return;
        }

        for (Match match : MatchHandler.getMatches()) {
            if (match.getArena().equals(foundArena)) {
                if (match.getState() == MatchState.FIGHTING) {
                    match.getPlacedBlocks().add(event.getToBlock().getLocation());
                }
                break;
            }
        }
    }
}