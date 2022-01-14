package us.soupland.kitpvp.practice.match.task;

import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import us.soupland.kitpvp.practice.match.Match;

@AllArgsConstructor
public class MatchResetTask extends BukkitRunnable {

    private Match match;

    @Override
    public void run() {
        /*if (match.getLadder().getName().toLowerCase().contains("build") && !match.getPlacedBlocks().isEmpty()) {
            TaskManager.IMP.async(() -> {
                EditSession session = new EditSessionBuilder(match.getArena().getFirstPosition().getWorld().getName())
                        .fastmode(true)
                        .allowedRegionsEverywhere()
                        .autoQueue(false)
                        .limitUnlimited()
                        .build();
                for (Location location : match.getPlacedBlocks()) {
                    try {
                        session.setBlock(new Vector(location.getBlockX(), location.getY(), location.getZ()), new BaseBlock(0, 0), EditSession.Stage.BEFORE_CHANGE);
                        session.flushQueue();
                    } catch (Exception ignored) {

                    }
                }
                session.flushQueue();

                TaskManager.IMP.async(() -> {
                    match.getPlacedBlocks().clear();
                    match.getArena().setActive(false);
                    cancel();
                });
            });
        } else if (match.getLadder().getName().toLowerCase().contains("build") && !match.getChangedBlocks().isEmpty()) {
            TaskManager.IMP.async(() -> {
                EditSession editSession = new EditSessionBuilder(match.getArena().getFirstPosition().getWorld().getName())
                        .fastmode(true)
                        .allowedRegionsEverywhere()
                        .autoQueue(false)
                        .limitUnlimited()
                        .build();

                for (BlockState blockState : match.getChangedBlocks()) {
                    try {
                        editSession.setBlock(
                                new Vector(blockState.getLocation().getBlockX(), blockState.getLocation().getBlockY(),
                                        blockState.getLocation().getZ()
                                ), new BaseBlock(blockState.getTypeId(), blockState.getRawData()), EditSession.Stage.BEFORE_CHANGE);
                        editSession.flushQueue();
                    } catch (Exception ignored) {
                    }
                }

                editSession.flushQueue();

                TaskManager.IMP.task(() -> {
                    if (match.getLadder().getName().toLowerCase().contains("build")) {
                        match.getChangedBlocks().clear();
                        match.getArena().setActive(false);
                    }

                    cancel();
                });
            });
        } else {
            cancel();
        }*/
    }
}