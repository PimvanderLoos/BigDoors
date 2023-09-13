package net.minecraft.server;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;

public class ServerNBTManager extends WorldNBTStorage {

    public ServerNBTManager(File file, String s, @Nullable MinecraftServer minecraftserver, DataFixer datafixer) {
        super(file, s, minecraftserver, datafixer);
    }

    public IChunkLoader createChunkLoader(WorldProvider worldprovider) {
        File file = worldprovider.getDimensionManager().a(this.getDirectory());

        file.mkdirs();
        return new ChunkRegionLoader(file, this.a);
    }

    public void saveWorldData(WorldData worlddata, @Nullable NBTTagCompound nbttagcompound) {
        worlddata.d(19133);
        super.saveWorldData(worlddata, nbttagcompound);
    }

    public void a() {
        try {
            FileIOThread.a().b();
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }

        RegionFileCache.a();
    }
}
