package dev.vinisebold;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.vinisebold.commands.*;
import dev.vinisebold.network.InteractionPacketFilter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Map<String, Item> ITEMS = new HashMap<>();

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        System.out.println("[BetterInteraction] Redirecionador de Interações ativo.");
        getCommandRegistry().registerCommand(new MyUICommand());
        PacketAdapters.registerInbound(new InteractionPacketFilter());

        this.getEventRegistry().register(LoadedAssetsEvent.class, Item.class, Main::onItemAssetLoad);

        getLogger().at(Level.INFO).log("Commands registered: /testui, /dialog, /form, /info, /tutorial1, /tutorial2, /tutorial3");
        this.getEventRegistry().registerGlobal(
                PlayerReadyEvent.class,
                event -> event.getPlayer().sendMessage(Message.raw("[BetterInteraction] Redirecionador de Interações ativo."))
        );
    }

    private static void onItemAssetLoad(LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>> event) {
        ITEMS = event.getAssetMap().getAssetMap();
    }
}
