package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadHandler implements Listener
{
    private final BigDoors plugin;

    public ChunkUnloadHandler(BigDoors plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunkUnload(ChunkUnloadEvent event)
    {
        // Abort all currently active BlockMovers that (might) interact with the chunk
        // that is being unloaded.
        plugin.getCommander().getBlockMovers()
            .filter(BM -> BM.getDoor().chunkInRange(event.getChunk()))
            .forEach(blockMover -> {
                plugin.getMyLogger().logMessageToLogFileForDoor(
                    blockMover.getDoor(), "Chunk unload detected while moving door. The animation will be cancelled."
                );
                blockMover.cancel(false);
             });
    }
}
