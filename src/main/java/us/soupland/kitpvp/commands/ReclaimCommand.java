package us.soupland.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

import java.util.Objects;

public class ReclaimCommand extends KitPvPCommand {

    public ReclaimCommand() {
        super("reclaim", null, "claim");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        if (profile.isReclaimed()) {
            player.sendMessage(ColorText.translateAmpersand("&7You have already claimed your perks."));
            return false;
        }
        if (player.hasPermission("Reclaim.1")) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + 1000));
            Kit kit = KitHandler.getRandomPermissableKit(player);
            if (kit != null) {
                Objects.requireNonNull(KitPvP.getInstance().getPermission()).playerAdd(player, kit.getPermissionNode());
                player.sendMessage(ColorText.translateAmpersand("&4&l[RECLAIM] &7You just unlocked a new &aKit&7. &c(" + kit.getName() + ')'));
            }
        } else if (player.hasPermission("Reclaim.2")) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + 2000));
            for (int i = 0; i < 2; i++) {
                Kit kit = KitHandler.getRandomPermissableKit(player);
                if (kit != null) {
                    Objects.requireNonNull(KitPvP.getInstance().getPermission()).playerAdd(player, kit.getPermissionNode());
                    player.sendMessage(ColorText.translateAmpersand("&4&l[RECLAIM] &7You just unlocked a new &aKit&7. &c(" + kit.getName() + ')'));
                }
            }
        } else if (player.hasPermission("Reclaim.3")) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + 3000));
            for (int i = 0; i < 3; i++) {
                Kit kit = KitHandler.getRandomPermissableKit(player);
                if (kit != null) {
                    Objects.requireNonNull(KitPvP.getInstance().getPermission()).playerAdd(player, kit.getPermissionNode());
                    player.sendMessage(ColorText.translateAmpersand("&4&l[RECLAIM] &7You just unlocked a new &aKit&7. &c(" + kit.getName() + ')'));
                }
            }
        } else if (player.hasPermission("Reclaim.4")) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + 4000));
            for (int i = 0; i < 4; i++) {
                Kit kit = KitHandler.getRandomPermissableKit(player);
                if (kit != null) {
                    Objects.requireNonNull(KitPvP.getInstance().getPermission()).playerAdd(player, kit.getPermissionNode());
                    player.sendMessage(ColorText.translateAmpersand("&4&l[RECLAIM] &7You just unlocked a new &aKit&7. &c(" + kit.getName() + ')'));
                }
            }
        } else if (player.hasPermission("Reclaim.5")) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + 5000));
            for (int i = 0; i < 5; i++) {
                Kit kit = KitHandler.getRandomPermissableKit(player);
                if (kit != null) {
                    Objects.requireNonNull(KitPvP.getInstance().getPermission()).playerAdd(player, kit.getPermissionNode());
                    player.sendMessage(ColorText.translateAmpersand("&4&l[RECLAIM] &7You just unlocked a new &aKit&7. &c(" + kit.getName() + ')'));
                }
            }
        } else if (player.hasPermission("Reclaim.6")) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + 6000));
            for (int i = 0; i < 6; i++) {
                Kit kit = KitHandler.getRandomPermissableKit(player);
                if (kit != null) {
                    Objects.requireNonNull(KitPvP.getInstance().getPermission()).playerAdd(player, kit.getPermissionNode());
                    player.sendMessage(ColorText.translateAmpersand("&4&l[RECLAIM] &7You just unlocked a new &aKit&7. &c(" + kit.getName() + ')'));
                }
            }
        } else if (player.hasPermission("Reclaim.7")) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + 7000));
            for (int i = 0; i < 7; i++) {
                Kit kit = KitHandler.getRandomPermissableKit(player);
                if (kit != null) {
                    Objects.requireNonNull(KitPvP.getInstance().getPermission()).playerAdd(player, kit.getPermissionNode());
                    player.sendMessage(ColorText.translateAmpersand("&4&l[RECLAIM] &7You just unlocked a new &aKit&7. &c(" + kit.getName() + ')'));
                }
            }
        } else {
            player.sendMessage(ColorText.translateAmpersand("&cYou don't have perks to claim."));
            return false;
        }
        profile.setReclaimed(true);
        Bukkit.broadcastMessage(ColorText.translateAmpersand("&4&l[RECLAIM] " + player.getDisplayName() + " &7just claimed their &fPERKS &7using &c/claim&7."));
        return true;
    }
}