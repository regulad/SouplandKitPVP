package us.soupland.kitpvp.practice.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.practice.match.Match;
import us.soupland.kitpvp.practice.match.MatchState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;

public class EntityListener implements Listener {

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                Player player = (Player) event.getEntity();
                Profile profile = ProfileManager.getProfile(player);
                if (profile.getPlayerState().equals(PlayerState.INGAME)) return;

                Match match = profile.getMatch();
                if (match != null) {
                    if (!match.getLadder().isRegeneration()) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Match match = ProfileManager.getProfile(player).getMatch();

            if (match != null) {
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    match.handleDeath(player, null, false);
                    return;
                }

                if (match.getState() != MatchState.FIGHTING) {
                    event.setCancelled(true);
                    return;
                }


                if (match.getLadder().getName().toLowerCase().contains("sumo") || match.getLadder().getName().equalsIgnoreCase("Spleef")) {
                    event.setDamage(0);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = getDamager(event);

        if (attacker != null && event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            if (event.getDamager() instanceof Arrow) {
                if (event.getEntity() == attacker) {
                    return;
                }
                double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0D;
                Theme theme = ProfileManager.getProfile(attacker).getTheme();
                attacker.sendMessage(ColorText.translate(theme.getPrimaryColor() + damaged.getName() + theme.getSecondaryColor() + " is now at " + theme.getPrimaryColor() + health + theme.getSecondaryColor() + '!'));
            }

            Match match = ProfileManager.getProfile(damaged).getMatch();
            Match damagedMatch = ProfileManager.getProfile(damaged).getMatch();

            if (match != null) {


                if (match != damagedMatch) {
                    event.setCancelled(true);
                    return;
                }

                if (!match.getMatchPlayer(damaged).isAlive() || !match.getMatchPlayer(attacker).isAlive()) {
                    event.setCancelled(true);
                    return;
                }

                match.getMatchPlayer(attacker).handlePlayerHit();
                match.getMatchPlayer(damaged).resetCombo();

            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Match match = ProfileManager.getProfile(player).getMatch();
            if (match != null && match.getState() == MatchState.FIGHTING) {
                if (event.getFoodLevel() >= 20) {
                    event.setFoodLevel(20);
                    player.setSaturation(20.0f);
                } else {
                    event.setCancelled(KitPvPUtils.getRandomNumber(100) > 20);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private Player getDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                return (Player) ((Projectile) event.getDamager()).getShooter();
            }
        }

        return null;
    }
}