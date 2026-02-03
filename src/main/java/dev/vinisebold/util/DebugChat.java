package dev.vinisebold.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DebugChat {

    private DebugChat() {}

    private static final Set<UUID> ENABLED = ConcurrentHashMap.newKeySet();

    public static void setEnabled(Ref<EntityStore> entityRef, Store<EntityStore> store, boolean enabled) {
        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }
        if (enabled) {
            ENABLED.add(playerRef.getUuid());
        } else {
            ENABLED.remove(playerRef.getUuid());
        }
    }

    public static boolean isEnabled(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        PlayerRef playerRef = store.getComponent(entityRef, PlayerRef.getComponentType());
        if (playerRef == null) {
            return false;
        }
        return ENABLED.contains(playerRef.getUuid());
    }

    public static void send(Ref<EntityStore> entityRef, Store<EntityStore> store, String text) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player != null) {
            player.sendMessage(Message.raw("[DEBUG - BetterInteraction] " + text).color("cyan"));
        }
    }

    public static void sendIfEnabled(Ref<EntityStore> entityRef, Store<EntityStore> store, String text) {
        if (!isEnabled(entityRef, store)) {
            return;
        }
        send(entityRef, store, text);
    }
}
