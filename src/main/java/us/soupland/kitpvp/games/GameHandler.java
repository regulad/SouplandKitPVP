package us.soupland.kitpvp.games;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerItem;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.games.events.GameStartedEvent;
import us.soupland.kitpvp.games.events.PlayerJoinGameEvent;
import us.soupland.kitpvp.games.types.*;
import us.soupland.kitpvp.listener.handler.ListenerHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.*;

@Getter
public class GameHandler {

    @Getter
    private static String prefix;
    @Setter
    private Game activeGame, upcomingGame;
    private List<Game> games = new ArrayList<>();
    private List<Player> players, spectators;
    private int startTime;
    private long eventCooldown;
    private HashMap<Game, Long> cooldown;
    private Player host;

    private List<Team> teams;
    private Set<GamePlayerInvite> invites;
    private ItemStack ITEM;

    public GameHandler() {
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.cooldown = new HashMap<>();
        this.invites = new HashSet<>();
        this.teams = new ArrayList<>();
        prefix = ColorText.translate("&4&l[Event] &f");
        this.activeGame = null;
        ITEM = PlayerItem.SPAWN_EVENT_ITEM.getItem();

        this.loadClasses();
    }

    public GamePlayerInvite[] getAllPlayerInvites(UUID target) {
        return invites.stream().filter(i -> i.getTarget() == target && i.isValid()).toArray(GamePlayerInvite[]::new);
    }

    private void acceptInvitation(GamePlayerInvite acceptedInvite) {
        for (GamePlayerInvite invite : invites) {
            if (invite.getSender().equals(acceptedInvite.getSender()) && invite.getTarget().equals(acceptedInvite.getTarget())) {
                Bukkit.getPlayer(acceptedInvite.getSender()).sendMessage(ColorText.translate(getPrefix() + "&7You have already requested to partner to this player."));
                return;
            }
        }
        Player senderPlayer = Bukkit.getPlayer(acceptedInvite.getSender()), targetPlayer = Bukkit.getPlayer(acceptedInvite.getTarget());
        Team team = getByPlayer(senderPlayer);

        if (team != null) {
            senderPlayer.sendMessage(ColorText.translate(getPrefix() + "&7You are already in a team."));
            return;
        }

        Team team2 = getByPlayer(targetPlayer);


        if (team2 != null) {
            senderPlayer.sendMessage(ColorText.translate("&cThat player is already in a team."));
            return;
        }

        senderPlayer.sendMessage(ColorText.translate(getPrefix() + targetPlayer.getName() + " &7has accepted your partner request."));
        targetPlayer.sendMessage(ColorText.translate(getPrefix() + "&7You have accepted " + senderPlayer.getName() + '\'' + (senderPlayer.getName().endsWith("s") ? "" : 's') + " &7partner request."));

        this.teams.add(Team.registerTeam(Arrays.asList(senderPlayer, targetPlayer), "$team"));
    }

    public boolean hasPlayerInvite(UUID sender, UUID target) {
        return (getPlayerInvite(sender, target) != null);
    }

    public void registerInvitation(GamePlayerInvite newInvite) {
        Iterator<GamePlayerInvite> inviteIterator = invites.iterator();

        while (inviteIterator.hasNext()) {
            GamePlayerInvite invite = inviteIterator.next();

            if (invite.getSender().equals(newInvite.getSender()) && invite.getTarget().equals(newInvite.getTarget())) {
                inviteIterator.remove();
            }
        }

        inviteIterator = invites.iterator();

        while (inviteIterator.hasNext()) {
            GamePlayerInvite invite = inviteIterator.next();

            if (invite.getSender().equals(newInvite.getTarget()) && invite.getTarget().equals(newInvite.getSender())) {
                inviteIterator.remove();
                acceptInvitation(invite);
                return;
            }
        }

        Player senderPlayer = Bukkit.getPlayer(newInvite.getSender()), targetPlayer = Bukkit.getPlayer(newInvite.getTarget());

        senderPlayer.sendMessage(ColorText.translate(getPrefix() + "&7You have sent a partner request to " + targetPlayer.getName() + "&7."));
        new ChatUtil(getPrefix() + senderPlayer.getName() + " &7has sent you a partner request. &a(Click to accept)", "&aClick to accept request", "/game partner " + senderPlayer.getName()).send(targetPlayer);

        invites.add(newInvite);
    }

