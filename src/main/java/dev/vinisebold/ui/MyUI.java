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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<UUID, UIState> STATE = new ConcurrentHashMap<>();

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

    private static class UIState {
        private boolean blockInteractionEnabled = true;
        private boolean debugMode = false;
        private String performanceMode = "BALANCEADO";
        private String activePage = "Settings";
    }

    public static boolean isBlockInteractionEnabled(@Nonnull PlayerRef playerRef) {
        UIState state = STATE.get(playerRef.getUuid());
        return state == null || state.blockInteractionEnabled;
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
        UIState state = getState(ref, store);

        // Load the UI layout
        cmd.append("MyUI.ui");

        // Bind all navigation buttons
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavSettingsActive", EventData.of("Button", "NavSettings"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavSettingsInactive", EventData.of("Button", "NavSettings"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavInteractionsActive", EventData.of("Button", "NavInteractions"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavInteractionsInactive", EventData.of("Button", "NavInteractions"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavAboutActive", EventData.of("Button", "NavAbout"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#NavAboutInactive", EventData.of("Button", "NavAbout"), false);

        // Bind settings buttons
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ToggleBlockInteractionOn", EventData.of("Button", "ToggleBlockInteraction"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ToggleBlockInteractionOff", EventData.of("Button", "ToggleBlockInteraction"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ConfigureBlocks", EventData.of("Button", "ConfigureBlocks"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ToggleDebugOn", EventData.of("Button", "ToggleDebug"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ToggleDebugOff", EventData.of("Button", "ToggleDebug"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#PerformanceMode", EventData.of("Button", "PerformanceMode"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ResetSettings", EventData.of("Button", "ResetSettings"), false);

        // Bind footer buttons
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", EventData.of("Button", "CloseButton"), false);

        // Update button states based on current settings
        updateButtonStates(cmd, state);
        DebugChat.setEnabled(ref, store, state.debugMode);
    }

    private UIState getState(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return new UIState();
        }
        return STATE.computeIfAbsent(playerRef.getUuid(), key -> new UIState());
    }

    private void updateButtonStates(UICommandBuilder cmd, UIState state) {
        // Update toggle button texts
        cmd.set("#ToggleBlockInteractionOn.Visible", state.blockInteractionEnabled);
        cmd.set("#ToggleBlockInteractionOff.Visible", !state.blockInteractionEnabled);
        cmd.set("#ToggleDebugOn.Visible", state.debugMode);
        cmd.set("#ToggleDebugOff.Visible", !state.debugMode);
        cmd.set("#PerformanceMode.Text", state.performanceMode);

        // Update page visibility
        cmd.set("#PageSettings.Visible", "Settings".equals(state.activePage));
        cmd.set("#PageInteractions.Visible", "Interactions".equals(state.activePage));
        cmd.set("#PageAbout.Visible", "About".equals(state.activePage));

        // Update nav button visibility
        cmd.set("#NavSettingsActive.Visible", "Settings".equals(state.activePage));
        cmd.set("#NavSettingsInactive.Visible", !"Settings".equals(state.activePage));
        cmd.set("#NavInteractionsActive.Visible", "Interactions".equals(state.activePage));
        cmd.set("#NavInteractionsInactive.Visible", !"Interactions".equals(state.activePage));
        cmd.set("#NavAboutActive.Visible", "About".equals(state.activePage));
        cmd.set("#NavAboutInactive.Visible", !"About".equals(state.activePage));
    }

    private void refreshUI(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        UIState state = getState(ref, store);
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        updateButtonStates(commandBuilder, state);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull UIEventData data
    ) {
        super.handleDataEvent(ref, store, data);
        Player player = store.getComponent(ref, Player.getComponentType());
        UIState state = getState(ref, store);
        
            String buttonId = data.getButtonId();
        
            if (buttonId == null) {
                return;
            }
        
            switch (buttonId) {
            case "NavSettings":
                state.activePage = "Settings";
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Navegando para Configurações");
                refreshUI(ref, store);
                break;

            case "NavInteractions":
                state.activePage = "Interactions";
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Navegando para Interações");
                refreshUI(ref, store);
                break;

            case "NavAbout":
                state.activePage = "About";
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Navegando para Sobre");
                refreshUI(ref, store);
                break;

            case "ToggleBlockInteraction":
                state.blockInteractionEnabled = !state.blockInteractionEnabled;
                String statusBlock = state.blockInteractionEnabled ? "§aATIVADO" : "§cDESATIVADO";
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Interação com blocos: " + statusBlock);
                // Rebuild UI to update button
                refreshUI(ref, store);
                break;

            case "ConfigureBlocks":
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Abrindo configuração de blocos...");
                // Future: Open block configuration menu
                break;

            case "ToggleDebug":
                state.debugMode = !state.debugMode;
                String statusDebug = state.debugMode ? "§aATIVADO" : "§cDESATIVADO";
                DebugChat.setEnabled(ref, store, state.debugMode);
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Modo de depuração: " + statusDebug);
                // Rebuild UI to update button
                refreshUI(ref, store);
                break;

            case "PerformanceMode":
                // Cycle through performance modes
                if ("BALANCEADO".equals(state.performanceMode)) {
                    state.performanceMode = "ALTO";
                    DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Modo de performance: §aALTO");
                } else if ("ALTO".equals(state.performanceMode)) {
                    state.performanceMode = "BAIXO";
                    DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Modo de performance: §cBAIXO");
                } else {
                    state.performanceMode = "BALANCEADO";
                    DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Modo de performance: §eBALANCEADO");
                }
                // Rebuild UI to update button
                refreshUI(ref, store);
                break;

            case "ResetSettings":
                state.blockInteractionEnabled = true;
                state.debugMode = false;
                state.performanceMode = "BALANCEADO";
                DebugChat.setEnabled(ref, store, state.debugMode);
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §eConfigurações resetadas para o padrão!");
                // Rebuild UI to update all buttons
                refreshUI(ref, store);
                break;

            case "CloseButton":
                // Close the page
                player.getPageManager().setPage(ref, store, Page.None);
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Menu fechado");
                break;

            default:
                DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §cBotão desconhecido: " + buttonId);
                break;
        }
    }
}