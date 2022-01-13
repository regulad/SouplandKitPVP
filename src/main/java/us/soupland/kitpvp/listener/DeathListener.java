package us.soupland.kitpvp.listener;

import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.Achievement;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.events.PlayerGainExpEvent;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.managers.BountyManager;
import us.soupland.kitpvp.managers.KillStreakManager;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.streak.KillStreak;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;
import java.util.UUID;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();

        Player player = event.getEntity();

        Profile profile = ProfileManager.getProfile(player);

        if (profile.getPlayerState() == PlayerState.INGAME) {
            return;
        }

        Kit currentKit = profile.getCurrentKit();
        if (currentKit != null) {
            profile.getKitDeaths().put(currentKit.getName(), profile.getKitDeaths().getOrDefault(currentKit.getName(), 0) + 1);
        }

        profile.setCurrentKit(null);
        profile.getEffectsSaved().clear();

        for (Cooldown cooldown : Cooldown.getCooldownMap().values()) {
            if (cooldown.isOnCooldown(player)) {
                cooldown.remove(player);
            }
        }

        profile.incrementStat(PlayerStat.DEATHS);

        int killStreak = profile.getStat(PlayerStat.STREAK);
        if (killStreak > 0) {
            player.sendMessage(ColorText.translate("&eYour &a&lKillStreak &eof &d" + killStreak + " &ehas been reset. &c:("));
        }

        Player killer = player.getKiller();

        if (player.getKiller() == null) {
            killer = profile.getLastDamager();
        }

        if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent) player.getLastDamageCause();
            if (event1.getEntity() instanceof TNTPrimed) {
                TNTPrimed tntPrimed = (TNTPrimed) event1.getEntity();
                killer = (Player) tntPrimed.getSource();
            }
        }

        if (killer != null && killer != player) {

            Profile killerProfile = ProfileManager.getProfile(killer);
            Theme theme = killerProfile.getTheme();
            //int credits = (killer.isOp() ? 50 : killer.hasPermission(AxisUtils.DONATOR_TOP_PERMISSION) ? 45 : killer.hasPermission(AxisUtils.DONATOR_PERMISSION) ? 40 : 20);
            int credits = KitPvP.getInstance().getRankConfig().getInt("PLAYER-CREDITS.DEFAULT.CREDITS-PER-KILL");

            for (String path : KitPvP.getInstance().getRankConfig().getConfigurationSection("PLAYER-CREDITS").getKeys(false)) {
                if (path.equals("DEFAULT")) continue;
                if (killer.hasPermission(KitPvP.getInstance().getRankConfig().getString("PLAYER-CREDITS." + path + ".PERMISSION"))) {
                    credits = Math.max(credits, KitPvP.getInstance().getRankConfig().getInt("PLAYER-CREDITS." + path + ".CREDITS-PER-KILL"));
                }
            }

            if (!KitPvP.getInstance().getServerData().isDoubleCredits()) {
                credits *= 2;
            }

            if (KitPvP.getInstance().getServerData().isDoubleCredits()) {
                killer.sendMessage(ColorText.translate("&a&lDOUBLE CREDITS &eare enabled, and your credits has been multiplied by 2."));
                credits *= 2;
            }

            killerProfile.setGgMode(true);
            killerProfile.incrementStat(PlayerStat.KILLS);
            killerProfile.incrementStat(PlayerStat.STREAK);
            killerProfile.setTempKills(killerProfile.getTempKills() + 1);

            if (killerProfile.getDeathReason() != null) {
                String deathReason = killer.getName() + " &7";
                switch (killerProfile.getDeathReason()) {
                    case OBLITERATED: {
                        deathReason += "obliterated you";
                        break;
                    }
                    case DEMOLISHED: {
                        deathReason += "demolished you";
                        break;
                    }
                    case LEGENDARY_SKILLS: {
                        deathReason += "killed you with his legendary skills";
                        break;
                    }
                    case CLEVERNESS: {
                        deathReason += "slayed you with his cleverness";
                        break;
                    }
                    case NOOB_SLAYER: {
                        deathReason += "killed you with his noob slaying sword!";
                        break;
                    }
                    case TWOHUNDRED_IQ: {
                        deathReason = killer.getName() + "&7's 20 IQ resulted in you being outplayed and your subsequent death";
                        break;
                    }
                    case EZ: {
                        deathReason += "thought you were eZ";
                        break;
                    }
                    default: {
                        return;
                    }
                }
                player.sendMessage(ColorText.translate(deathReason + '!'));
            }
            int finalCredits = credits;
            for (Player online : Bukkit.getOnlinePlayers()) {
                Theme oTheme = ProfileManager.getProfile(online).getTheme();
                new ChatUtil("&c" + player.getName() + "&7[" + profile.getTempKills() + "] &ewas slain by &a" + killer.getName() + "&7[" + killerProfile.getTempKills() + ']', theme.getPrimaryColor() + "&lKill Information\n\n" + oTheme.getPrimaryColor() + "&lCurrent Kit&7: &f" + (killerProfile.getCurrentKit() == null ? "None" : killerProfile.getCurrentKit().getDisplayName()) + '\n' + oTheme.getPrimaryColor() + "&lCredits received&7: &f" + finalCredits, null).send(online);
            }
            killerProfile.upgradeExperience(PlayerGainExpEvent.Type.KILL);

            Achievement achievement = null;

            if (player.getName().equalsIgnoreCase(Bukkit.getOfflinePlayer(UUID.fromString("0285f685-c5f9-4be3-8b17-533fce184bcc")).getName())) {
                achievement = Achievement.KILL_KATSU;
                if (!killerProfile.getAchievements().contains(achievement)) {
                    killerProfile.getAchievements().add(achievement);
                    achievement.broadcast(killer);
                }
            }

            switch (killerProfile.getStat(PlayerStat.KILLS)) {
                case 8000: {
                    achievement = Achievement.SLAUGHTERER;
                    break;
                }
                case 5000: {
                    achievement = Achievement.EXECUTIONER;
                    break;
                }
                case 1000: {
                    achievement = Achievement.MURDERER;
                    break;
                }
            }

            if (achievement != null) {
                if (!killerProfile.getAchievements().contains(achievement)) {
                    achievement.broadcast(killer);
                }
            }

            switch (killerProfile.getStat(PlayerStat.STREAK)) {
                case 200: {
                    achievement = Achievement.GENERAL_KITPVP;
                    break;
                }
                case 100: {
                    achievement = Achievement.OFFICER;
                    break;
                }
                case 50: {
                    achievement = Achievement.SOLDIER;
                    break;
                }
            }

            if (achievement != null) {
                if (!killerProfile.getAchievements().contains(achievement)) {
                    achievement.broadcast(killer);
                }
            }

            if ((currentKit = killerProfile.getCurrentKit()) != null) {
                killerProfile.getKitKills().put(currentKit.getName(), killerProfile.getKitKills().getOrDefault(currentKit.getName(), 0) + 1);
            }

            for (Map.Entry<UUID, UUID> entry : BountyManager.getFaggotMap().entrySet()) {
                if (player.getUniqueId() != entry.getValue() || !BountyManager.getPriceMap().containsKey(entry.getKey())) {
                    continue;
                }
                killerProfile.setStat(PlayerStat.CREDITS, (killerProfile.getStat(PlayerStat.CREDITS) + BountyManager.getPriceMap().get(entry.getKey())));
                Bukkit.broadcastMessage(ColorText.translate("&4&l[Bounty] " + killer.getName() + " &7received &c" + BountyManager.getPriceMap().get(entry.getKey()) + " credits &7for killing " + player.getName() + "&7."));
                BountyManager.getPriceMap().remove(entry.getKey());
                BountyManager.getFaggotMap().remove(entry.getKey());
            }

            for (KillStreak streak : KillStreakManager.getStreaks()) {
                if (streak.getKillStreak() == killerProfile.getStat(PlayerStat.STREAK)) {
                    if (streak.execute(killer)) {
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            Theme theme2 = ProfileManager.getProfile(online).getTheme();
                            online.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&lStreak &7\u2503 " + killer.getName() + theme2.getSecondaryColor() + " received " + theme2.getPrimaryColor() + streak.getDisplay() + theme2.getSecondaryColor() + " for getting a " + theme2.getPrimaryColor() + streak.getKillStreak() + theme2.getSecondaryColor() + " killstreak!"));
                        }
                    }
                    break;
                }
            }
            if (killerProfile.getStat(PlayerStat.STREAK) > killerProfile.getStat(PlayerStat.HIGHEST_STREAK)) {
                killerProfile.setStat(PlayerStat.HIGHEST_STREAK, killerProfile.getStat(PlayerStat.STREAK));
                killer.sendMessage(ColorText.translate("&a&lNew record! &eYou're on killstreak of &d" + killerProfile.getStat(PlayerStat.STREAK) + "&e!"));
            }
            killerProfile.setStat(PlayerStat.CREDITS, (killerProfile.getStat(PlayerStat.CREDITS) + credits));

            killer.sendMessage(ColorText.translate(theme.getPrimaryColor() + "You have earned " + theme.getSecondaryColor() + credits + theme.getPrimaryColor() + " credits for killing " + player.getDisplayName() + theme.getPrimaryColor() + '.'));
            /*killer.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 30)));
            killer.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&l\u26A0 &c&lEnemy Killed: &7" + player.getName()));
            killer.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&l\u26A0 &7Tokens: " + theme.getPrimaryColor() + killerProfile.getCredits() + " &7(" + theme.getSecondaryColor() + credits + "&7) | Kills: " + theme.getPrimaryColor() + "&l" + killerProfile.getKills() + " &7(" + theme.getSecondaryColor() + "&l" + killerProfile.getLastKills() + "&7)"));
            killer.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 30)));

            player.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 30)));
            player.sendMessage(ColorText.translate("&a&l\u26A0 &eKeep at it! Practice makes perfect!"));
            player.sendMessage(ColorText.translate("&a&l\u26A0 &aCasual Kills &7- &a" + (profile.getKills() - 1) + " -> " + profile.getKills() + (profile.getKillStreak() > 0 ? " &a&l(" + profile.getKillStreak() + "+ Kills)" : "")));
            new ChatUtil("&a&l\u26A0 &n&lClick here to practice", "&7Click here to practice", "/onevsone").send(player);
            player.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 30)));
             */

            //killer.sendMessage(ColorText.translate("&7You received &4&l" + credits + " &7credits for killing " + AxisAPI.getRank(player).getColor() + player.getName() + "&7."));

            profile.setStat(PlayerStat.STREAK, 0);

            profile.setLastDamager(null);
        }

        TaskUtil.runTask(() -> PlayerUtils.resetPlayer(player, true, true));

    }
}