package us.soupland.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

import java.util.ArrayList;
import java.util.List;

public class SetStateCommand extends KitPvPCommand {

    public SetStateCommand() {
        super("setstate");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(ColorText.translate("&cUsage: /" + label + " <state>"));
        } else {
            Profile profile = ProfileManager.getProfile(player);
            try {
                PlayerState state = profile.getPlayerState();
                profile.setPlayerState(PlayerState.valueOf(args[0].toUpperCase()));
                player.sendMessage(ColorText.translate("&c" + state.name() + " &7-> &a" + args[0].toUpperCase()));
            } catch (Exception ignored) {
                player.sendMessage(ColorText.translate("&cAn error occurred!"));
                player.sendMessage(ColorText.translate("&cUsage: /" + label + " <state>"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> toReturn = new ArrayList<>();
        if (args.length == 1) {
            for (PlayerState state : PlayerState.values()) {
                toReturn.add(state.name());
            }
        }
        return KitPvPUtils.getCompletions(args, toReturn);
    }
}