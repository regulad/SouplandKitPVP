package us.soupland.kitpvp.practice.arena;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Location;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.location.LocationUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class Arena {

    private String name;
    private Location firstPosition, secondPosition;
    private boolean active;
    private List<String> ladders;

    public Arena(String name) {
        this.name = name;
        this.ladders = new ArrayList<>();

        KitPvP.getInstance().getArenaHandler().registerArena(this);
    }

    public static void loadArenas() {
        try (MongoCursor<Document> mongoCursor = KitPvP.getInstance().getPvPDatabase().getArenas().find().iterator()) {
            while (mongoCursor.hasNext()) {
                Document document = mongoCursor.next();

                try {
                    Arena arena = new Arena(document.getString("name"));

                    if (document.containsKey("firstPosition")) {
                        arena.setFirstPosition(LocationUtils.getLocation(document.getString("firstPosition")));
                    }

                    if (document.containsKey("secondPosition")) {
                        arena.setSecondPosition(LocationUtils.getLocation(document.getString("secondPosition")));
                    }

                    if (document.containsKey("ladders")) {
                        List<String> strings = (List<String>) document.get("ladders");
                        arena.getLadders().addAll(strings);
                    }
                } catch (Exception ignored) {

                }

            }
        }
    }

    public int getMaxBuildHeight() {
        int i = (int) (Math.max(firstPosition.getY(), secondPosition.getY()));
        return i + 5;
    }

    public void saveArena() {
        Document document = new Document();
        document.put("name", name);
        if (firstPosition != null) {
            document.put("firstPosition", LocationUtils.getString(firstPosition));
        }
        if (secondPosition != null) {
            document.put("secondPosition", LocationUtils.getString(secondPosition));
        }
        if (!getLadders().isEmpty()) {
            document.put("ladders", getLadders());
        }
        KitPvP.getInstance().getPvPDatabase().getArenas().replaceOne(Filters.eq("name", name), document, new UpdateOptions().upsert(true));
    }
}