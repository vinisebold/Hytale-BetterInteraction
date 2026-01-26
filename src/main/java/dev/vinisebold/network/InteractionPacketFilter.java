package dev.vinisebold.network;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.math.vector.Vector3i;
import dev.vinisebold.interaction.InteractionRedirector;
import dev.vinisebold.util.ContainerBlocks;

import javax.annotation.Nonnull;

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

            var pos = sync.data.blockPosition;
            var block = world.getBlockType(new Vector3i(pos.x, pos.y, pos.z));

            if (block != null && ContainerBlocks.isContainer(block.getId())) {
                InteractionRedirector.redirect(playerRef, world, pos, sync.chainId);
                return true;
            }
        }
        return false;
    }
}
