package dev.vinisebold.interaction;

import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.*;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.vinisebold.ui.BetterInteractionManagerUI;
import dev.vinisebold.util.DebugChat;


public final class InteractionRedirector {

    private InteractionRedirector() {
    }

    public static void redirect(PlayerRef playerRef, World world, BlockPosition pos, int originalChainId) {
        world.execute(() -> {

            if (playerRef == null || !BetterInteractionManagerUI.isBlockInteractionEnabled(playerRef)) {
                return;
            }

            Ref<EntityStore> entityRef = playerRef.getReference();
            if (entityRef == null || !entityRef.isValid()) return;

            Store<EntityStore> store = world.getEntityStore().getStore();
            InteractionManager manager = store.getComponent(
                    entityRef,
                    InteractionModule.get().getInteractionManagerComponent()
            );
            if (manager == null) return;

            InteractionChain oldChain = manager.getChains().get(originalChainId);
            if (oldChain != null) manager.cancelChains(oldChain);

            InteractionContext context = InteractionContext.forInteraction(
                    manager,
                    entityRef,
                    InteractionType.Use,
                    (short) -1,
                    store
            );

            context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, pos);
            context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, pos);

            String rootId = context.getRootInteractionId(InteractionType.Use);
            if (rootId == null) return;

            RootInteraction root = RootInteraction.getAssetMap().getAsset(rootId);
            if (root == null) return;

            InteractionChain chain = manager.initChain(
                    InteractionType.Use,
                    context,
                    root,
                    -1,
                    pos,
                    true
            );
            manager.queueExecuteChain(chain);

            DebugChat.sendIfEnabled(entityRef, store, "Abrindo " + rootId);
        });
    }
}
