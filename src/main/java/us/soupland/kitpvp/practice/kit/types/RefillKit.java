package us.soupland.kitpvp.practice.kit.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.soupland.kitpvp.practice.kit.Kit;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.List;

public class RefillKit extends Kit {

    public RefillKit() {
        super("Refill");
    }

    @Override
    public boolean isRanked() {
        return true;
    }

    @Override
    public boolean isDuel() {
        return true;
    }

    @Override
    public void onEquip(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        inventory.setHelmet(new ItemMaker(Material.IRON_HELMET).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.IRON_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setEnchant(Enchantment.DURABILITY, 3).setUnbreakable(true).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker(Material.MUSHROOM_SOUP).setDisplayname("&6Soup").create());
        }

        player.updateInventory();
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public List<String> getDescription() {
        return null;
    }
}