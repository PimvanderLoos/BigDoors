package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

public class PersistentCollection {

    public final Map<DimensionManager, WorldPersistentData> worldMap;
    @Nullable
    private final IDataManager b;

    public PersistentCollection(@Nullable IDataManager idatamanager) {
        this.b = idatamanager;
        Builder<DimensionManager, WorldPersistentData> builder = ImmutableMap.builder();
        Iterator iterator = DimensionManager.b().iterator();

        while (iterator.hasNext()) {
            DimensionManager dimensionmanager = (DimensionManager) iterator.next();
            WorldPersistentData worldpersistentdata = new WorldPersistentData(dimensionmanager, idatamanager);

            builder.put(dimensionmanager, worldpersistentdata);
            worldpersistentdata.a();
        }

        this.worldMap = builder.build();
    }

    @Nullable
    public <T extends PersistentBase> T get(DimensionManager dimensionmanager, Function<String, T> function, String s) {
        return ((WorldPersistentData) this.worldMap.get(dimensionmanager)).a(function, s);
    }

    public void a(DimensionManager dimensionmanager, String s, PersistentBase persistentbase) {
        ((WorldPersistentData) this.worldMap.get(dimensionmanager)).a(s, persistentbase);
    }

    public void a() {
        this.worldMap.values().forEach(WorldPersistentData::b);
    }

    public int a(DimensionManager dimensionmanager, String s) {
        return ((WorldPersistentData) this.worldMap.get(dimensionmanager)).a(s);
    }

    public NBTTagCompound a(String s, int i) throws IOException {
        return WorldPersistentData.a(this.b, DimensionManager.OVERWORLD, s, i);
    }
}
