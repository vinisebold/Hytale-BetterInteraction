package dev.vinisebold;

import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;

import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        System.out.println("[SmartInteraction] Engenharia de Interação: Redirecionador Nativo Ativado.");

        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> sendChatDebug(event.getPlayer(), "Sistema de Interação Inteligente Pronto!", "green"));

        PacketAdapters.registerInbound(this::handlePacketFiltering);
    }

    private boolean handlePacketFiltering(@Nonnull PlayerRef playerRef, @Nonnull Packet packet) {
        if (!(packet instanceof SyncInteractionChains interactionChains)) return false;

        assert playerRef.getWorldUuid() != null;
        World world = Universe.get().getWorld(playerRef.getWorldUuid());
        if (world == null) return false;

        for (SyncInteractionChain sync : interactionChains.updates) {
            if (sync.initial && sync.interactionType == InteractionType.Secondary && sync.data != null && sync.data.blockPosition != null) {

                BlockPosition pos = sync.data.blockPosition;
                BlockType block = world.getBlockType(new Vector3i(pos.x, pos.y, pos.z));

                if (block != null && isContainer(block.getId().toLowerCase())) {
                    triggerManualInteract(playerRef, world, pos, sync.chainId);

                    return true;
                }
            }
        }
        return false;
    }

    private void triggerManualInteract(PlayerRef playerRef, World world, BlockPosition pos, int originalChainId) {
        world.execute(() -> {
            Ref<EntityStore> entityRef = playerRef.getReference();
            if (entityRef == null || !entityRef.isValid()) return;

            Store<EntityStore> store = world.getEntityStore().getStore();
            InteractionManager manager = store.getComponent(entityRef, InteractionModule.get().getInteractionManagerComponent());
            if (manager == null) return;

            // 1. CANCELA qualquer ação pendente
            InteractionChain oldChain = manager.getChains().get(originalChainId);
            if (oldChain != null) manager.cancelChains(oldChain);

            // 2. CRIA CONTEXTO DE INTERAGIR (Use = Valor 5)
            // Passamos slot -1 p garantir que ignore o item da mão
            InteractionContext context = InteractionContext.forInteraction(manager, entityRef, InteractionType.Use, (short) -1, store);

            // Sem isso, o motor não sabe oq abrir e cai no fallback de mão vazia.
            context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, pos);
            context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, pos);

            // 4. RESOLVE O ID DA AÇÃO
            String rootId = context.getRootInteractionId(InteractionType.Use);
            if (rootId == null) return;

            // 5. BUSCA O ASSET E EXECUTA
            RootInteraction root = RootInteraction.getAssetMap().getAsset(rootId);
            if (root != null) {
                InteractionChain chain = manager.initChain(InteractionType.Use, context, root, -1, pos, true);
                manager.queueExecuteChain(chain);

                Player player = store.getComponent(entityRef, Player.getComponentType());
                if (player != null) sendChatDebug(player, "Abrindo " + rootId, "cyan");
            }
        });
    }

    private static boolean isContainer(String id) {
        return id.contains("chest") || id.contains("door") || id.contains("crafting_table") ||
                id.contains("gate") || id.contains("barrel") || id.contains("container") ||
                id.contains("furnace") || id.contains("anvil") || id.contains("bench");
    }

    public static void sendChatDebug(Player player, String text, String color) {
        player.sendMessage(Message.raw("[DEBUG] " + text).color(color));
    }
}