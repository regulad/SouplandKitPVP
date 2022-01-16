package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;

public class SummonerKit extends Kit {

    public SummonerKit() {
        super("Summoner", "&cSummoner", "20s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.SKULL_ITEM) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
            return;
        }

        if (player.getNearbyEntities(20, 20, 20).isEmpty()) {
            player.sendMessage(ColorText.translateAmpersand("&cThere are no players in a 20 block radius to target."));
            return;
        }

        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player);

        for (int i = 0; i < 4; i++) {
            IronGolem golem = (IronGolem) player.getWorld().spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);
            golem.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 3));
            golem.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200000, 7));
            golem.setPlayerCreated(false);
            golem.setCanPickupItems(false);
            golem.setMaxHealth(2000);
            golem.setHealth(2000);
            golem.setCustomName(ColorText.translateAmpersand(player.getName()));
            golem.setCustomNameVisible(true);
            golem.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(KitPvP.getInstance(), "faggotSummoner"));
        }

        TaskUtil.runTaskLater(() -> {
            if (player.isOnline()) {
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity.hasMetadata(player.getUniqueId().toString())) {
                        entity.remove();
                    }
                }
            }
        }, 5 * 20L);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDurability(3).create();
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner("MHF_Golem");
        meta.setDisplayName(ColorText.translateAmpersand(profile.getTheme().getPrimaryColor() + getName()));
        stack.setItemMeta(meta);

        inventory.setHelmet(stack);

        inventory.setItem(0, new ItemMaker(Material.STONE_SWORD).setUnbreakable(true).setEnchant(Enchantment.DAMAGE_ALL, 3).create());

        meta.setOwner("MHF_Golem");
        stack.setItemMeta(meta);

        inventory.setItem(1, new ItemMaker(stack).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200000, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player target = (Player) event.getTarget();
            if (event.getEntity().hasMetadata(target.getUniqueId().toString())) {
                for (Entity entity : target.getNearbyEntities(30, 30, 30)) {
                    Match match = ProfileManager.getProfile(target).getMatch();
                    if (match != null) {
                        if (match.getOpponent(target) != entity) {
                            continue;
                        }
                    }
                    if (entity instanceof Player) {
                        event.setTarget(entity);
                    }
                }
            }
        }
    }
}