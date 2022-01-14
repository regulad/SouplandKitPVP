package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class StrafeKit extends Kit {

    public StrafeKit() {
        super("Strafe", "&5Strafe", "");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.NETHER_STAR) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translate("&cYou can't do this in Spawn."));
            return;
        }
        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translate("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 4 * 20, 3));

        cooldown.setCooldown(player);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setBoots(new ItemMaker(Material.GOLD_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.NETHER_STAR).create());

        for (int i = 0; i < 35; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200000, 1));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.GOLD_BOOTS).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 3800;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Has no special abilities,");
        list.add("&7but is equipped with Speed III.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }
}