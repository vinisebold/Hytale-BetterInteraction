package dev.vinisebold;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.vinisebold.network.InteractionPacketFilter;

import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        System.out.println("[BetterInteraction] Redirecionador de Interações ativo.");

        this.getEventRegistry().registerGlobal(
                PlayerReadyEvent.class,
                event -> event.getPlayer().sendMessage(Message.raw("[BetterInteraction] Redirecionador de Interações ativo."))
        );

        PacketAdapters.registerInbound(new InteractionPacketFilter());
    }
}
