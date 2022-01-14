package us.soupland.kitpvp.practice.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.practice.match.MatchState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        event.setRespawnLocation(player.getLocation());

        Match match = ProfileManager.getProfile(player).getMatch();
        if (match != null) {
            match.handleRespawn(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Match match = ProfileManager.getProfile(player).getMatch();

        if (match != null) {

            if (match.getLadder().isBuild() && match.getState() == MatchState.FIGHTING) {

                if (match.getLadder().getName().equalsIgnoreCase("Spleef")) {
                    event.setCancelled(true);
                    return;
                }

                Arena arena = match.getArena();
                int x = (int) event.getBlockPlaced().getLocation().getX();
                int y = (int) event.getBlockPlaced().getLocation().getY();
                int z = (int) event.getBlockPlaced().getLocation().getZ();

                if (y > arena.getMaxBuildHeight()) {
                    player.sendMessage(ColorText.translateAmpersand("&cYou have reached the maximum build height."));
                    event.setCancelled(true);
                    return;
                }

                if (x >= arena.getFirstPosition().getBlockX() && x <= arena.getSecondPosition().getBlockX() && y >= arena.getFirstPosition().getBlockY() && y <= arena.getSecondPosition().getBlockY() &&
                        z >= arena.getFirstPosition().getBlockZ() && z <= arena.getSecondPosition().getBlockZ()) {
                    match.getPlacedBlocks().add(event.getBlock().getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            if (player.getGameMode() != GameMode.CREATIVE || !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Match match = ProfileManager.getProfile(player).getMatch();

        if (match != null) {

            if (match.getLadder().getName().toLowerCase().contains("build") && match.getState() == MatchState.FIGHTING) {
                Arena arena = match.getArena();
                Block block = event.getBlockClicked().getRelative(event.getBlockFace());
                int x = (int) block.getLocation().getX();
                int y = (int) block.getLocation().getY();
                int z = (int) block.getLocation().getZ();

                if (y > arena.getMaxBuildHeight()) {
                    player.sendMessage(ColorText.translateAmpersand("&cYou have reached the maximum build height."));
                    event.setCancelled(true);
                    return;
                }

                if (x >= arena.getFirstPosition().getBlockX() && x <= arena.getSecondPosition().getBlockX() && y >= arena.getFirstPosition().getBlockY() && y <= arena.getSecondPosition().getBlockY() &&
                        z >= arena.getFirstPosition().getBlockZ() && z <= arena.getSecondPosition().getBlockZ()) {
                    match.getPlacedBlocks().add(block.getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            if (player.getGameMode() != GameMode.CREATIVE || !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Match match = ProfileManager.getProfile(player).getMatch();

        if (match != null) {

            if (match.getState() == MatchState.FIGHTING) {

                if (match.getLadder().getName().equalsIgnoreCase("Spleef")) {
                    if (event.getBlock().getType() == Material.SNOW_BLOCK ||
                            event.getBlock().getType() == Material.SNOW) {
                        match.getChangedBlocks().add(event.getBlock().getState());

                        event.getBlock().setType(Material.AIR);
                        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
                        player.updateInventory();
                    } else {
                        event.setCancelled(true);
                    }
                } else if (!match.getPlacedBlocks().remove(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            if (player.getGameMode() != GameMode.CREATIVE || !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (ProfileManager.getProfile(player).getPlayerState() != PlayerState.FIGHTINGPRACTICE) {
            return;
        }
        Location location = player.getLocation();
        event.getDrops().clear();
        TaskUtil.runTaskLater(() -> {
            player.spigot().respawn();
            player.teleport(location);

            Match match = ProfileManager.getProfile(player).getMatch();

            if (match != null) {

                match.handleDeath(event.getEntity(), event.getEntity().getKiller(), false);
            }
        }, 2L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Match match = ProfileManager.getProfile(event.getPlayer()).getMatch();
        if (match != null) {
            match.handleDeath(event.getPlayer(), null, true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Profile profile = ProfileManager.getProfile(event.getPlayer());
        if (event.getAction() == Action.LEFT_CLICK_AIR && (profile.getPlayerState() == PlayerState.FIGHTINGPRACTICE || profile.getPlayerState() == PlayerState.INGAME)) {
            long checkLong = profile.getPlayerCps();
            checkLong++;
            profile.setPlayerCps(checkLong);
            TaskUtil.runTaskLater(() -> profile.setPlayerCps(profile.getPlayerCps() - 1), 20L);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Match match = ProfileManager.getProfile(event.getPlayer()).getMatch();

        if (match != null) {
            if (event.getItemDrop().getItemStack().getType().name().contains("SWORD") || event.getItemDrop().getItemStack().getType() == Material.MUSHROOM_SOUP) {
                event.setCancelled(true);
                return;
            }
            TaskUtil.runTaskLater(() -> event.getItemDrop().remove(), 2L);
        }
    }
}