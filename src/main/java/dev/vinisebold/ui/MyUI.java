package dev.vinisebold.ui;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Tutorial Level 3: Sectioned Layout
 *
 * Demonstrates:
 * - Sectioned content grouped into Settings, Advanced, and About
 * - Styled two-panel layout with FlexWeight
 * - Custom button styles
 * - Empty EventData for close-only buttons
 */
public class MyUI extends InteractiveCustomUIPage<MyUI.CloseEventData> {

    /**
     * Empty EventData - we only need to handle the close button.
     * No fields, just an empty codec.
     */
    public static class CloseEventData {
        public static final BuilderCodec<CloseEventData> CODEC =
                BuilderCodec.builder(CloseEventData.class, CloseEventData::new).build();
    }

        public MyUI(
            @Nonnull PlayerRef playerRef
        ) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, CloseEventData.CODEC);
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        // Load the UI layout
        cmd.append("MyUI.ui");

        // Bind close button - no EventData needed, just triggers handleDataEvent
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton");
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull CloseEventData data
    ) {
        // Close the page
        Player player = store.getComponent(ref, Player.getComponentType());
        player.getPageManager().setPage(ref, store, Page.None);
    }
}