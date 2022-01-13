package us.soupland.kitpvp.commands.experience;

import us.soupland.kitpvp.commands.experience.arguments.ExperienceCheckArgument;
import us.soupland.kitpvp.commands.experience.arguments.ExperienceIncrementArgument;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class ExperienceCommand extends KitPvPCommand {

    public ExperienceCommand() {
        super("experience");
        registerArgument(new ExperienceIncrementArgument());
        registerArgument(new ExperienceCheckArgument());
    }
}