package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChemistKit extends Kit {

    public ChemistKit() {
        super("Chemist", "&aChemist", "");
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.CHAINMAIL_HELMET).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.DIAMOND_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.CHAINMAIL_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.IRON_SWORD).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.POTION).setDurability(16428).create());
        inventory.setItem(2, new ItemMaker(Material.POTION).setDurability(16426).create());
        inventory.setItem(3, new ItemMaker(Material.POTION).setDurability(16420).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Profile profile = ProfileManager.getProfile(player);

        Player killer = player.getKiller();

        if (player.getKiller() == null) {
            killer = profile.getLastDamager();
        }

        if (killer == null) {
            return;
        }

        if (!isEquipped(killer)) {
            return;
        }

        List<ItemStack> items = Arrays.asList(killer.getInventory().getContents());

        if (items.stream().noneMatch(itemStack -> itemStack != null && itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16428)) {
            killer.getInventory().addItem(new ItemMaker(Material.POTION).setDurability(16428).create());
        }
        if (items.stream().noneMatch(itemStack -> itemStack != null && itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16426)) {
            killer.getInventory().addItem(new ItemMaker(Material.POTION).setDurability(16426).create());
        }
        if (items.stream().noneMatch(itemStack -> itemStack != null && itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16420)) {
            killer.getInventory().addItem(new ItemMaker(Material.POTION).setDurability(16420).create());
        }

    }
}