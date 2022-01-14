package us.soupland.kitpvp.practice.duel.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.practice.duel.DuelRequest;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class AcceptCommand extends KitPvPCommand {

    public AcceptCommand() {
        super("accept");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() != PlayerState.SPAWNPRACTICE) {
            player.sendMessage(ColorText.translateAmpersand("&cYou cannot duel right now."));
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(ColorText.translateAmpersand("&cUsage: /" + label + " <playerName>"));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (!KitPvPUtils.isOnline(target) || !player.canSee(target)) {
                player.sendMessage(KitPvPUtils.getPlayerNotFoundMessage(args[0]));
                return false;
            }
            Profile targetProfile = ProfileManager.getProfile(target);
            if (targetProfile.getPlayerState() != PlayerState.SPAWNPRACTICE) {
                player.sendMessage(ColorText.translateAmpersand("&c" + target.getName() + " is currently busy."));
                return false;
            }
            if (!targetProfile.isPendingDuelRequest(player)) {
                player.sendMessage(ColorText.translateAmpersand("&cYou do not have a pending duel request from " + target.getName() + '.'));
                return false;
            }

            DuelRequest request = targetProfile.getSentDuelRequests().get(player.getUniqueId());
            Arena arena = request.getArena();

            if (arena.isActive()) {
                arena = ArenaHandler.getRandom(request.getLadder());
            }

            if (arena == null) {
                player.sendMessage(ColorText.translateAmpersand("&cTried to start a match but there are no available arenas."));
            } else {
                arena.setActive(true);
                Match match = new Match(null, request.getLadder(), arena, false, target, player);
                match.handleStart();
            }
        }
        return true;
    }
}