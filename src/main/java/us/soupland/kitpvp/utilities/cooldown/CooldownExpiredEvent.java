package us.soupland.kitpvp.utilities.cooldown;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class CooldownExpiredEvent extends PlayerBase {

    private Cooldown cooldown;
    private boolean forced;

    CooldownExpiredEvent(Player player, Cooldown cooldown) {
        super(player);
        this.cooldown = cooldown;
    }

    public CustomEvent setForced(boolean forced) {
        this.forced = forced;
        return this;
    }
}
