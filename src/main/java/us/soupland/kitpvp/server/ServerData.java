package us.soupland.kitpvp.server;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Data;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.cuboid.Cuboid;
import org.bson.Document;
import org.bukkit.Location;
import us.soupland.kitpvp.utilities.location.LocationUtils;

@Data
public class ServerData {

    private String spawn, spawnPractice, spawnEvents, firstCI, secondCI, firstKoth, secondKoth;
    private boolean doubleCredits, freeKitsMode, freeEventsMode;

    public ServerData() {
        loadServer();
    }

    private void loadServer() {
        Document document = (Document) KitPvP.getInstance().getPvPDatabase().getServerInformation().find(Filters.eq("_faggot_", "mc")).first();
        if (document != null) {
            if (document.containsKey("spawn")) {
                spawn = document.getString("spawn");
            }
            if (document.containsKey("spawnPractice")) {
                spawnPractice = document.getString("spawnPractice");
            }
            if (document.containsKey("spawnEvents")) {
                spawnEvents = document.getString("spawnEvents");
            }
            if (document.containsKey("firstCI")) {
                firstCI = document.getString("firstCI");
            }
            if (document.containsKey("secondCI")) {
                secondCI = document.getString("secondCI");
            }
        }
    }

    public void saveServer() {
        Document document = new Document("_faggot_", "mc");
        if (spawn != null) {
            document.put("spawn", spawn);
        }
        if (spawnEvents != null) {
            document.put("spawnEvents", spawnEvents);
        }
        if (spawnPractice != null) {
            document.put("spawnPractice", spawnPractice);
        }
        if (firstCI != null) {
            document.put("firstCI", firstCI);
        }
        if (secondCI != null) {
            document.put("secondCI", secondCI);
        }
        KitPvP.getInstance().getPvPDatabase().getServerInformation().replaceOne(Filters.eq("_faggot_", "mc"), document, new UpdateOptions().upsert(true));
    }

    public Cuboid getSpawnCuboID() {
        Location first = LocationUtils.getLocation(firstCI);
        Location second = LocationUtils.getLocation(secondCI);
        if (first == null || second == null) {
            return null;
        }
        return new Cuboid(first, second);
    }

    public Cuboid getKothCuboID() {
        Location first = LocationUtils.getLocation(firstKoth);
        Location second = LocationUtils.getLocation(secondKoth);
        if (first == null || second == null) {
            return null;
        }
        return new Cuboid(first, second);
    }

    public Location getSpawnLocation() {
        return LocationUtils.getLocation(spawn);
    }

    public Location getSpawnEventsLocation() {
        return LocationUtils.getLocation(spawnEvents);
    }

}