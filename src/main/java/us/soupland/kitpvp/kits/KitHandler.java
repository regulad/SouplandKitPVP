package us.soupland.kitpvp.kits;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.KitPvPUtils;

import java.util.ArrayList;
import java.util.List;

public class KitHandler {

    @Getter
    private static List<Kit> kitList = new ArrayList<>();

    public static void registerKits(Kit... kits) {
        for (Kit kit : kits) {
            kitList.add(kit);
            Bukkit.getServer().getPluginManager().registerEvents(kit, KitPvP.getInstance());
        }
    }

    public static Kit getByName(String name) {
        for (Kit kit : kitList) {
            if (kit.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase().replace(" ", ""))) {
                return kit;
            }
        }
        return null;
    }

    public static Kit getRandomKit(Player player) {
        List<Kit> kits = new ArrayList<>();
        Kit kit = getKitList().get(KitPvPUtils.getRandomNumber(getKitList().size()));
        if (kit.getPermissionNode() != null && !player.hasPermission(kit.getPermissionNode())) {
            kits.add(kit);
        }
        if (kits.isEmpty()) {
            return null;
        }
        return kits.get(KitPvPUtils.getRandomNumber(kits.size()));
    }
}