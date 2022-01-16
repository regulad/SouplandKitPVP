package us.soupland.kitpvp.kits.types;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;

public class DragonKit extends Kit {

    public DragonKit() {
        super("Dragon", "&cDragon", "30s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.BLAZE_POWDER) return;
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

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (!(entity instanceof Player) || player == entity) {
                continue;
            }
            Profile faggot = ProfileManager.getProfile((Player) entity);
            if (faggot.getPlayerState() == PlayerState.PLAYING || faggot.getPlayerState() == PlayerState.FIGHTINGPRACTICE) {
                entity.setFireTicks(100);
            }
        }
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        ItemStack helmet = new ItemMaker(Material.SKULL_ITEM).setDisplayname(getDisplayName()).setDurability(3).create();
        SkullMeta meta = (SkullMeta) helmet.getItemMeta();
        meta.setOwner("MHF_EnderDragon");
        helmet.setItemMeta(meta);

        inventory.setHelmet(helmet);
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.LEATHER_LEGGINGS).setColor(Color.BLACK).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setColor(Color.BLUE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.IRON_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 2).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.BLAZE_POWDER).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + "'s Breath").setUnbreakable(true).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200000, 0));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }
}