package us.soupland.kitpvp.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.cooldown.PlayerBase;

@Getter
public class PlayerGainExpEvent extends PlayerBase {

    private Type type;
    private int amount;

    public PlayerGainExpEvent(Player player, Type type, int amount) {
        super(player);
        this.type = type;
        this.amount = amount;
    }

    public enum Type {
        KILL,
        EVENT,
        ACHIEVEMENT,
        KOTH
    }
}
