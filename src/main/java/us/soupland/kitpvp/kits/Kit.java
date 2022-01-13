package us.soupland.kitpvp.kits;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Getter
public abstract class Kit implements Listener {

    private String name, displayName, cooldown;
    @Setter
    private ItemStack[] playerInventory;
    @Setter
    private ItemStack[] inventory;
    @Setter
    private List<PotionEffect> effects;

    public Kit(String name, String displayName, String cooldown) {
        this.name = name;
        this.displayName = displayName;
        this.cooldown = cooldown;
        this.playerInventory = new ItemStack[]{};
        this.inventory = new ItemStack[]{};
        this.effects = Lists.newArrayList();
    }

    public String getDisplayName() {
        return KitPvP.getInstance().getKitConfig().getString("Kits." + this.name + ".name");
    }

    public String getCooldown() {
        return KitPvP.getInstance().getKitConfig().getString("Kits." + this.name + ".cooldown");
    }

    public YamlConfiguration getConfig() {
        return KitPvP.getInstance().getKitConfig();
    }

    public abstract void execute(PlayerInteractEvent event);

    public abstract void onLoad(Player player);

    public abstract ItemStack getItem();

    public abstract String getPermissions();

    public abstract int getCredits();

    public abstract List<String> getDescription();

    public boolean isEquped(Player player) {
        Profile profile = ProfileManager.getProfile(player);
        if (profile != null) {
            Kit kit = profile.getCurrentKit();
            if (kit != null) {
                String name = kit.getName();
                if (name != null) {
                    return name.equals(this.getName());
                }
            }
        }
        return false;
    }
}