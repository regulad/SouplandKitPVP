package us.soupland.kitpvp.kits.types;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.time.TimeUtils;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class SwitcherKit extends Kit {

    public SwitcherKit() {
        super("Switcher", "&bSwitcher", "25s");
        new Cooldown(getName(), TimeUtils.parse("25s"), getDisplayName(), null);
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.SNOW_BALL || !event.getAction().name().startsWith("RIGHT"))
            return;
        Cooldown cooldown = KitPvP.getCooldown(getName());

        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translate("&cYou can't do this in Spawn."));
        } else if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translate("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
        } else {
            return;
        }

        event.setUseItemInHand(Event.Result.DENY);
        player.updateInventory();

    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.IRON_HELMET).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.DIAMOND_CHESTPLATE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.CHAINMAIL_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.CHAINMAIL_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.SNOW_BALL).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").setAmount(16).create());

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
        return new ItemMaker(Material.SNOW_BALL).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 1600;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Switch positions.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Snowball) {
            if (((Snowball) event.getDamager()).getShooter() instanceof Player) {
                Player damaged = (Player) event.getEntity();
                Player damager = (Player) ((Snowball) event.getDamager()).getShooter();
                Profile profile = ProfileManager.getProfile(damager);
                if (!(profile.getCurrentKit() instanceof SwitcherKit)) {
                    return;
                }
                if (damager == damaged) {
                    return;
                }
                Cooldown cooldown = KitPvP.getCooldown(getName());
                if (cooldown.isOnCooldown(damager)) {
                    damager.sendMessage(ColorText.translate("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(damager), true) + "&c."));
                    return;
                }
                cooldown.setCooldown(damager);
                Location location = damaged.getLocation();
                damaged.teleport(damager);
                damager.teleport(location);
            }
        }
    }

}