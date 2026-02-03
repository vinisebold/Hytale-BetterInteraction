package dev.vinisebold.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.vinisebold.Main;
import dev.vinisebold.util.DebugChat;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockConfigUI extends InteractiveCustomUIPage<BlockConfigUI.BlockConfigData> {

    private static final Map<UUID, BlockConfigState> STATE = new ConcurrentHashMap<>();

    public BlockConfigUI(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BlockConfigData.CODEC);
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        BlockConfigState state = getState(ref, store);
        cmd.append("Pages/BlockConfig/BetterInteraction_BlockConfig.ui");

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", EventData.of("Button", "BackButton"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ModeWhitelistActive", EventData.of("Button", "ModeWhitelist"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ModeWhitelistInactive", EventData.of("Button", "ModeWhitelist"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ModeBlacklistActive", EventData.of("Button", "ModeBlacklist"), false);
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ModeBlacklistInactive", EventData.of("Button", "ModeBlacklist"), false);
        evt.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SearchInput", EventData.of("@SearchQuery", "#SearchInput.Value"), false);

        updateUI(cmd, evt, state);
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull BlockConfigData data
    ) {
        super.handleDataEvent(ref, store, data);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        BlockConfigState state = getState(ref, store);

        if (data.searchQuery != null) {
            state.searchQuery = data.searchQuery;
        }
        if (data.toggleId != null) {
            toggleBlock(state, data.toggleId);
        }

        if (data.button != null) {
            switch (data.button) {
                case "BackButton":
                    if (playerRef != null) {
                        player.getPageManager().openCustomPage(ref, store, new MyUI(playerRef));
                    }
                    return;

                case "ModeWhitelist":
                    state.mode = Mode.WHITELIST;
                    DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Modo: Whitelist");
                    break;

                case "ModeBlacklist":
                    state.mode = Mode.BLACKLIST;
                    DebugChat.sendIfEnabled(ref, store, "§b[Better Interaction] §7Modo: Blacklist");
                    break;

                default:
                    break;
            }
        }

        refreshUI(ref, store);
    }

    private void toggleBlock(BlockConfigState state, String blockIdRaw) {
        String blockId = normalizeBlockId(blockIdRaw);
        if (blockId.isEmpty()) {
            return;
        }
        List<String> list = state.mode == Mode.WHITELIST ? state.whitelist : state.blacklist;
        if (list.contains(blockId)) {
            list.remove(blockId);
        } else {
            list.add(blockId);
        }
    }

    private String normalizeBlockId(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toLowerCase(Locale.ROOT);
    }

    private BlockConfigState getState(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return new BlockConfigState();
        }
        return STATE.computeIfAbsent(playerRef.getUuid(), key -> new BlockConfigState());
    }

    private void refreshUI(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        BlockConfigState state = getState(ref, store);
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        updateUI(commandBuilder, eventBuilder, state);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void updateUI(UICommandBuilder cmd, UIEventBuilder evt, BlockConfigState state) {
        cmd.set("#ModeWhitelistActive.Visible", state.mode == Mode.WHITELIST);
        cmd.set("#ModeWhitelistInactive.Visible", state.mode != Mode.WHITELIST);
        cmd.set("#ModeBlacklistActive.Visible", state.mode == Mode.BLACKLIST);
        cmd.set("#ModeBlacklistInactive.Visible", state.mode != Mode.BLACKLIST);
        cmd.set("#SearchInput.Value", state.searchQuery == null ? "" : state.searchQuery);

        List<String> enabled = state.mode == Mode.WHITELIST ? state.whitelist : state.blacklist;
        List<String> results = getSearchResults(state.searchQuery, enabled);

        cmd.clear("#BlockList");
        cmd.set("#EmptyListLabel.Visible", results.isEmpty());

        int rowIndex = 0;
        int cardsInCurrentRow = 0;
        int itemIndex = 0;

        for (String blockId : results) {
            if (cardsInCurrentRow == 0) {
                cmd.appendInline("#BlockList", "Group { LayoutMode: Left; Anchor: (Bottom: 0); }");
            }

            cmd.append("#BlockList[" + rowIndex + "]", "Pages/BlockConfig/BetterInteraction_BlockEntry.ui");

            Item item = Main.ITEMS.get(blockId);
            String displayName = blockId;
            if (item != null) {
                String translationKey = item.getTranslationKey();
                displayName = I18nModule.get().getMessage(this.playerRef.getLanguage(), translationKey);
                if (displayName == null) {
                    displayName = blockId;
                }
            }

            cmd.set("#BlockList[" + rowIndex + "][" + cardsInCurrentRow + "] #BlockName.Text", displayName);
            cmd.set("#BlockList[" + rowIndex + "][" + cardsInCurrentRow + "] #BlockIcon.ItemId", blockId);
            
            boolean isEnabled = enabled.contains(blockId);
            cmd.set("#BlockList[" + rowIndex + "][" + cardsInCurrentRow + "] #ToggleOn.Visible", isEnabled);
            cmd.set("#BlockList[" + rowIndex + "][" + cardsInCurrentRow + "] #ToggleOff.Visible", !isEnabled);

            evt.addEventBinding(CustomUIEventBindingType.Activating, "#BlockList[" + rowIndex + "][" + cardsInCurrentRow + "] #ToggleOn", EventData.of("Toggle", blockId), false);
            evt.addEventBinding(CustomUIEventBindingType.Activating, "#BlockList[" + rowIndex + "][" + cardsInCurrentRow + "] #ToggleOff", EventData.of("Toggle", blockId), false);
            
            cardsInCurrentRow++;
            if (cardsInCurrentRow >= 5) {
                cardsInCurrentRow = 0;
                rowIndex++;
            }
        }
    }

    private List<String> getSearchResults(String queryRaw, List<String> enabled) {
        String query = queryRaw == null ? "" : queryRaw.trim().toLowerCase(Locale.ROOT);
        List<String> results = new ArrayList<>();
        if (query.isEmpty()) {
            results.addAll(enabled);
            return results;
        }

        for (Map.Entry<String, Item> entry : Main.ITEMS.entrySet()) {
            String id = entry.getKey();
            if (id == null) {
                continue;
            }
            String idLower = id.toLowerCase(Locale.ROOT);
            if (idLower.contains(query)) {
                results.add(id);
                continue;
            }
            Item item = entry.getValue();
            if (item != null) {
                String name = I18nModule.get().getMessage(this.playerRef.getLanguage(), item.getTranslationKey());
                if (name != null && name.toLowerCase(Locale.ROOT).contains(query)) {
                    results.add(id);
                }
            }
        }

        return results;
    }

    private enum Mode {
        WHITELIST,
        BLACKLIST
    }

    private static class BlockConfigState {
        private Mode mode = Mode.WHITELIST;
        private String searchQuery = "";
        private final List<String> whitelist = new ArrayList<>();
        private final List<String> blacklist = new ArrayList<>();
    }

    public static class BlockConfigData {
        static final String KEY_BUTTON = "Button";
        static final String KEY_TOGGLE_ID = "Toggle";
        static final String KEY_SEARCH_QUERY = "@SearchQuery";

        public static final BuilderCodec<BlockConfigData> CODEC = BuilderCodec.<BlockConfigData>builder(BlockConfigData.class, BlockConfigData::new)
                .addField(new KeyedCodec<>(KEY_BUTTON, Codec.STRING), (data, s) -> data.button = s, data -> data.button)
                .addField(new KeyedCodec<>(KEY_TOGGLE_ID, Codec.STRING), (data, s) -> data.toggleId = s, data -> data.toggleId)
                .addField(new KeyedCodec<>(KEY_SEARCH_QUERY, Codec.STRING), (data, s) -> data.searchQuery = s, data -> data.searchQuery)
                .build();

        private String button;
        private String toggleId;
        private String searchQuery;
    }
}
