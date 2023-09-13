package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.level.saveddata.PersistentBase;

public class PersistentIdCounts extends PersistentBase {

    public static final String FILE_NAME = "idcounts";
    private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap();

    public PersistentIdCounts() {
        this.usedAuxIds.defaultReturnValue(-1);
    }

    public static PersistentIdCounts b(NBTTagCompound nbttagcompound) {
        PersistentIdCounts persistentidcounts = new PersistentIdCounts();
        Iterator iterator = nbttagcompound.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (nbttagcompound.hasKeyOfType(s, 99)) {
                persistentidcounts.usedAuxIds.put(s, nbttagcompound.getInt(s));
            }
        }

        return persistentidcounts;
    }

    @Override
    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        ObjectIterator objectiterator = this.usedAuxIds.object2IntEntrySet().iterator();

        while (objectiterator.hasNext()) {
            Entry<String> entry = (Entry) objectiterator.next();

            nbttagcompound.setInt((String) entry.getKey(), entry.getIntValue());
        }

        return nbttagcompound;
    }

    public int a() {
        int i = this.usedAuxIds.getInt("map") + 1;

        this.usedAuxIds.put("map", i);
        this.b();
        return i;
    }
}
