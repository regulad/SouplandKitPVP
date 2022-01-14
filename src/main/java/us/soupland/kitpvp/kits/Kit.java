package us.soupland.kitpvp.kits;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;

import java.util.List;
import java.util.Optional;

@Getter
public abstract class Kit implements Listener {

    private final @NotNull String name;
    private final @NotNull String displayName;
    private final @NotNull String cooldown; // Formatted like "10s" or "1m"
    @Setter
    private ItemStack[] playerInventory;
    @Setter
    private ItemStack[] inventory;
    @Setter
    private List<PotionEffect> effects;

    public Kit(@NotNull String name, @NotNull String displayName, @NotNull String cooldown) {
        this.name = name;
        this.displayName = displayName;
        this.cooldown = cooldown;
        this.playerInventory = new ItemStack[]{};
        this.inventory = new ItemStack[]{};
        this.effects = Lists.newArrayList();
    }

    public String getDisplayName() {
        return Optional.ofNullable(KitPvP.getInstance().getKitConfig().getString("Kits." + this.name + ".name")).orElse(this.displayName);
    }

    public String getCooldown() {
        return Optional.ofNullable(KitPvP.getInstance().getKitConfig().getString("Kits." + this.name + ".cooldown")).orElse(this.cooldown);
    }

    public YamlConfiguration getConfig() {
        return KitPvP.getInstance().getKitConfig();
    }

    public void execute(PlayerInteractEvent event) {
    }

    public abstract void onLoad(Player player);

    public abstract ItemStack getItem();

    public @NotNull String getPermissionNode() {
        return "soupland.kit." + getName().toLowerCase();
    }

    public abstract int getCreditCost();

    public List<String> getDescription() {
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

    public boolean isEquipped(Player player) {
        Profile profile = ProfileManager.getProfile(player);
        Kit kit = profile.getCurrentKit();
        return getClass().isInstance(kit);
    }
}