package net.minecraft.server;

public class DemoWorldServer extends WorldServer {

    private static final long F = (long) "North Carolina".hashCode();
    public static final WorldSettings a = (new WorldSettings(DemoWorldServer.F, EnumGamemode.SURVIVAL, true, false, WorldType.NORMAL)).a();

    public DemoWorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, PersistentCollection persistentcollection, WorldData worlddata, DimensionManager dimensionmanager, MethodProfiler methodprofiler) {
        super(minecraftserver, idatamanager, persistentcollection, worlddata, dimensionmanager, methodprofiler);
        this.worldData.a(DemoWorldServer.a);
    }
}
