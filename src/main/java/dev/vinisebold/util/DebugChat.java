package dev.vinisebold.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class DebugChat {

    private DebugChat() {}

    public static void send(Ref<EntityStore> entityRef, Store<EntityStore> store, String text) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player != null) {
            player.sendMessage(Message.raw("[DEBUG - BetterInteraction] " + text).color("cyan"));
        }
    }
}