    private GamePlayerInvite getPlayerInvite(UUID sender, UUID target) {
        for (GamePlayerInvite invite : invites) {
            if ((sender == null || invite.getSender().equals(sender)) && invite.getTarget().equals(target) && invite.isValid()) {
                return (invite);
            }
        }

        return (null);
    }

    public void addSpectator(Player player) {
        if (!spectators.contains(player)) {
            spectators.add(player);
        }

        for (Player gay : getPlayers()) {
            if (gay == null) {
                continue;
            }
            if (activeGame.getPlayers(GamePlayerState.ALIVE).contains(gay) || activeGame.getPlayers(GamePlayerState.DEAD).contains(gay)) {
                gay.hidePlayer(player);
            }
        }

        if (this.activeGame != null) {
            this.activeGame.spectator(player);
            this.activeGame.getPlayers().put(player, GamePlayerState.SPECTATING);
        } else {
            player.teleport(this.chooseEventSpawnLocation());
        }
    }

    public void removeSpectator(Player player) {
        if (!spectators.contains(player)) {
            return;
        }
        getSpectators().remove(player);
        PlayerUtils.resetPlayer(player, false, true);
        player.setAllowFlight(false);
        player.setFlying(false);
    }

    private void startTimer(int seconds) {
        this.startTime = seconds;
        new BukkitRunnable() {
            public void run() {
                if (upcomingGame == null) {
                    cancel();
                    return;
                }
                if (startTime <= 0) {
                    setup();
                    cancel();
                    return;
                }
                switch (startTime) {
                    case 5 * 60:
                    case 4 * 60:
                    case 3 * 60:
                    case 2 * 60:
                    case 60:
                    case 30:
                    case 20:
                    case 15:
                    case 10:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            Theme theme = ProfileManager.getProfile(player).getTheme();
                            new ChatUtil(theme.getPrimaryColor() + "&l[Event] &f" + upcomingGame.getName() + ' ' + theme.getSecondaryColor() + "will be starting in " + theme.getPrimaryColor() + DurationFormatUtils.formatDurationWords((startTime * 1000), true, true) + theme.getSecondaryColor() + ". &7(Click to join)", "&7Click here!", "/game join").send(player);
                        }
                        break;
                    default:
                        break;
                }

                startTime--;
            }

        }.runTaskTimer(KitPvP.getInstance(), 20L, 20L);
    }

    public void join(Player player) {
        if (this.getPlayers().contains(player)) return;

        if (!new PlayerJoinGameEvent(player, upcomingGame).call()) {
            return;
        }

        this.getPlayers().add(player);
        player.teleport(this.chooseEventSpawnLocation());
        broadcast(GameHandler.getPrefix() + ChatColor.WHITE + player.getDisplayName() + ChatColor.YELLOW + " has joined the events." + ChatColor.GRAY + " (" + KitPvP.getInstance().getGameHandler().getPlayers().size() + "/" + this.upcomingGame.getMaxPlayers() + ")");
        Profile profile = ProfileManager.getProfile(player);
        profile.setPlayerState(PlayerState.INGAME);
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        inventory.setItem(7, new ItemMaker(Material.PAPER).setDisplayname(profile.getTheme().getPrimaryColor() + "&lEvent Information").addLore("", "&7Event: &f" + upcomingGame.getName(), "&7Reward: &f" + upcomingGame.getReward() + " Credits", "&7Hoster: " + (host == null ? "&c[Disconnected]" : host.getName()), "", "&d" + upcomingGame.getDescription(), "").setInteractRight(player1 -> player.sendMessage(ColorText.translate("&7You are on a " + upcomingGame.getName() + " Event!"))).create());
        inventory.setItem(8, PlayerItem.EVENT_LEAVE_ITEM.getItem());
        player.updateInventory();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }


    public void leave(Player player) {
        this.getPlayers().remove(player);
        if (!spectators.contains(player)) {
            removeSpectator(player);
        }

        Team team = this.getByPlayer(player);
        if (team != null) {
            team.sendMessage(ColorText.translate(getPrefix() + "&cYour team was disbanded as " + player.getName() + " &cleft."));
            this.teams.remove(team);
        }
        if (activeGame != null) {
            activeGame.eliminate(player);
            activeGame.getPlayers().remove(player);
        }

        PlayerUtils.resetPlayer(player, false, true);
    }

    private Location chooseEventSpawnLocation() {
        return KitPvP.getInstance().getServerData().getSpawnEventsLocation();
    }

    private Team getByPlayer(Player player) {
        for (Team team : teams) {
            if (team.getPlayers().containsKey(player)) {
                return team;
            }
        }
        return null;
    }

    public void startGame(Game game, int startTime, Player player) {
        this.upcomingGame = game;
        this.startTimer(startTime);
        this.startTime = startTime;
        this.host = player;
        players.clear();
        this.teams.clear();
        this.invites.clear();
        getPlayers().clear();
        for (Player online : Bukkit.getOnlinePlayers()) {
            Profile profile = ProfileManager.getProfile(online);
            if (profile.getPlayerState() == PlayerState.SPAWN && profile.getCurrentKit() == null) {
                online.getInventory().setItem(3, ITEM);
                online.updateInventory();
            }
        }
    }

    public void setup() {
        this.activeGame = this.upcomingGame;
        this.upcomingGame = null;
        this.host = null;

        if (getPlayers().size() <= /*(activeGame instanceof TvTSumoGame ? 2 : 1)*/1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Theme theme = ProfileManager.getProfile(player).getTheme();
                player.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 12) + "&r " + theme.getPrimaryColor() + "&lEvent &7&m" + StringUtils.repeat("-", 12)));
                player.sendMessage(" ");
                player.sendMessage(ColorText.translate(theme.getSecondaryColor() + "The events did not reach its minimum"));
                player.sendMessage(ColorText.translate(theme.getSecondaryColor() + "amount and was forcefully ended!"));
                player.sendMessage(" ");
                player.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 30)));
                if (getPlayers().contains(player)) {
                    leave(player);
                }
            }
            this.destroy();
            return;
        }

        activeGame.searchArena();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Theme theme = ProfileManager.getProfile(player).getTheme();
            new ChatUtil(theme.getPrimaryColor() + "&l[Event] &f" + activeGame.getName() + theme.getSecondaryColor() + " has started. &7(" + getPlayers().size() + '/' + activeGame.getMaxPlayers() + ')' + (getPlayers().contains(player) ? "" : " &c(Click to spectate)")).send(player);
        }

        if (activeGame != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = ProfileManager.getProfile(player);
                if (profile.getPlayerState() == PlayerState.SPAWN && profile.getCurrentKit() == null) {
                    player.getInventory().setItem(3, ITEM);
                    player.updateInventory();
                }
            }
        }
        new GameStartedEvent(activeGame, players).call();
        getPlayers().clear();
    }

    public void destroy() {
        if (this.activeGame != null) {
            activeGame.setArena(null);
            this.getPlayers().clear();
            this.getSpectators().clear();
            this.teams.clear();
            this.invites.clear();
            this.cooldown.put(this.activeGame, TimeUtils.parse("1m30s") + System.currentTimeMillis());
            eventCooldown = TimeUtils.parse("1m30s") + System.currentTimeMillis();
            this.activeGame = null;
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inventory = player.getInventory();

                if (inventory.contains(ITEM)) {
                    inventory.remove(ITEM);
                }

                Profile profile = ProfileManager.getProfile(player);

                if (profile.getPlayerState() == PlayerState.SPAWN && profile.getCurrentKit() == null) {
                    inventory.setItem(3, PlayerItem.SPAWN_LEADERBOARD_ITEM.getItem());
                }
            }

        }
    }

    private void broadcast(String message) {
        if (this.getPlayers() != null) {
            for (Player player : getPlayers()) {
                player.sendMessage(message);
            }

        }

        if (this.activeGame != null) {
            for (Player player : activeGame.getPlayers().keySet()) {
                if (!this.getPlayers().contains(player)) {
                    player.sendMessage(message);
                }
            }
        }
    }

    private void loadClasses() {
        for (Game game : new Game[]{new SumoGame(), new TagGame(), new SpleefGame(), new BracketsGame(), new PotPvPGame()}) {
            games.add(game);
            ListenerHandler.registerListeners(game);
        }
    }
}
