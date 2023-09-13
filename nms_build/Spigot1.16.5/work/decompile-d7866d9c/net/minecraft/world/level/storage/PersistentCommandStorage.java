package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.saveddata.PersistentBase;

public class PersistentCommandStorage {

    private final Map<String, PersistentCommandStorage.a> a = Maps.newHashMap();
    private final WorldPersistentData b;

    public PersistentCommandStorage(WorldPersistentData worldpersistentdata) {
        this.b = worldpersistentdata;
    }

    private PersistentCommandStorage.a a(String s, String s1) {
        PersistentCommandStorage.a persistentcommandstorage_a = new PersistentCommandStorage.a(s1);

        this.a.put(s, persistentcommandstorage_a);
        return persistentcommandstorage_a;
    }

    public NBTTagCompound a(MinecraftKey minecraftkey) {
        String s = minecraftkey.getNamespace();
        String s1 = a(s);
        PersistentCommandStorage.a persistentcommandstorage_a = (PersistentCommandStorage.a) this.b.b(() -> {
            return this.a(s, s1);
        }, s1);

        return persistentcommandstorage_a != null ? persistentcommandstorage_a.a(minecraftkey.getKey()) : new NBTTagCompound();
    }

    public void a(MinecraftKey minecraftkey, NBTTagCompound nbttagcompound) {
        String s = minecraftkey.getNamespace();
        String s1 = a(s);

        ((PersistentCommandStorage.a) this.b.a(() -> {
            return this.a(s, s1);
        }, s1)).a(minecraftkey.getKey(), nbttagcompound);
    }

    public Stream<MinecraftKey> a() {
        return this.a.entrySet().stream().flatMap((entry) -> {
            return ((PersistentCommandStorage.a) entry.getValue()).b((String) entry.getKey());
        });
    }

    private static String a(String s) {
        return "command_storage_" + s;
    }

    static class a extends PersistentBase {

        private final Map<String, NBTTagCompound> a = Maps.newHashMap();

        public a(String s) {
            super(s);
        }

        @Override
        public void a(NBTTagCompound nbttagcompound) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("contents");
            Iterator iterator = nbttagcompound1.getKeys().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                this.a.put(s, nbttagcompound1.getCompound(s));
            }

        }

        @Override
        public NBTTagCompound b(NBTTagCompound nbttagcompound) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            this.a.forEach((s, nbttagcompound2) -> {
                nbttagcompound1.set(s, nbttagcompound2.clone());
            });
            nbttagcompound.set("contents", nbttagcompound1);
            return nbttagcompound;
        }

        public NBTTagCompound a(String s) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) this.a.get(s);

            return nbttagcompound != null ? nbttagcompound : new NBTTagCompound();
        }

        public void a(String s, NBTTagCompound nbttagcompound) {
            if (nbttagcompound.isEmpty()) {
                this.a.remove(s);
            } else {
                this.a.put(s, nbttagcompound);
            }

            this.b();
        }

        public Stream<MinecraftKey> b(String s) {
            return this.a.keySet().stream().map((s1) -> {
                return new MinecraftKey(s, s1);
            });
        }
    }
}
