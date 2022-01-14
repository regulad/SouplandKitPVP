package us.soupland.kitpvp.koth.selection;

import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.Arrays;

@Data
public class KothSelection {

    private Location first, second;

    public static ItemStack getWand() {
        return new ItemMaker(Material.DIAMOND_HOE).setDisplayname(ChatColor.GREEN + "Koth zone selection").addLore(Arrays.asList(
                "&aLeft click the ground&7 to set the &afirst&7 point.",
                "&aRight click the ground&7 to set the &asecond&7 point."
        )).create();
    }
}
