package us.soupland.kitpvp.kits.types;


import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.utilities.time.TimeUtils;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class WitherKit extends Kit {

    public WitherKit() {
        super("Wither", "&cWither", "35s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.getMaterial(289)) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translate("&cYou can't do this in Spawn."));
            return;
        }
        if (player.getNearbyEntities(20, 20, 20).isEmpty()) {
            player.sendMessage(ColorText.translate("&cThere are no players in a 20 block radius to target."));
            return;
        }

        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.getDuration(player) > 0L) {
            player.sendMessage(ColorText.translate("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player);

        for (int i = 0; i < 3; i++) {
            Skeleton wither = (Skeleton) player.getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);
            wither.setSkeletonType(Skeleton.SkeletonType.WITHER);
            wither.getEquipment().setHelmet(new ItemMaker(Material.SKULL_ITEM).setDurability(1).create());
            wither.getEquipment().setChestplate(new ItemMaker(Material.LEATHER_CHESTPLATE).setColor(Color.BLACK).create());
            wither.getEquipment().setItemInHand(new ItemStack(Material.WOOD_SWORD));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 3));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200000, 3));
            wither.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200000, 0));
            wither.setCanPickupItems(false);
            wither.setMaxHealth(2000);
            wither.setHealth(2000);
            wither.setCustomName(ColorText.translate(player.getName()));
            wither.setCustomNameVisible(true);
            wither.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(KitPvP.getInstance(), "faggotWither"));
        }

        World world = player.getWorld();
        String uuid = player.getUniqueId().toString();

        TaskUtil.runTaskLater(() -> {
            for (Entity entity : world.getEntities()) {
                if (entity.hasMetadata(uuid)) {
                    entity.remove();
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
        meta.setOwner("MHF_Wither");
        stack.setItemMeta(meta);

        inventory.setHelmet(stack);
        inventory.setChestplate(new ItemMaker(Material.CHAINMAIL_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.DIAMOND_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.IRON_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.SKULL_ITEM).setDurability(1).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 0));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.SKULL_ITEM).setDurability(1).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 3000;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Surround your enemies with");
        list.add("&7strong Wither Skeletons.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager().hasMetadata(event.getEntity().getUniqueId().toString())) {
                event.setCancelled(true);
            }
        }
    }
}