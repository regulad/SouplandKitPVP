package us.soupland.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.configuration.Config;
import us.soupland.kitpvp.utilities.location.LocationUtils;

public class RandomSkyCommand extends KitPvPCommand {

    private Config config;

    public RandomSkyCommand() {
        super("randomsky");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage("Unknown command.");
            return false;
        }
        config = new Config(KitPvP.getInstance(), "config");
        if (args.length < 1) {
            sender.sendMessage(ColorText.translate("&cUsage: /" + label + " <playerName>"));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (!KitPvPUtils.isOnline(target)) {
                sender.sendMessage(KitPvPUtils.getPlayerNotFoundMessage(args[0]));
                return false;
            }
            if (ProfileManager.getProfile(target).getPlayerState() != PlayerState.PLAYING) {
                sender.sendMessage(ColorText.translate("&c" + target.getName() + " could not be teleported while is busy."));
                return false;
            }
            if (!config.contains("SKY-LOCATIONS") || config.getStringList("SKY-LOCATIONS").isEmpty()) {
                sender.sendMessage(ColorText.translate("&cThere are no locations set."));
                return false;
            }
            Location location = LocationUtils.getLocation(config.getStringList("SKY_LOCATIONS").get(KitPvPUtils.getRandomNumber(config.getStringList("SKY_LOCATIONS").size())));
            if (location == null) {
                sender.sendMessage(ColorText.translate("&cLocation is null."));
                return false;
            }
            target.teleport(location);
            target.playSound(target.getLocation(), Sound.ENDERMAN_TELEPORT, 2, 2);
        }
        return true;
    }
}