package us.soupland.kitpvp.practice.arena;

import com.boydti.fawe.util.TaskManager;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.task.TaskUtil;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@AllArgsConstructor
public class ArenaGenerator {

    private String name;
    private Schematic schematic;
    private World world;

    public void generate(File file) {
        if (ArenaHandler.getByName(name) != null) {
            this.name += KitPvPUtils.getRandomNumber(1000);
        }

        System.out.println("[ArenaGenerator] Generating arenas [" + name + ']');

        int range = 1000, attempts = 0;

        int x = KitPvPUtils.getRandomNumber(range), z = KitPvPUtils.getRandomNumber(range);

        if (new Random().nextBoolean()) {
            x = -x;
        }

        if (new Random().nextBoolean()) {
            z = -z;
        }

        top:
        while (true) {
            attempts++;

            if (attempts >= 5) {
                x = KitPvPUtils.getRandomNumber(range);
                z = KitPvPUtils.getRandomNumber(range);

                if (new Random().nextBoolean()) {
                    x = -x;
                }

                if (new Random().nextBoolean()) {
                    z = -z;
                }

                range += 1000;

                System.out.println("[ArenaGenerator] Increased range to " + range);
            }

            if (world.getBlockAt(x, 72, z) == null) {
                continue;
            }

            int minX = x - schematic.getClipboard().getWidth() - 200,
                    maxX = x + schematic.getClipboard().getWidth() + 200,
                    minZ = z - schematic.getClipboard().getLength() - 200,
                    maxZ = z + schematic.getClipboard().getLength() + 200,
                    minY = 72,
                    maxY = 72 + schematic.getClipboard().getHeight();

            for (int i = minX; i < maxX; i++) {
                for (int a = minZ; a < maxZ; a++) {
                    for (int y = minY; y < maxY; y++) {
                        if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
                            continue top;
                        }
                    }
                }
            }

            //Location minCorner = new Location(this.world, minX, minY, minZ), maxCorner = new Location(this.world, maxX, maxY, maxZ);

            int finalX = x, finalZ = z;

            TaskManager.IMP.async(() -> {
                try {
                    new Schematic(file).pasteSchematic(world, finalX, 76, finalZ);
                } catch (IOException ignored) {
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }

                Arena arena = new Arena(name);

                helper:
                for (int a = minX; a < maxX; a++) {
                    for (int b = minZ; b < maxZ; b++) {
                        for (int c = minY; c < maxY; c++) {
                            if (world.getBlockAt(a, c, b).getType() == Material.SPONGE) {
                                Block block = world.getBlockAt(a, b, c);
                                Block upBlock = block.getRelative(BlockFace.UP, 1);

                                if (upBlock.getState() instanceof Sign) {
                                    Sign sign = (Sign) upBlock.getState();
                                    float yaw = Float.valueOf(sign.getLine(1)), pitch = Float.valueOf(sign.getLine(0));

                                    Location location = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ(), yaw, pitch);

                                    TaskUtil.runTask(() -> {
                                        upBlock.setType(Material.AIR);
                                        block.setType(block.getRelative(BlockFace.NORTH).getType());
                                    });

                                    if (arena.getFirstPosition() == null) {
                                        arena.setFirstPosition(location);
                                    } else if (arena.getSecondPosition() == null) {
                                        arena.setSecondPosition(location);
                                        break helper;
                                    }
                                }
                            }
                        }
                    }
                }

                arena.saveArena();
            });

            System.out.println("[ArenaGenerator] Pasted schematic at " + x + ", " + 76 + ", " + z + '!');

            return;
        }
    }
}