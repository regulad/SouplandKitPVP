package us.soupland.kitpvp.kits.types;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ZenKit extends Kit {

    public ZenKit() {
        super("Zen", "&9Zen", "25s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.INK_SACK) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
            return;
        }

        if (player.getNearbyEntities(10, 10, 10).isEmpty()) {
            player.sendMessage(ColorText.translateAmpersand("&cThere are no players in a 10 block radius to target."));
            return;
        }

        Cooldown cooldown = KitPvP.getCooldown(getName());

        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }

        List<Player> players = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (!(entity instanceof Player) || entity == player) {
                continue;
            }
            if (ProfileManager.getProfile((Player) entity).getPlayerState() == PlayerState.SPAWN) {
                continue;
            }
            players.add((Player) entity);
        }

        if (players.isEmpty()) {
            player.sendMessage(ColorText.translateAmpersand("&cThere are currently no close available players."));
            return;
        }

        Player found = players.get(KitPvPUtils.getRandomNumber(players.size()));
        player.teleport(found);
        player.sendMessage(ColorText.translateAmpersand("&eTeleported to " + found.getName() + "&e."));

        cooldown.setCooldown(player);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.EMERALD_BLOCK).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setColor(Color.GREEN).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.STONE_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 2).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.INK_SACK).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").setDurability(3).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.SLIME_BALL).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public int getCreditCost() {
        return 6000;
    }
}