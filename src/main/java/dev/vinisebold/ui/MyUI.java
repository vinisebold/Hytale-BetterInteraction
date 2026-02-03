package dev.vinisebold.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.vinisebold.util.DebugChat;

import javax.annotation.Nonnull;

/**
 * Better Interaction UI - Advanced Configuration Menu
 *
 * Features:
 * - Navigation menu with 5 sections
 * - Interactive settings cards with toggle buttons
 * - Modern design with multiple button styles
 * - Real-time configuration updates
 */
public class MyUI extends InteractiveCustomUIPage<MyUI.UIEventData> {

    private boolean blockInteractionEnabled = true;
    private boolean debugMode = false;
    private String performanceMode = "BALANCEADO";

    /**
     * Event data for all UI interactions
     */
    public static class UIEventData {
        static final String KEY_BUTTON = "Button";

        public static final BuilderCodec<UIEventData> CODEC = BuilderCodec.<UIEventData>builder(UIEventData.class, UIEventData::new)
                .addField(new KeyedCodec<>(KEY_BUTTON, Codec.STRING), (data, s) -> data.buttonId = s, data -> data.buttonId)
                .build();

        private String buttonId;

        public UIEventData() {
            this.buttonId = "";
        }

        public String getButtonId() {
            return buttonId;
        }

        public void setButtonId(String buttonId) {
            this.buttonId = buttonId;
        }
    }

    public MyUI(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, UIEventData.CODEC);
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

        // Bind all navigation buttons
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavSettings", EventData.of("Button", "NavSettings"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavInteractions", EventData.of("Button", "NavInteractions"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavBlocks", EventData.of("Button", "NavBlocks"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavAdvanced", EventData.of("Button", "NavAdvanced"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavAbout", EventData.of("Button", "NavAbout"), false);

        // Bind settings buttons
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ToggleBlockInteraction", EventData.of("Button", "ToggleBlockInteraction"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ConfigureBlocks", EventData.of("Button", "ConfigureBlocks"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ToggleDebug", EventData.of("Button", "ToggleDebug"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#PerformanceMode", EventData.of("Button", "PerformanceMode"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ResetSettings", EventData.of("Button", "ResetSettings"), false);

        // Bind footer buttons
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", EventData.of("Button", "CloseButton"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ApplyButton", EventData.of("Button", "ApplyButton"), false);

        // Update button states based on current settings
        updateButtonStates(cmd);
    }

    private void updateButtonStates(UICommandBuilder cmd) {
        // Update toggle button texts
        cmd.set("#ToggleBlockInteraction.Text", blockInteractionEnabled ? "ATIVADO" : "DESATIVADO");
        cmd.set("#ToggleDebug.Text", debugMode ? "ATIVADO" : "DESATIVADO");
        cmd.set("#PerformanceMode.Text", performanceMode);
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull UIEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        
            String buttonId = data.getButtonId();
        
            if (buttonId == null) {
                return;
            }
        
            switch (buttonId) {
            case "NavSettings":
                DebugChat.send(ref, store, "§b[Better Interaction] §7Navegando para Configurações");
                // Future: Switch to settings page
                break;

            case "NavInteractions":
                DebugChat.send(ref, store, "§b[Better Interaction] §7Navegando para Interações");
                // Future: Switch to interactions page
                break;

            case "NavBlocks":
                DebugChat.send(ref, store, "§b[Better Interaction] §7Navegando para Blocos");
                // Future: Switch to blocks page
                break;

            case "NavAdvanced":
                DebugChat.send(ref, store, "§b[Better Interaction] §7Navegando para Avançado");
                // Future: Switch to advanced page
                break;

            case "NavAbout":
                DebugChat.send(ref, store, "§b[Better Interaction] §7Navegando para Sobre");
                // Future: Switch to about page
                break;

            case "ToggleBlockInteraction":
                blockInteractionEnabled = !blockInteractionEnabled;
                String statusBlock = blockInteractionEnabled ? "§aATIVADO" : "§cDESATIVADO";
                DebugChat.send(ref, store, "§b[Better Interaction] §7Interação com blocos: " + statusBlock);
                // Rebuild UI to update button
                    this.sendUpdate();
                break;

            case "ConfigureBlocks":
                DebugChat.send(ref, store, "§b[Better Interaction] §7Abrindo configuração de blocos...");
                // Future: Open block configuration menu
                break;

            case "ToggleDebug":
                debugMode = !debugMode;
                String statusDebug = debugMode ? "§aATIVADO" : "§cDESATIVADO";
                DebugChat.send(ref, store, "§b[Better Interaction] §7Modo de depuração: " + statusDebug);
                // Rebuild UI to update button
                    this.sendUpdate();
                break;

            case "PerformanceMode":
                // Cycle through performance modes
                if (performanceMode.equals("BALANCEADO")) {
                    performanceMode = "ALTO";
                    DebugChat.send(ref, store, "§b[Better Interaction] §7Modo de performance: §aALTO");
                } else if (performanceMode.equals("ALTO")) {
                    performanceMode = "BAIXO";
                    DebugChat.send(ref, store, "§b[Better Interaction] §7Modo de performance: §cBAIXO");
                } else {
                    performanceMode = "BALANCEADO";
                    DebugChat.send(ref, store, "§b[Better Interaction] §7Modo de performance: §eBALANCEADO");
                }
                // Rebuild UI to update button
                    this.sendUpdate();
                break;

            case "ResetSettings":
                blockInteractionEnabled = true;
                debugMode = false;
                performanceMode = "BALANCEADO";
                DebugChat.send(ref, store, "§b[Better Interaction] §eConfigurações resetadas para o padrão!");
                // Rebuild UI to update all buttons
                    this.sendUpdate();
                break;

            case "ApplyButton":
                DebugChat.send(ref, store, "§b[Better Interaction] §aConfigurações aplicadas com sucesso!");
                // Future: Save settings to config file
                break;

            case "CloseButton":
                // Close the page
                player.getPageManager().setPage(ref, store, Page.None);
                DebugChat.send(ref, store, "§b[Better Interaction] §7Menu fechado");
                break;

            default:
                    DebugChat.send(ref, store, "§b[Better Interaction] §cBotão desconhecido: " + buttonId);
                break;
        }
    }
}