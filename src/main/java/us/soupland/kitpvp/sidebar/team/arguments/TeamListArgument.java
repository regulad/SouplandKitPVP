package us.soupland.kitpvp.sidebar.team.arguments;

import com.google.common.primitives.Ints;
import us.soupland.kitpvp.sidebar.team.Team;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamListArgument extends KitPvPArgument {

    public TeamListArgument() {
        super("list");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        int page = 1;

        Map<Team, Integer> teamMap = new HashMap<>();

        for (Team team : Team.getTeams()) {
            if (team.getOnlinePlayers().size() > 0) {
                teamMap.put(team, team.getOnlinePlayers().size());
            }
        }

        List<Team> teamList = new ArrayList<>(teamMap.keySet());
        teamList.sort((o1, o2) -> teamMap.get(o2).compareTo(teamMap.get(o1)));

        if (args.length > 1) {
            if (NumberUtils.isNumber(args[1])) {
                page = Ints.tryParse(args[1]);
            }
        }

        if (teamList.isEmpty()) {
            sender.sendMessage(ColorText.translate("&cThere are currently no teams to list."));
        } else {
            int size = Math.round(teamList.size() / 10);
            if (size < 1) {
                size = 1;
            }

            if (page > size) {
                page = size;
            }

            sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 36)));
            sender.sendMessage(ColorText.translate("&9Team List &7(Page " + page + '/' + size + ')'));
            sender.sendMessage("");
            for (int i = page * 10 - 10; i < page * 10; i++) {
                if (teamList.size() > i) {
                    Team team = teamList.get(i);
                    if (sender instanceof Player) {
                        new ChatUtil("&7" + (i + 1) + ". &e" + team.getDisplayName() + " &a(" + team.getOnlinePlayers().size() + '/' + team.getAllUuids().size() + ") &a&l[INFO]", "&7Click here for more information", "/team info " + team.getName()).send((Player) sender);
                    } else {
                        sender.sendMessage(ColorText.translate("&7" + (i + 1) + ". &e" + team.getDisplayName() + " &a(" + team.getOnlinePlayers().size() + '/' + team.getAllUuids().size() + ')'));
                    }
                }
            }
            sender.sendMessage("");
            sender.sendMessage(ColorText.translate("&7You are currently on &fPage " + page + '/' + size + "&7."));
            sender.sendMessage(ColorText.translate("&7To view other pages use &e/" + label + " list <page#>"));
            sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 36)));
        }
    }
}