package us.soupland.kitpvp.kits.types;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;

public class JesterKit extends Kit {

    public JesterKit() {
        super("Jester", "&6Jester", "30s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.DIAMOND_HOE) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
            return;
        }

        if (player.getNearbyEntities(5, 5, 5).isEmpty()) {
            player.sendMessage(ColorText.translateAmpersand("&cThere are no players in a 5 block radius to target."));
            return;
        }

        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player);

        int i = KitPvPUtils.getRandomNumber(100);

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (!(entity instanceof Player) || player == entity) {
                continue;
            }
            if (i > 40 && i < 70) {
                entity.teleport(new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), KitPvPUtils.getRandomNumber((180 - -180) + 1), entity.getLocation().getPitch()));
            } else if (i < 40) {
                PlayerInventory inventory = ((Player) entity).getInventory();
                for (ItemStack stack : inventory.getContents()) {
                    if (stack == null || stack.getType() == Material.AIR) {
                        continue;
                    }
                    if (stack.getType().name().endsWith("SWORD") || stack.getType().name().endsWith("AXE")) {
                        int random = KitPvPUtils.getRandomNumber(9);
                        if (inventory.getItem(random) != null && inventory.getItem(random).getType() != Material.AIR) {
                            inventory.setItem(random, inventory.getItem(random));
                            if (inventory.getItem(random).getType() == Material.MUSHROOM_SOUP || inventory.getItem(random).getType() == Material.POTION) {
                                inventory.setItem(random, stack);
                            }
                        }
                        ((Player) entity).updateInventory();
                        break;
                    }
                }
                player.sendMessage(ColorText.translateAmpersand("&eYou have moved your enemies' sword."));
            } else {
                ((Player) entity).openInventory(new InventoryMaker("&cYou have been trolled!", 1).getCurrentPage());
                player.sendMessage(ColorText.translateAmpersand("&6&k&l||| &cINVENTORY TROLL &6&k&l||| &ehas been opened to &d" + entity.getName() + "&e."));
            }
        }
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).setColor(Color.YELLOW).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.LEATHER_CHESTPLATE).setColor(Color.RED).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.LEATHER_LEGGINGS).setColor(Color.BLACK).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setColor(Color.BLUE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.DIAMOND_HOE).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").setUnbreakable(true).create());

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
        return new ItemMaker(Material.DIAMOND_HOE).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public int getCreditCost() {
        return 5200;
    }
}