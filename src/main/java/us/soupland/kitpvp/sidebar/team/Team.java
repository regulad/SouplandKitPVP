package us.soupland.kitpvp.sidebar.team;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;

import java.util.*;

@Getter
public class Team {

    @Getter
    private static Set<Team> teams = new HashSet<>();

    @Setter
    private String name, displayName, description;
    @Setter
    private UUID leader, uuid;
    @Setter
    private long createdAt;
    private Set<UUID> officers, members;
    private Map<UUID, UUID> invitedPlayers;
    private Map<UUID, Long> playerJoined;

    public Team(String name, UUID leader, UUID uuid) {
        this.name = name;
        this.leader = leader;
        this.uuid = uuid;
        this.createdAt = System.currentTimeMillis();

        this.officers = new HashSet<>();
        this.members = new HashSet<>();
        this.invitedPlayers = new HashMap<>();
        this.playerJoined = new HashMap<>();

        if (uuid == null) {
            this.uuid = UUID.randomUUID();
        }

        officers.add(leader);

        teams.add(this);
    }

    public static Team getByName(String name) {
        for (Team team : teams) {
            if (team.getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
                return team;
            }
        }
        return null;
    }

    public static void loadTeams() {
        try (MongoCursor cursor = KitPvP.getInstance().getPvPDatabase().getTeams().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = (Document) cursor.next();

                String name = document.getString("name");
                UUID leader = UUID.fromString(document.getString("leader"));
                UUID uuid = UUID.fromString(document.getString("uuid"));

                Team team = new Team(name, leader, uuid);

                team.setCreatedAt(document.getLong("createdAt"));

                if (document.containsKey("displayName")) {
                    team.setDisplayName(document.getString("displayName"));
                }

                if (document.containsKey("description")) {
                    team.setDescription(document.getString("description"));
                }

                if (document.containsKey("officers")) {
                    List<String> list = (List<String>) document.get("officers");

                    for (String string : list) {
                        team.getOfficers().add(UUID.fromString(string));
                    }
                }

                if (document.containsKey("members")) {
                    List<String> list = (List<String>) document.get("members");

                    for (String string : list) {
                        team.getMembers().add(UUID.fromString(string));
                    }
                }

                if (document.containsKey("invitedPlayers")) {
                    Document invitedPlayerMap = (Document) document.get("invitedPlayers");

                    for (String key : invitedPlayerMap.keySet()) {
                        UUID invitedPlayer = UUID.fromString(key);
                        UUID invitedBy = (UUID) invitedPlayerMap.get(key);
                        team.getInvitedPlayers().put(invitedPlayer, invitedBy);
                    }
                }

                if (document.containsKey("joinedPlayers")) {
                    Document joinedPlayers = (Document) document.get("joinedPlayers");

                    for (String key : joinedPlayers.keySet()) {
                        UUID invited = UUID.fromString(key);
                        long time = joinedPlayers.getLong(key);
                        team.getPlayerJoined().put(invited, time);
                    }
                }
            }
        }
    }

    public String getDisplayName() {
        if (displayName == null) {
            displayName = getName();
        }
        return displayName;
    }

    public int getOnlineMembers(CommandSender sender) {
        int size = 0;
        for (UUID uuid : getAllUuids()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                continue;
            }
            if (sender instanceof Player && !((Player) sender).canSee(player)) {
                continue;
            }
            size++;
        }
        return size;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> toReturn = new ArrayList<>();
        for (UUID uuid : getAllUuids()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                toReturn.add(player);
            }
        }
        return toReturn;
    }

    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    public void sendMessage(String message, UUID ignore) {
        getOnlinePlayers().forEach(player -> {
            if (ignore != null && ignore.equals(player.getUniqueId())) {
                return;
            }
            player.sendMessage(message);
        });
    }

    public List<UUID> getAllUuids() {
        List<UUID> list = new ArrayList<>(getMembers());
        list.add(leader);
        return list;
    }

    public void saveTeam() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", name);
        if (displayName != null) {
            document.put("displayName", displayName);
        }
        document.put("leader", leader.toString());
        document.put("createdAt", createdAt);
        if (description != null) {
            document.put("description", description);
        }

        if (!getOfficers().isEmpty()) {
            document.put("officers", getConvertedUuidSet(getOfficers()));
        }

        if (!getMembers().isEmpty()) {
            document.put("members", getConvertedUuidSet(getMembers()));
        }


        if (!getInvitedPlayers().isEmpty()) {
            Document invitedPlayers = new Document();

            for (UUID invited : getInvitedPlayers().keySet()) {
                invitedPlayers.put(invited.toString(), getInvitedPlayers().get(invited));
            }

            document.put("invitedPlayers", invitedPlayers);

        }

        if (!getPlayerJoined().isEmpty()) {
            Document joined = new Document();

            for (UUID uuid : playerJoined.keySet()) {
                joined.put(uuid.toString(), getPlayerJoined().get(uuid));
            }

            document.put("joinedPlayers", joined);

        }

        KitPvP.getInstance().getPvPDatabase().getTeams().replaceOne(Filters.eq("uuid", uuid.toString()), document, new UpdateOptions().upsert(true));
    }

    private Set<String> getConvertedUuidSet(Set<UUID> uuids) {
        Set<String> toReturn = new HashSet<>();

        for (UUID uuid : uuids) {
            toReturn.add(uuid.toString());
        }

        return toReturn;
    }
}