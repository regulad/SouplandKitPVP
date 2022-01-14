package us.soupland.kitpvp.utilities;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.commands.*;
import us.soupland.kitpvp.commands.arena.ArenaCommand;
import us.soupland.kitpvp.commands.experience.ExperienceCommand;
import us.soupland.kitpvp.commands.menu.*;
import us.soupland.kitpvp.games.GameCommand;
import us.soupland.kitpvp.koth.commands.KothCommands;
import us.soupland.kitpvp.levelrank.LevelRankCommand;
import us.soupland.kitpvp.practice.duel.commands.AcceptCommand;
import us.soupland.kitpvp.practice.duel.commands.DuelCommand;
import us.soupland.kitpvp.sidebar.team.TeamCommand;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class KitPvPCache {

    private JavaPlugin javaPlugin;
    private CommandMap commandMap;

    public KitPvPCache() {
        javaPlugin = KitPvP.getInstance();
        registerCommand(new KitCommand());
        registerCommand(new MenuCommand());
        registerCommand(new SetStateCommand(), KitPvPUtils.PERMISSION + "setstate");
        registerCommand(new SetCuboCommand(), KitPvPUtils.PERMISSION + "setcubo");
        registerCommand(new StatsCommand());
        registerCommand(new SetSpawnCommand(), KitPvPUtils.PERMISSION + "setspawn");
        registerCommand(new SendTitleCommand(), KitPvPUtils.PERMISSION + "sendtitle");
        registerCommand(new KothCommands(), KitPvPUtils.PERMISSION + "koth");
        registerCommand(new KICommand());
        registerCommand(new CosmeticCommand());
        registerCommand(new ColorCommand());
        registerCommand(new DRCommand());
        registerCommand(new SettingsCommand());
        registerCommand(new ShopCommand());
        registerCommand(new CreditsCommand(), KitPvPUtils.PERMISSION + "credits");
        registerCommand(new TestCommand(), KitPvPUtils.PERMISSION + "test");
        registerCommand(new TeamCommand());
        registerCommand(new OneVsOneCommand());
        registerCommand(new ThemeCommand());
        registerCommand(new us.soupland.kitpvp.commands.KitPvPCommand(), KitPvPUtils.STAFF_PERMISSION);
        registerCommand(new LeaderboardCommand());
        registerCommand(new ArenaCommand(), KitPvPUtils.PERMISSION + "arena");
        registerCommand(new DuelCommand());
        registerCommand(new AcceptCommand());
        registerCommand(new TeamsCommand());
        registerCommand(new BountyHunterCommand());
        //registerCommand(new GameCommand());
        registerCommand(new ViewInvCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new HelpCommand());
        registerCommand(new EventsCommand(), KitPvPUtils.PERMISSION + "manager");
        registerCommand(new RandomSkyCommand());
        registerCommand(new ReclaimCommand());
        registerCommand(new GameCommand());
        registerCommand(new SetReclaimCommand(), KitPvPUtils.PERMISSION + "setreclaim");
        registerCommand(new ExperienceCommand(), KitPvPUtils.PERMISSION + "experience");
        registerCommand(new LevelRankCommand(), KitPvPUtils.PERMISSION + "levelrank");
    }

    private void registerCommand(KitPvPCommand axisCommand) {
        registerCommand(axisCommand, null);
    }

    private void registerCommand(KitPvPCommand axisCommand, String permission) {
        PluginCommand command = getCommand(axisCommand.getName(), javaPlugin);

        command.setPermissionMessage(ColorText.translate("&cNo permission."));

        if (permission != null) {
            command.setPermission(permission.toLowerCase());
        }

        if (axisCommand.getDescription() != null) {
            command.setDescription(axisCommand.getDescription());
        }

        command.setAliases(Arrays.asList(axisCommand.getAliases()));

        command.setExecutor(axisCommand);
        command.setTabCompleter(axisCommand);

        if (!getCommandMap().register(axisCommand.getName(), command)) {
            command.unregister(getCommandMap());
            getCommandMap().register(axisCommand.getName(), command);
        }
    }

    private CommandMap getCommandMap() {
        if (commandMap != null) {
            return commandMap;
        }

        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);

            commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
        } catch (Exception ignored) {
        }

        return commandMap;
    }

    private PluginCommand getCommand(String name, Plugin owner) {
        PluginCommand command = null;

        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            command = constructor.newInstance(name, owner);
        } catch (Exception ignored) {
        }

        return command;
    }
}