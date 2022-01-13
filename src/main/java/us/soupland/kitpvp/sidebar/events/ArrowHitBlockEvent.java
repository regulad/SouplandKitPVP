package us.soupland.kitpvp.sidebar.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

@Getter
public class ArrowHitBlockEvent extends BlockEvent {

    private static HandlerList handlers = new HandlerList();

    private Arrow arrow;

    public ArrowHitBlockEvent(Arrow arrow, Block block) {
        super(block);
        this.arrow = arrow;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}


