package us.soupland.kitpvp.practice.ladder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.practice.kit.Kit;

@AllArgsConstructor
@Getter
public abstract class Ladder {

    private String name;

    public abstract ItemStack getDisplayIcon();

    public abstract Kit getPlayerKit();

    //public abstract KnockbackProfile getKnockbackProfile();

    public abstract boolean isBuild();

    public abstract boolean isRegeneration();

}