package dev.vinisebold.network;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.math.vector.Vector3i;
import dev.vinisebold.interaction.InteractionRedirector;
import dev.vinisebold.util.ContainerBlocks;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk; // Importante
import com.hypixel.hytale.server.core.util.FillerBlockUtil;

import javax.annotation.Nonnull;


/**
 * Intercepts player interaction packets.
 * <p>
 * This filter detects interactions on functional blocks and normalizes the target
 * coordinates for multi-block structures (handling "filler" blocks like door tops).
 * This prevents visual duplication glitches by ensuring all interactions are processed
 * at the block's root position.
 * </p>
 */
public class InteractionPacketFilter implements PlayerPacketFilter {

    @Override
    public boolean test(@Nonnull PlayerRef playerRef, @Nonnull Packet packet) {
        if (!(packet instanceof SyncInteractionChains chains)) return false;
        if (playerRef.getWorldUuid() == null) return false;

        World world = Universe.get().getWorld(playerRef.getWorldUuid());
        if (world == null) return false;

        for (SyncInteractionChain sync : chains.updates) {
            if (!sync.initial) continue;
            if (sync.interactionType != InteractionType.Secondary) continue;
            if (sync.data == null || sync.data.blockPosition == null) continue;

            BlockPosition pos = sync.data.blockPosition;

            long chunkIndex = ChunkUtil.indexChunkFromBlock(pos.x, pos.z);
            WorldChunk chunk = world.getChunkIfInMemory(chunkIndex);

            if (chunk != null) {
                BlockChunk blockChunk = chunk.getBlockChunk();

                if (blockChunk != null && pos.y >= 0 && pos.y < 320) {

                    // We need to access the 'filler' value to determine the parent block position.
                    // The 'getSectionAtBlockY' method is marked @Deprecated but with 'forRemoval = false'.
                    // This indicates it is an internal API that is stable for use, unlike WorldChunk.getFiller
                    // which is marked 'forRemoval = true'.
                    // There is NO public alternative to access filler data in the current server API.
                    @SuppressWarnings("deprecation")
                    var section = blockChunk.getSectionAtBlockY(pos.y);

                    int filler = section.getFiller(pos.x, pos.y, pos.z);

                    if (filler != 0) {
                        int offsetX = FillerBlockUtil.unpackX(filler);
                        int offsetY = FillerBlockUtil.unpackY(filler);
                        int offsetZ = FillerBlockUtil.unpackZ(filler);

                        pos = new BlockPosition(
                                pos.x - offsetX,
                                pos.y - offsetY,
                                pos.z - offsetZ
                        );
                    }
                }
            }

            var block = world.getBlockType(new Vector3i(pos.x, pos.y, pos.z));

            if (block != null && ContainerBlocks.isContainer(block.getId())) {
                InteractionRedirector.redirect(playerRef, world, pos, sync.chainId);
                return true;
            }
        }
        return false;
    }
}