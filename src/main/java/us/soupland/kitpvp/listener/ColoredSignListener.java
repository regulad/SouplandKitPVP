package us.soupland.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import us.soupland.kitpvp.utilities.chat.ColorText;

public class ColoredSignListener implements Listener {

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            String[] lines = event.getLines();
            for (int i = 0; i < lines.length; i++) {
                event.setLine(i, ColorText.translate(lines[i]));
            }
        }
    }
}