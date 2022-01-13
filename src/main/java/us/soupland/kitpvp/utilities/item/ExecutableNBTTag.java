package us.soupland.kitpvp.utilities.item;

import lombok.AllArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.Callback;

@AllArgsConstructor
public class ExecutableNBTTag extends NBTTagString {
    private Callback<Player> callback;

    public void execute(Player player) {
        callback.callback(player);
    }

    @Override
    public NBTBase clone() {
        return new ExecutableNBTTag(callback);
    }
}