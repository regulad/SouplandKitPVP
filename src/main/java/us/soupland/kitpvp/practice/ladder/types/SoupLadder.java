package us.soupland.kitpvp.practice.ladder.types;

import us.soupland.kitpvp.practice.kit.Kit;
import us.soupland.kitpvp.practice.kit.types.RefillKit;
import us.soupland.kitpvp.practice.ladder.Ladder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class SoupLadder extends Ladder {

    public SoupLadder() {
        super("Soup");
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemMaker(Material.MUSHROOM_SOUP).setDisplayname("&c&lSoup").create();
    }

    @Override
    public Kit getPlayerKit() {
        return new RefillKit();
    }

    @Override
    public boolean isBuild() {
        return false;
    }

    @Override
    public boolean isRegeneration() {
        return false;
    }
}
