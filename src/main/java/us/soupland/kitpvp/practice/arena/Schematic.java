package us.soupland.kitpvp.practice.arena;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.DataException;
import lombok.Getter;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

public class Schematic {

    @Getter
    private CuboidClipboard clipboard;

    public Schematic(File file) throws IOException {
        SchematicFormat format = SchematicFormat.MCEDIT;

        try {
            this.clipboard = format.load(file);
        } catch (DataException ignored) {
        }
    }

    void pasteSchematic(World world, int x, int y, int z) {
        Vector pastePos = new Vector(x, y, z);
        EditSession editSession = new EditSessionBuilder(new BukkitWorld(world))
                .fastmode(true)
                .allowedRegionsEverywhere()
                .autoQueue(false)
                .limitUnlimited()
                .build();

        try {
            this.clipboard.place(editSession, pastePos, true);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

}
