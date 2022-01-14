package us.soupland.kitpvp.practice.ladder.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.practice.kit.Kit;
import us.soupland.kitpvp.practice.kit.arcade.ArcherKit;
import us.soupland.kitpvp.practice.ladder.Ladder;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.List;

public class ArcadeLadder extends Ladder {

    public ArcadeLadder() {
        super("Arcade");
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemMaker(Material.getMaterial(401)).setDisplayname("&a&lA&b&lr&c&lc&d&la&e&ld&f&le").create();
    }

    @Override
    public Kit getPlayerKit() {
        List<Kit> list = new ArrayList<>();
        list.add(new ArcherKit());
        return list.get(KitPvPUtils.getRandomNumber(list.size()));
    }

    @Override
    public boolean isBuild() {
        return false;
    }

    @Override
    public boolean isRegeneration() {
        return true;
    }
}