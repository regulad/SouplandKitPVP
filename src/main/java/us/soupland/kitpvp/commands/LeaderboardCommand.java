package us.soupland.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.leaderboard.LeaderboardMenu;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class LeaderboardCommand extends KitPvPCommand {
    public LeaderboardCommand() {
        super("leaderboard", "Open leadeboard menu", "leaderboards", "topstats", "topkills", "statstop");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;

        LeaderboardMenu.openMenu(player);

        return true;
    }
}
