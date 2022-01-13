package us.soupland.kitpvp.utilities.command;

import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitPvPCommand implements CommandExecutor, TabCompleter {

    public KitPvP kitPvP = KitPvP.getInstance();

    private String name, description;
    private String[] aliases;

    @Getter
    private List<KitPvPArgument> kitPvPArguments = new ArrayList<>();

    public KitPvPCommand(String name) {
        this(name, null);
    }

    public KitPvPCommand(String name, String description) {
        this(name, description, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public KitPvPCommand(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 50)));
            sender.sendMessage(ColorText.translate("&cAvailable sub-command(s) for '&7" + command.getName() + "&c'."));
            sender.sendMessage("");

            for (KitPvPArgument kitPvPArgument : kitPvPArguments) {
                if (kitPvPArgument.permission != null && !sender.hasPermission(kitPvPArgument.permission)) {
                    continue;
                }
                sender.sendMessage(ColorText.translate(" &e" + kitPvPArgument.getUsage(label) + (kitPvPArgument.description != null ? " &7- &f" + kitPvPArgument.description : "")));
            }
            sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 50)));
        } else {
            KitPvPArgument kitPvPArgument = getArgument(args[0]);
            if (kitPvPArgument == null || (kitPvPArgument.permission != null && !sender.hasPermission(kitPvPArgument.permission))) {
                sender.sendMessage(ColorText.translate("&cNo argument found."));
            } else {
                if (kitPvPArgument.onlyplayers && sender instanceof ConsoleCommandSender) {
                    Bukkit.getConsoleSender().sendMessage(KitPvPUtils.ONLY_PLAYERS);
                    return false;
                }
                kitPvPArgument.onExecute(sender, label, args);
            }
        }
        return true;
    }

    public KitPvPArgument getArgument(String name) {
        for (KitPvPArgument kitPvPArgument : kitPvPArguments) {
            if (kitPvPArgument.name.equalsIgnoreCase(name) || Arrays.asList(kitPvPArgument.aliases).contains(name.toLowerCase())) {
                return kitPvPArgument;
            }
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length < 2) {

            for (KitPvPArgument kitPvPArgument : kitPvPArguments) {
                String permission = kitPvPArgument.permission;
                if (permission == null || sender.hasPermission(permission)) {
                    results.add(kitPvPArgument.name);
                }
            }

            if (results.isEmpty()) {
                return null;
            }
        } else {
            KitPvPArgument kitPvPArgument = getArgument(args[0]);
            if (kitPvPArgument == null) {
                return results;
            }

            String permission = kitPvPArgument.permission;
            if (permission == null || sender.hasPermission(permission)) {
                results = kitPvPArgument.onTabComplete(sender, label, args);

                if (results == null) {
                    return null;
                }
            }
        }

        return KitPvPUtils.getCompletions(args, results);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void registerArgument(KitPvPArgument kitPvPArgument) {
        this.kitPvPArguments.add(kitPvPArgument);
    }

}