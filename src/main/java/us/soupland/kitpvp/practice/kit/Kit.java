package us.soupland.kitpvp.practice.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@Getter
public abstract class Kit {
    private String name;

    public abstract boolean isRanked();

    public abstract boolean isDuel();

    public abstract void onEquip(Player player);

    public abstract ItemStack getIcon();

    public abstract List<String> getDescription();
}