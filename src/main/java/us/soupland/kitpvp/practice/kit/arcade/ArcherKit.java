package us.soupland.kitpvp.practice.kit.arcade;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.practice.kit.Kit;
import us.soupland.kitpvp.profile.ProfileManager;

import java.util.List;

public class ArcherKit extends Kit {

    public ArcherKit() {
        super("Archer");
    }

    @Override
    public boolean isRanked() {
        return false;
    }

    @Override
    public boolean isDuel() {
        return false;
    }

    @Override
    public void onEquip(Player player) {
        us.soupland.kitpvp.kits.Kit kit = KitHandler.getByName("Archer");
        if (kit != null) {
            kit.onLoad(player);
            ProfileManager.getProfile(player).setCurrentKit(kit);
        }
    }

    @Override
    public ItemStack getIcon() {
        return null;
    }

    @Override
    public List<String> getDescription() {
        return null;
    }
}