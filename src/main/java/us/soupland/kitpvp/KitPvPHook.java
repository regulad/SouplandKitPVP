package us.soupland.kitpvp;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;

public class KitPvPHook extends PlaceholderExpansion {

    private KitPvP plugin;

    public KitPvPHook(KitPvP plugin){
        this.plugin = plugin;
    }

    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "kitpvp";
    }



    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){
        if(player == null){
            return "";
        }

        if(identifier.equals("chatcolor")){
            return ProfileManager.getProfile(player.getUniqueId()).getChatColor().toString();
        }

        if(identifier.equals("team")){
            return (ProfileManager.getProfile(player.getUniqueId()).getTeam() == null ? "" : ProfileManager.getProfile(player.getUniqueId()).getTeam().getDisplayName());
        }

        if(identifier.equals("pvplevel")){
            return (ProfileManager.getProfile(player.getUniqueId()).getLevelRank() == null ? "" : ProfileManager.getProfile(player.getUniqueId()).getLevelRank().getDisplayName());
        }
        return null;
    }
}