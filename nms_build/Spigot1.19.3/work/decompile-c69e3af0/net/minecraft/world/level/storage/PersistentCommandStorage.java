package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.saveddata.PersistentBase;

public class PersistentCommandStorage {

    private static final String ID_PREFIX = "command_storage_";
    private final Map<String, PersistentCommandStorage.a> namespaces = Maps.newHashMap();
    private final WorldPersistentData storage;

    public PersistentCommandStorage(WorldPersistentData worldpersistentdata) {
        this.storage = worldpersistentdata;
    }

    private PersistentCommandStorage.a newStorage(String s) {
        PersistentCommandStorage.a persistentcommandstorage_a = new PersistentCommandStorage.a();

        this.namespaces.put(s, persistentcommandstorage_a);
        return persistentcommandstorage_a;
    }

    public NBTTagCompound get(MinecraftKey minecraftkey) {
        String s = minecraftkey.getNamespace();
        PersistentCommandStorage.a persistentcommandstorage_a = (PersistentCommandStorage.a) this.storage.get((nbttagcompound) -> {
            return this.newStorage(s).load(nbttagcompound);
        }, createId(s));

        return persistentcommandstorage_a != null ? persistentcommandstorage_a.get(minecraftkey.getPath()) : new NBTTagCompound();
    }

    public void set(MinecraftKey minecraftkey, NBTTagCompound nbttagcompound) {
        String s = minecraftkey.getNamespace();

        ((PersistentCommandStorage.a) this.storage.computeIfAbsent((nbttagcompound1) -> {
            return this.newStorage(s).load(nbttagcompound1);
        }, () -> {
            return this.newStorage(s);
        }, createId(s))).put(minecraftkey.getPath(), nbttagcompound);
    }

    public Stream<MinecraftKey> keys() {
        return this.namespaces.entrySet().stream().flatMap((entry) -> {
            return ((PersistentCommandStorage.a) entry.getValue()).getKeys((String) entry.getKey());
        });
    }

    private static String createId(String s) {
        return "command_storage_" + s;
    }

    private static class a extends PersistentBase {

        private static final String TAG_CONTENTS = "contents";
        private final Map<String, NBTTagCompound> storage = Maps.newHashMap();

        a() {}

        PersistentCommandStorage.a load(NBTTagCompound nbttagcompound) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("contents");
            Iterator iterator = nbttagcompound1.getAllKeys().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                this.storage.put(s, nbttagcompound1.getCompound(s));
            }

            return this;
        }

        @Override
        public NBTTagCompound save(NBTTagCompound nbttagcompound) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            this.storage.forEach((s, nbttagcompound2) -> {
                nbttagcompound1.put(s, nbttagcompound2.copy());
            });
            nbttagcompound.put("contents", nbttagcompound1);
            return nbttagcompound;
        }

        public NBTTagCompound get(String s) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) this.storage.get(s);

            return nbttagcompound != null ? nbttagcompound : new NBTTagCompound();
        }

        public void put(String s, NBTTagCompound nbttagcompound) {
            if (nbttagcompound.isEmpty()) {
                this.storage.remove(s);
            } else {
                this.storage.put(s, nbttagcompound);
            }

            this.setDirty();
        }

        public Stream<MinecraftKey> getKeys(String s) {
            return this.storage.keySet().stream().map((s1) -> {
                return new MinecraftKey(s, s1);
            });
        }
    }
}
