package us.soupland.kitpvp.kits.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ArcherKit extends Kit {

    private HashMap<UUID, Location> locations = new HashMap<>();

    public ArcherKit() {
        super("Archer", "&dArcher", "");
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.IRON_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());

        inventory.setItem(1, new ItemMaker(Material.BOW).setEnchant(Enchantment.ARROW_INFINITE, 1).setUnbreakable(true).create());
        inventory.setItem(2, new ItemMaker(Material.ENDER_PEARL).setAmount(3).create());

        inventory.setItem(9, new ItemStack(Material.ARROW));

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {

        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {

            Arrow arrow = (Arrow) event.getEntity();
            Player player = (Player) event.getEntity().getShooter();
            Profile profile = ProfileManager.getProfile(player);

            if (profile.getPlayerState() == PlayerState.SPAWN) {
                player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
                return;
            }

            if (isEquipped(player)) {
                this.locations.put(player.getUniqueId(), player.getLocation());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                Player damaged = (Player) event.getEntity();
                Player shooter = (Player) source;

                if (!isEquipped(shooter)) {
                    return;
                }

                if (damaged.getName().equalsIgnoreCase(shooter.getName())) {
                    return;
                }

                if (this.locations.get(shooter.getUniqueId()) == null) {
                    return;
                }

                int distance = (int) this.locations.get(shooter.getUniqueId()).distance(damaged.getLocation());
                this.locations.remove(shooter.getUniqueId());

                damaged.setHealth(damaged.getHealth() - (distance / 30));
            }
        }
    }
}