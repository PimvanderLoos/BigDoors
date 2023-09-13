package net.minecraft.server;

import java.io.File;
import javax.annotation.Nullable;

public interface IDataManager {

    @Nullable
    WorldData getWorldData();

    void checkSession() throws ExceptionWorldConflict;

    IChunkLoader createChunkLoader(WorldProvider worldprovider);

    void saveWorldData(WorldData worlddata, NBTTagCompound nbttagcompound);

    void saveWorldData(WorldData worlddata);

    IPlayerFileData getPlayerFileData();

    void a();

    File getDirectory();

    File getDataFile(String s);

    DefinedStructureManager h();
}
