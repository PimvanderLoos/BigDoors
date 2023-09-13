package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;

public class PersistentCollection {

    private final Map<DimensionManager, WorldPersistentData> a;
    @Nullable
    private final IDataManager b;

    public PersistentCollection(@Nullable IDataManager idatamanager) {
        this.b = idatamanager;
        Builder builder = ImmutableMap.builder();
        Iterator iterator = DimensionManager.b().iterator();

        while (iterator.hasNext()) {
            DimensionManager dimensionmanager = (DimensionManager) iterator.next();
            WorldPersistentData worldpersistentdata = new WorldPersistentData(dimensionmanager, idatamanager);

            builder.put(dimensionmanager, worldpersistentdata);
            worldpersistentdata.a();
        }

        this.a = builder.build();
    }

    @Nullable
    public <T extends PersistentBase> T get(DimensionManager dimensionmanager, Function<String, T> function, String s) {
        return ((WorldPersistentData) this.a.get(dimensionmanager)).a(function, s);
    }

    public void a(DimensionManager dimensionmanager, String s, PersistentBase persistentbase) {
        ((WorldPersistentData) this.a.get(dimensionmanager)).a(s, persistentbase);
    }

    public void a() {
        this.a.values().forEach(WorldPersistentData::b);
    }

    public int a(DimensionManager dimensionmanager, String s) {
        return ((WorldPersistentData) this.a.get(dimensionmanager)).a(s);
    }

    public NBTTagCompound a(String s, int i) throws IOException {
        return WorldPersistentData.a(this.b, DimensionManager.OVERWORLD, s, i);
    }
}
