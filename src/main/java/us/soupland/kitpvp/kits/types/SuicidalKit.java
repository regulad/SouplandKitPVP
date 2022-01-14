package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.List;

public class SuicidalKit extends Kit {

    public SuicidalKit() {
        super("Suicidal", "&cSuicidal", "");
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.TNT)
            return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translate("&cYou can't do this in Spawn."));
            return;
        }

        if (player.getNearbyEntities(8, 8, 8).isEmpty()) {
            player.sendMessage(ColorText.translate("&cThere are no players in a 8 block radius to target."));
            return;
        }

        for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
            if (!(entity instanceof Player) || player == entity) {
                continue;
            }
            Profile faggot = ProfileManager.getProfile((Player) entity);
            if (faggot.getPlayerState() == PlayerState.PLAYING || faggot.getPlayerState() == PlayerState.FIGHTINGPRACTICE) {
                if (((Player) entity).getHealth() <= 3) {
                    ((Player) entity).setHealth(0);
                } else {
                    ((Player) entity).setHealth(3);
                }
                entity.sendMessage(ColorText.translate("&e" + player.getName() + " &ahas just bombed their self, and you got hurt."));
            }
        }

        player.setHealth(0);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.IRON_HELMET).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.IRON_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.TNT).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.TNT).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 3200;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Causes an explosion that will kill you, but do");
        list.add("&7massive amounts of damage to players around you.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

}