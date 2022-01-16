package us.soupland.kitpvp.kits.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
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

public class FishermanKit extends Kit {

    public FishermanKit() {
        super("Fisherman", "&dFisherman", "25s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.GOLD_HELMET).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.GOLD_BOOTS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.FISHING_ROD).setDisplayname(profile.getTheme().getPrimaryColor() + "Fishing Rod").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() instanceof Player) {
            Player caught = (Player) event.getCaught();
            Player player = event.getPlayer();
            Profile profile = ProfileManager.getProfile(player);
            if (!(profile.getCurrentKit() instanceof FishermanKit)) {
                return;
            }
            if (player == caught) {
                return;
            }
            if (profile.isFrozenToUseAbility()) {
                player.sendMessage(ColorText.translateAmpersand("&cYou are currently jammed, so you can not use your ability."));
                return;
            }

            Cooldown cooldown = KitPvP.getCooldown(getName());
            if (cooldown.isOnCooldown(player)) {
                player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
                return;
            }
            cooldown.setCooldown(player);

            Vector vector = caught.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
            double x = vector.getX(), y = vector.getY(), z = vector.getZ();

            Location location = caught.getLocation().clone();
            location.setYaw(180 - (float) Math.toDegrees(Math.atan2(x, z)));
            location.setPitch(80 - (float) Math.toDegrees(Math.acos(y)));
            caught.teleport(location);
            caught.setVelocity(caught.getLocation().getDirection().multiply(0.5).setY(1));
            caught.sendMessage(ColorText.translateAmpersand("&aYou have been caught by &e" + player.getName() + " &aand he/she is reeling you in!"));
        }
    }

}