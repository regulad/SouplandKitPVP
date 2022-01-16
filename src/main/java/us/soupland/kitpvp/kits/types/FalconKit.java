package us.soupland.kitpvp.kits.types;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.server.ServerData;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;

public class FalconKit extends Kit {

    private ServerData serverData = KitPvP.getInstance().getServerData();

    public FalconKit() {
        super("Falcon", "&bFalcon", "10s");
        new Cooldown(getName(), TimeUtils.parse("10s"), getDisplayName(), null);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.FEATHER || !event.getAction().name().startsWith("RIGHT"))
            return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
            return;
        }

        if (player.getAllowFlight()) {
            return;
        }

        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player, true);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).setColor(Color.RED).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.LEATHER_CHESTPLATE).setColor(Color.WHITE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.LEATHER_LEGGINGS).setColor(Color.RED).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setColor(Color.WHITE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.FEATHER).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        if (serverData.getSpawnCuboID() != null && serverData.getSpawnCuboID().contains(event.getTo()) && (profile.getPlayerState() == PlayerState.PLAYING || profile.getPlayerState() == PlayerState.FIGHTINGPRACTICE)) {
            if (profile.getCurrentKit() instanceof FalconKit) {
                event.setTo(event.getFrom());
            }
        }
    }

}