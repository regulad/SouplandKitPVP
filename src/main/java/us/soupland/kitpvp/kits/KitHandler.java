package us.soupland.kitpvp.kits;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.soupland.kitpvp.KitPvP;

import java.util.ArrayList;

public class KitHandler {
    private KitHandler() {
    }

    @Getter
    private static final ArrayList<Kit> kitList = new ArrayList<>();

    public static void registerKits(Kit... kits) {
        for (Kit kit : kits) {
            kitList.add(kit);
            Bukkit.getServer().getPluginManager().registerEvents(kit, KitPvP.getInstance());
        }
    }

    public static void saveKits() {
        kitList.forEach(Kit::save);
    }

    public static Kit getByName(String name) {
        for (Kit kit : kitList) {
            if (kit.getName().toLowerCase().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public static @Nullable Kit getRandomPermissableKit(Player player) {
        return getKitList().stream().filter(kit -> player.hasPermission(kit.getPermissionNode())).findAny().orElse(null);
    }
}