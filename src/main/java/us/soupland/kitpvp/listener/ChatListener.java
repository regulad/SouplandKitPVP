package us.soupland.kitpvp.listener;

import com.google.common.primitives.Ints;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.managers.BountyManager;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.chat.FormatBuilder;

import java.util.Objects;

public class ChatListener implements Listener {
    private final YamlConfiguration config = KitPvP.getInstance().getConfig();

    @EventHandler // We don't need to ignore cancelled here.
    public void formatChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        final @NotNull FormatBuilder builder = new FormatBuilder()
                .setVariable("{LEVEL_NAME}", profile.getLevelRank().getDisplayName())
                .setVariable("{TEAM_ACRONYM}", (profile.getTeam() == null ? "" : "&7[" + profile.getTeam().getDisplayName() + "&7] "))
                .setVariable("{PLAYERNAME}", "%s")
                .setVariable("{CHATCOLOR}", profile.getChatColor().toString())
                .setVariable("{SPACE_TEAM}", (profile.getTeam() == null ? "" : " "))
                .setVariable("{MESSAGE}", "%s");

        final @NotNull String formatString = builder.format(config.getString("CHAT-FORMAT"));

        event.setFormat(formatString);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void setLastMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        profile.setLastMessage(event.getMessage());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        String[] args = event.getMessage().split(" ");
        if (profile.isCreatingTeam()) {
            event.setCancelled(true);
            if (profile.getTeam() != null) {
                player.sendMessage(ColorText.translateAmpersand("&cYou are already in a team"));
                profile.setCreatingTeam(false);
                return;
            }
            if (args[0].equalsIgnoreCase("cancel")) {
                profile.setCreatingTeam(false);
                player.sendMessage(ColorText.translateAmpersand("&c&lProcess cancelled."));
            } else {
                String name = args[0];
                if (name.length() < 3) {
                    player.sendMessage(ColorText.translateAmpersand("&cMinimum team name size is 3 characters!"));
                } else if (name.length() > 10) {
                    player.sendMessage(ColorText.translateAmpersand("&cMaximum team name size is 10 characters!"));
                } else {
                    final @Nullable Team maybeTeam = Team.getByName(name);
                    if (maybeTeam != null) {
                        player.sendMessage(ColorText.translateAmpersand("&cThat team already exists! &7(Type '&fcancel&7' to cancel)"));
                    } else if (!StringUtils.isAlphanumeric(name)) {
                        player.sendMessage(ColorText.translateAmpersand("&cTeam tag must be alphanumeric! &7(Type '&fcancel&7' to cancel)"));
                    } else {
                        final @NotNull Team newTeam = new Team(name, player.getUniqueId(), null);

                        profile.setTeam(newTeam);
                        profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - 5000));
                        profile.setCreatingTeam(false);

                        Bukkit.broadcastMessage(ColorText.translateAmpersand("&eTeam &9" + newTeam.getName() + " &ehas been &acreated &eby " + player.getName() + "&e."));

                        Bukkit.getOnlinePlayers().stream().filter(online -> !online.equals(player)).forEach(online -> {
                            new ChatUtil("&7[Click here for more Information]", null, "/team show " + newTeam.getName()).send(online);
                        });
                    }
                }
            }
        } else if (profile.isCreatingBounty()) {
            event.setCancelled(true);
            if (args[0].equalsIgnoreCase("cancel")) {
                profile.setCreatingBounty(false);
                player.sendMessage(ColorText.translateAmpersand("&c&lProcess cancelled."));
                return;
            }
            Integer integer;
            try {
                integer = Ints.tryParse(args[0]);
                Objects.requireNonNull(integer);
            } catch (NullPointerException ignored) {
                player.sendMessage(ColorText.translateAmpersand("&cInvalid amount."));
                return;
            }
            if (integer < 500) {
                player.sendMessage(ColorText.translateAmpersand("&cAmount must be more than 500."));
            } else if (profile.getStat(PlayerStat.CREDITS) < integer) {
                player.sendMessage(ColorText.translateAmpersand("&cYou don't have credits enough to create a Bounty."));
            } else {
                Player target = Bukkit.getPlayer(BountyManager.getHunterHunted().get(player.getUniqueId()));
                profile.setCreatingBounty(false);
                if (target == null) {
                    BountyManager.getHunterHunted().remove(player.getUniqueId());
                    player.sendMessage(ColorText.translateAmpersand("&cProcess has been cancelled because that player is not online."));
                }
                profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - integer));
                BountyManager.getPriceMap().put(player.getUniqueId(), integer);
                player.sendMessage(ColorText.translateAmpersand("&4&l[Bounty] &cYou put &f" + integer + " &ccredits for " + target.getName() + "&c's head."));
                target.sendMessage(ColorText.translateAmpersand("&4&l[Bounty] &c" + player.getName() + " put &f" + integer + " &ccredits for your head."));
                Bukkit.broadcastMessage(ColorText.translateAmpersand("&4&l[Bounty] " + player.getName() + " &cput &f" + integer + " &ccredits for " + target.getName() + "&c's head. &lKill him!"));
            }
        } else if (event.getMessage().startsWith("@") && profile.getTeam() != null) {
            profile.getTeam().sendMessage(ColorText.translateAmpersand("&3[Team] &e" + player.getName() + "&7: &f") + event.getMessage().replaceFirst("@", ""));
            event.setCancelled(true);
        } else if (profile.isGgMode() && event.getMessage().equalsIgnoreCase("gg")) {
            player.sendMessage(ColorText.translateAmpersand("&d+1 coin"));
            profile.setGgMode(false);
        }
    }
}