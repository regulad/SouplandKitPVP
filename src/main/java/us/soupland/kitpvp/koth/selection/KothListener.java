package us.soupland.kitpvp.koth.selection;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public final class KothListener implements Listener {

    @Getter
    private static Map<UUID, KothSelection> selection = Maps.newHashMap();

    public static KothSelection getByPlayer(Player player) {
        return selection.get(player.getUniqueId());
    }

    @EventHandler
    final void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Action action = event.getAction();
        if (!action.name().contains("BLOCK")) {
            return;
        }

        Location location = event.getClickedBlock().getLocation();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (!item.isSimilar(KothSelection.getWand())) {
            return;
        }

        event.setCancelled(true);

        KothSelection kothSelection = selection.getOrDefault(player.getUniqueId(), new KothSelection());

        if (action.name().contains("LEFT")) {
            kothSelection.setFirst(location);
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW + "First position set.");
            player.sendMessage(" ");
        } else {
            kothSelection.setSecond(location);
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW + "Second position set.");
            player.sendMessage(" ");
        }

        selection.put(player.getUniqueId(), kothSelection);
    }

}
