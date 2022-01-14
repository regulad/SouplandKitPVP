package us.soupland.kitpvp.sidebar;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.koth.KothManager;
import us.soupland.kitpvp.managers.PracticeManager;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.practice.match.MatchState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.server.ServerData;
import us.soupland.kitpvp.sidebar.scoreboard.AridiAdapter;
import us.soupland.kitpvp.sidebar.scoreboard.AridiStyle;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.Utils;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.player.DurationFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class KitPvPBoard implements AridiAdapter, Listener {

    private String format(double tps) {
        return (tps > 18.0D ? ChatColor.GREEN : tps > 16.0D ? ChatColor.YELLOW : ChatColor.RED).toString() + (tps > 20.0D ? "*" : "") + Math.min(Math.round(tps * 100.0D) / 100.0D, 20.0D);
    }

    @Override
    public String getTitle(Player player) {
        Profile profile = ProfileManager.getProfile(player);
        Theme theme = profile.getTheme();
        return theme.getPrimaryColor() + "&lSoupLand &7\u2503 " + theme.getSecondaryColor() + (profile.getPlayerState().name().contains("PRACTICE") ? "1v1" : "KitPvP");
    }

    @Override
    public List<String> getLines(Player player) {
        Profile profile = ProfileManager.getProfile(player);
        Theme theme = profile.getTheme();

        if (!profile.isScoreboardEnabled()) {
            return null;
        }

        List<String> toReturn = new ArrayList<>();

        KothManager kothManager = KitPvP.getInstance().getKothManager();
        PracticeManager practiceManager = KitPvP.getInstance().getPracticeManager();
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        ServerData serverData = KitPvP.getInstance().getServerData();

        toReturn.add("&7&m" + StringUtils.repeat("-", 22));

        if (profile.getPlayerState() == PlayerState.INGAME) {
            if (gameHandler.getUpcomingGame() != null) {
                toReturn.add(theme.getPrimaryColor() + "&lEvent: " + theme.getSecondaryColor() + gameHandler.getUpcomingGame().getName());
                toReturn.add(theme.getPrimaryColor() + "&lPlayers: " + theme.getSecondaryColor() + gameHandler.getPlayers().size() + '/' + gameHandler.getUpcomingGame().getMaxPlayers());
                toReturn.add("&7&m" + StringUtils.repeat("-", 22));
                toReturn.add(theme.getSecondaryColor() + "Starting in " + theme.getPrimaryColor() + gameHandler.getStartTime() + 's');
            } else if (gameHandler.getActiveGame() != null) {
                toReturn.addAll(gameHandler.getActiveGame().getPlayerScoreboard(player));
            }
            toReturn.add("&7&m" + StringUtils.repeat("-", 22));
            return toReturn;
        }

        if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.PLAYING) {
            toReturn.add(theme.getPrimaryColor() + "&lKills: " + theme.getSecondaryColor() + profile.getStat(PlayerStat.KILLS));
            toReturn.add(theme.getPrimaryColor() + "&lDeaths: " + theme.getSecondaryColor() + profile.getStat(PlayerStat.DEATHS));
            if (profile.getStat(PlayerStat.CREDITS) > 0) {
                toReturn.add(theme.getPrimaryColor() + "&lCredits: " + theme.getSecondaryColor() + Utils.getFormat(profile.getStat(PlayerStat.CREDITS)));
            }
            toReturn.add(theme.getPrimaryColor() + "&lKillstreak: " + theme.getSecondaryColor() + profile.getStat(PlayerStat.STREAK));
            double kills = profile.getStat(PlayerStat.KILLS), deaths = profile.getStat(PlayerStat.DEATHS), kdr = kills / deaths;
            toReturn.add(theme.getPrimaryColor() + "&lK/D Ratio: " + theme.getSecondaryColor() + (kills == 0 && deaths == 0 ? "N/A" : new DecimalFormat("#.##").format(kdr)));
            /*if (profile.getHighestKillStreak() > 0) {
                toReturn.add(theme.getPrimaryColor() + "&lBest Streak: " + theme.getSecondaryColor() + profile.getHighestKillStreak());
            }*/
            Team team = profile.getTeam();
            if (team != null) {
                toReturn.add(theme.getPrimaryColor() + "&lTeam: " + theme.getSecondaryColor() + team.getDisplayName());
            }

            if (serverData.isDoubleCredits() || serverData.isFreeKitsMode() || serverData.isFreeEventsMode()) {
                toReturn.add("&7&m" + StringUtils.repeat("-", 22));
                List<String> strings = new ArrayList<>();
                if (serverData.isDoubleCredits()) {
                    strings.add(" &7- &a&lDouble Credits");
                }
                if (serverData.isFreeKitsMode()) {
                    strings.add(" &7- &b&lFree Kits Mode");
                }
                if (serverData.isFreeEventsMode()) {
                    strings.add(" &7- &d&lFree Events Mode");
                }
                toReturn.add(theme.getPrimaryColor() + "&lCurrent Event" + (strings.size() == 1 ? "" : "s") + ":");
                toReturn.addAll(strings);
            }

            toReturn.add("");
            toReturn.add("&7www.soupland.us");

            toReturn.add("&7&m" + StringUtils.repeat("-", 22));
            if (gameHandler.getUpcomingGame() != null) {
                toReturn.add(theme.getPrimaryColor() + "&lEvent &7(/join)");
                toReturn.add("&7- " + theme.getSecondaryColor() + gameHandler.getUpcomingGame().getName());
                toReturn.add("&7- " + theme.getPrimaryColor() + gameHandler.getStartTime() + 's');
                toReturn.add("&7&m" + StringUtils.repeat("-", 22));
            }
            /*if (kothManager.isActive()) {
                toReturn.add(theme.getPrimaryColor() + "&lKoTH " + theme.getSecondaryColor() + "(/ki)");
                if (kothManager.getCapper() != null) {
                    toReturn.add(" &7- &f" + kothManager.getCapper().getName());
                }
                toReturn.add(" &7- &f" + DurationFormatter.getRemaining(kothManager.getRemaining() * 1000, true).replace(".0", ""));
                toReturn.add("&7&m" + StringUtils.repeat("-", 22));
            }*/
            if (profile.getPlayerCombat() > 0L) {
                toReturn.add("&cCombat&f: " + DurationFormatter.getRemaining(profile.getPlayerCombat(), true));
            }

            for (Cooldown cooldown : Cooldown.getCooldownMap().values()) {
                if (cooldown.isOnCooldown(player) && !cooldown.getName().contains("LFF")) {
                    toReturn.add(cooldown.getDisplayName() + "&f: " + DurationFormatter.getRemaining(cooldown.getDuration(player), true));
                }
            }
        } else if (profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
            toReturn.add(theme.getPrimaryColor() + "Online: " + theme.getSecondaryColor() + practiceManager.getPlayersInSpawn());
            toReturn.add(theme.getPrimaryColor() + "Playing: " + theme.getSecondaryColor() + practiceManager.getPlayersInMatch());
            toReturn.add(theme.getPrimaryColor() + "Queueing: " + theme.getSecondaryColor() + practiceManager.getPlayersInQueue());
            toReturn.add("");
            toReturn.add(theme.getPrimaryColor() + "Global Elo: " + theme.getSecondaryColor() + profile.getStat(PlayerStat.ELO));
            toReturn.add("");
            toReturn.add("&7www.soupland.us");
            toReturn.add("&7&m" + StringUtils.repeat("-", 22));
        } else if (profile.getPlayerState() == PlayerState.FIGHTINGPRACTICE) {
            Match match = profile.getMatch();
            if (match != null) {
                toReturn.add(theme.getPrimaryColor() + "Kit: " + theme.getSecondaryColor() + match.getKit().getName());
                if (match.getState() == MatchState.STARTING) {
                    toReturn.add(theme.getPrimaryColor() + "Opponent: " + theme.getSecondaryColor() + "???");
                } else {
                    toReturn.add(theme.getPrimaryColor() + "Opponent: " + theme.getSecondaryColor() + match.getOpponent(player).getName());
                    toReturn.add("");
                    toReturn.add(theme.getPrimaryColor() + "Ping: " + theme.getSecondaryColor() + ((CraftPlayer) player).getHandle().ping + " \u2503 " + ((CraftPlayer) match.getOpponent(player)).getHandle().ping);
                    toReturn.add(theme.getPrimaryColor() + "CPS: " + theme.getSecondaryColor() + profile.getPlayerCps() + " \u2503 " + ProfileManager.getProfile(match.getOpponent(player)).getPlayerCps());
                }
                toReturn.add("");
                toReturn.add("&7www.soupland.us");
                toReturn.add("&7&m" + StringUtils.repeat("-", 22));
            }
        }
        return toReturn;
    }

    @Override
    public AridiStyle getAridiStyle(Player player) {
        return AridiStyle.MODERN;
    }

    /*@EventHandler
    public void ScoreLoadEvent(PlayerLoadScoreboardEvent event) {
        Player player = event.getPlayer();
        if (player.getScoreboard() != null) {
            Scoreboard scoreboard = player.getScoreboard();

            if (scoreboard.getObjective("namehealth") == null) {
                scoreboard.registerNewObjective("namehealth", "health");
            }

            Objective objective = scoreboard.getObjective("namehealth");

            /*if (scoreboard.getObjective("tabhealth") == null) {
                scoreboard.registerNewObjective("tabhealth", "health");
            }

            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName(ChatColor.DARK_RED + "\u2764");
        }
    }*/


    /*@EventHandler
    public void ScoreUpdateEvent(PlayerUpdateScoreboardEvent event) {
        Player player = event.getPlayer();
        me.tinchx.axis.profile.Profile profilePlayer = AxisAPI.getProfile(player);
        for (Player online : Bukkit.getOnlinePlayers()) {
            me.tinchx.axis.profile.Profile profile = AxisAPI.getProfile(online);
            /*org.bukkit.scoreboard.Team rank = getExistingOrCreateNewTeam("rank", AxisPlugin.getPlugin().getAridiManager().getAridiMap().get(online.getUniqueId()).getScoreboard(), ChatColor.getByChar(Utils.getColorPrefix(profile.getRank().getColor())
                    .replace("§", "")
                    .replace("§o", "")));
            rank.setCanSeeFriendlyInvisibles(false);
            if (!(rank.hasEntry(online.getName()))) {
                rank.addEntry(online.getName());
            }
            if (online.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                rank.removeEntry(online.getName());
            }
            NameTagHandler.addToTeam(online, player, ChatColor.getByChar(Utils.getColorPrefix(profilePlayer.getRank().getColor())
                            .replace("§", "")
                            .replace("§o", "")),
                    true);
            NameTagHandler.addToTeam(player, online, ChatColor.getByChar(Utils.getColorPrefix(profile.getRank().getColor())
                            .replace("§", "")
                            .replace("§o", "")),
                    true);
        }
    }*/
}