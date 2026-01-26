package dev.vinisebold.util;

public final class ContainerBlocks {

    private static final String[] KEYWORDS = {
            "chest", "door", "crafting_table",
            "gate", "barrel", "container",
            "furnace", "anvil", "bench"
    };

    private ContainerBlocks() {}

    public static boolean isContainer(String blockId) {
        String id = blockId.toLowerCase();
        for (String key : KEYWORDS) {
            if (id.contains(key)) return true;
        }
        return false;
    }
}
