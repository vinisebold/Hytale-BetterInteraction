package dev.vinisebold.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.vinisebold.ui.MyUI;

import javax.annotation.Nonnull;

/**
 * Command to open the Tutorial Level 3 page.
 * Usage: /tutorial3
 */
public class MyUICommand extends AbstractPlayerCommand {

    public MyUICommand() {
        super("tutorial3", "Opens Tutorial Level 3 - Dynamic Values", false);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());

        // Build the sectioned UI page
        MyUI page = new MyUI(playerRef);

        assert player != null;
        player.getPageManager().openCustomPage(ref, store, page);
    }
}