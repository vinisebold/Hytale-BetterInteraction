package dev.vinisebold.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.vinisebold.ui.BetterInteractionManagerUI;

import javax.annotation.Nonnull;

/**
 * Command to open the Better Interaction Manager UI.
 * Usage: /tutorial3 or /betterinteraction
 */
public class MyUICommand extends AbstractPlayerCommand {

    public MyUICommand() {
        super("tutorial3", "Opens Better Interaction Manager", false);
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

        // Build the Better Interaction Manager with tabs
        BetterInteractionManagerUI page = new BetterInteractionManagerUI(playerRef);

        assert player != null;
        player.getPageManager().openCustomPage(ref, store, page);
    }
}