package us.soupland.kitpvp.kits;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.configuration.Config;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public abstract class Kit implements Listener {

    private final @NotNull String name;
    private final @NotNull String initialDisplayName;
    private final @NotNull String cooldown; // Formatted like "10s" or "1m"
    @Setter
    private ItemStack[] playerInventory;
    @Setter
    private ItemStack[] inventory;
    @Setter
    private List<PotionEffect> effects;

    public Kit(@NotNull String name, @NotNull String initialDisplayName, @NotNull String cooldown) {
        this.name = name;
        this.initialDisplayName = initialDisplayName;
        this.cooldown = cooldown;
        this.playerInventory = new ItemStack[]{};
        this.inventory = new ItemStack[]{};
        this.effects = Lists.newArrayList();
    }

    public static @NotNull Config getKitConfig() {
        return KitPvP.getInstance().getKitConfig();
    }

    public static void saveKitConfig() {
        getKitConfig().save();
    }

    public static @NotNull ConfigurationSection getRootSection() {
        return Optional.ofNullable(getKitConfig().getConfigurationSection("Kits")).orElseGet(() -> getKitConfig().createSection("Kits"));
    }

    public @NotNull ConfigurationSection getSection() {
        return Optional.ofNullable(getRootSection().getConfigurationSection(this.getName())).orElseGet(() -> getRootSection().createSection(this.getName()));
    }

    public String getDisplayName() {
        return Optional.ofNullable(getSection().getString("name")).orElse(this.initialDisplayName);
    }

    public String getCooldown() {
        return Optional.ofNullable(getSection().getString("cooldown")).orElse(this.cooldown);
    }

    public void onInteract(PlayerInteractEvent event) {
    }

    public void onLoad(Player player) {
    }

    public @NotNull String getPermissionNode() {
        return "soupland.kit." + getName().toLowerCase();
    }

    public int getCreditCost() {
        return getSection().getInt("credits");
    }

    ;

    public List<String> getDescription() {
        return getSection().getStringList("description");
    }

    public boolean isEquipped(Player player) {
        Profile profile = ProfileManager.getProfile(player);
        Kit kit = profile.getCurrentKit();
        return getClass().isInstance(kit);
    }

    public @NotNull Material getMaterial() {
        return Material.getMaterial(getSection().getString("display-item"));
    }

    public ItemStack getItem() {
        return new ItemMaker(getMaterial()).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    public void save() {
        getSection().set("name", this.getDisplayName());
        getSection().set("display-item", this.getMaterial().name());
        getSection().set("description", this.getDescription());
        getSection().set("credits", this.getCreditCost());
        getSection().set("cooldown", this.getCooldown());

        List<String> effects = this.getEffects().stream().map(potionEffect -> potionEffect.getType().getName()).collect(Collectors.toList());
        getSection().set("effects", effects);

        saveKitConfig();
    }
}