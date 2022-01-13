package us.soupland.kitpvp.listener;

import com.google.common.primitives.Ints;
import org.bukkit.configuration.file.YamlConfiguration;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.managers.BountyManager;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.chat.MessageUtil;

public class ChatListener implements Listener {

    private YamlConfiguration config = KitPvP.getInstance().getConfig();

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        String[] args = event.getMessage().split(" ");
        if (event.isCancelled()) {
            return;
        }
        event.setCancelled(true);
        if (profile.isCreatingTeam()) {
            if (profile.getTeam() != null) {
                player.sendMessage(ColorText.translate("&cYou are already in a team"));
                profile.setCreatingTeam(false);
                return;
            }
            if (args[0].equalsIgnoreCase("cancel")) {
                profile.setCreatingTeam(false);
                player.sendMessage(ColorText.translate("&c&lProcess cancelled."));
            } else {
                String name = args[0];
                if (name.length() < 3) {
                    player.sendMessage(ColorText.translate("&cMinimum team name size is 3 characters!"));
                } else if (name.length() > 10) {
                    player.sendMessage(ColorText.translate("&cMaximum team name size is 10 characters!"));
                } else {
                    Team team = Team.getByName(name);
                    if (team != null) {
                        player.sendMessage(ColorText.translate("&cThat team already exists! &7(Type '&fcancel&7' to cancel)"));
                        return;
                    }
                    if (!StringUtils.isAlphanumeric(name)) {
                        player.sendMessage(ColorText.translate("&cTeam tag must be alphanumeric! &7(Type '&fcancel&7' to cancel)"));
                        return;
                    }
                    team = new Team(name, player.getUniqueId(), null);

                    profile.setTeam(team);
                    profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - 5000));
                    profile.setCreatingTeam(false);

                    Bukkit.broadcastMessage(ColorText.translate("&eTeam &9" + team.getName() + " &ehas been &acreated &eby " + player.getName() + "&e."));
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (online.equals(player)) {
                            continue;
                        }
                        new ChatUtil("&7[Click here for more Information]", null, "/team show " + team.getName()).send(online);
                    }
                }
            }
            return;
        }
        if (profile.isCreatingBounty()) {
            if (args[0].equalsIgnoreCase("cancel")) {
                profile.setCreatingBounty(false);
                player.sendMessage(ColorText.translate("&c&lProcess cancelled."));
                return;
            }
            Integer integer;
            try {
                integer = Ints.tryParse(args[0]);
            } catch (NullPointerException ignored) {
                player.sendMessage(ColorText.translate("&cInvalid amount."));
                return;
            }
            if (integer < 500) {
                player.sendMessage(ColorText.translate("&cAmount must be more than 500."));
                return;
            }
            if (profile.getStat(PlayerStat.CREDITS) < integer) {
                player.sendMessage(ColorText.translate("&cYou don't have credits enough to create a Bounty."));
                return;
            }
            Player target = Bukkit.getPlayer(BountyManager.getFaggotMap().get(player.getUniqueId()));
            profile.setCreatingBounty(false);
            if (target == null) {
                BountyManager.getFaggotMap().remove(player.getUniqueId());
                player.sendMessage(ColorText.translate("&cProcess has been cancelled because that player is not online."));
                return;
            }
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - integer));
            BountyManager.getPriceMap().put(player.getUniqueId(), integer);
            player.sendMessage(ColorText.translate("&4&l[Bounty] &cYou put &f" + integer + " &ccredits for "+ target.getName() + "&c's head."));
            target.sendMessage(ColorText.translate("&4&l[Bounty] &c" + player.getName() + " put &f" + integer + " &ccredits for your head."));
            Bukkit.broadcastMessage(ColorText.translate("&4&l[Bounty] " + player.getName() + " &cput &f" + integer + " &ccredits for " + target.getName() + "&c's head. &lKill him!"));
            return;
        }
        if (event.getMessage().startsWith("@") && profile.getTeam() != null) {
            profile.getTeam().sendMessage(ColorText.translate("&3[Team] &e" + player.getName() + "&7: &f") + event.getMessage().replaceFirst("@", ""));
            return;
        }
        if (profile.getLastMessage() != null && event.getMessage().equalsIgnoreCase(profile.getLastMessage()) && !player.isOp()) {
            player.sendMessage(ColorText.translate("&cYou may not spam the same message."));
            return;
        }
        if (profile.isGgMode() && event.getMessage().equalsIgnoreCase("gg")) {
            player.sendMessage(ColorText.translate("&d+1 coin"));
            profile.setGgMode(false);
        }
        profile.setLastMessage(event.getMessage());
        for (Player recipient : event.getRecipients()) {

            recipient.sendMessage(new MessageUtil().setVariable("{LEVEL_NAME}", profile.getLevelRank().getDisplayName()).setVariable("{TEAM_ACRONYM}",
                    (profile.getTeam() == null ? "" : "&7[" + profile.getTeam().getDisplayName() + "&7] ")).setVariable("{PLAYERNAME}", player.getName()).setVariable("{DISPLAYNAME}", player.getDisplayName())
                    .setVariable("{CHATCOLOR}", profile.getChatColor().toString())
                    .setVariable("{SPACE_TEAM}", (profile.getTeam() == null ? "" : " ")).setVariable("{MESSAGE}", event.getMessage().replace("&", "")).format(config.getString("CHAT-FORMAT")));

            //recipient.sendMessage(ColorText.translate("&7[&f" + profile.getLevelRank().getName() + "&7]&r" + (profile.getTeam() != null ? "&6[" + (profile.getTeam().equals(recipeProfile.getTeam()) ? "&a" : "&c") + profile.getTeam().getDisplayName() + "&6] " : "") + player.getDisplayName() + "&7: &f") + profile.getChatColor() + event.getMessage());
        }
        Bukkit.getConsoleSender().sendMessage(ColorText.translate((profile.getTeam() != null ? "&6[&c" + profile.getTeam().getDisplayName() + "&6] " : "") + player.getDisplayName() + "&7: &f") + profile.getChatColor() + event.getMessage());
    }
}