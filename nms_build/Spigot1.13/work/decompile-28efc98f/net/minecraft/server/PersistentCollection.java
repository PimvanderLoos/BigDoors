package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PersistentCollection {

    private static final Logger b = LogManager.getLogger();
    private final IDataManager c;
    protected Map<String, PersistentBase> a = Maps.newHashMap();
    private final List<PersistentBase> d = Lists.newArrayList();
    private final Object2IntMap<String> e = new Object2IntOpenHashMap();

    public PersistentCollection(IDataManager idatamanager) {
        this.c = idatamanager;
        this.e.defaultReturnValue(-1);
        this.b();
    }

    @Nullable
    public <T extends PersistentBase> T get(Function<String, T> function, String s) {
        PersistentBase persistentbase = (PersistentBase) this.a.get(s);

        if (persistentbase == null && this.c != null) {
            try {
                File file = this.c.getDataFile(s);

                if (file != null && file.exists()) {
                    persistentbase = (PersistentBase) function.apply(s);
                    persistentbase.a(this.a(s, 1519).getCompound("data"));
                    this.a.put(s, persistentbase);
                    this.d.add(persistentbase);
                }
            } catch (Exception exception) {
                PersistentCollection.b.error("Error loading saved data: {}", s, exception);
            }
        }

        return persistentbase;
    }

    public NBTTagCompound a(String s, int i) throws IOException {
        File file = this.c.getDataFile(s);
        FileInputStream fileinputstream = new FileInputStream(file);
        Throwable throwable = null;

        NBTTagCompound nbttagcompound;

        try {
            NBTTagCompound nbttagcompound1 = NBTCompressedStreamTools.a((InputStream) fileinputstream);
            int j = nbttagcompound1.hasKeyOfType("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : 1343;

            nbttagcompound = GameProfileSerializer.a(this.c.i(), DataFixTypes.SAVED_DATA, nbttagcompound1, j, i);
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (fileinputstream != null) {
                if (throwable != null) {
                    try {
                        fileinputstream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    fileinputstream.close();
                }
            }

        }

        return nbttagcompound;
    }

    public void a(String s, PersistentBase persistentbase) {
        if (this.a.containsKey(s)) {
            this.d.remove(this.a.remove(s));
        }

        this.a.put(s, persistentbase);
        this.d.add(persistentbase);
    }

    public void a() {
        for (int i = 0; i < this.d.size(); ++i) {
            PersistentBase persistentbase = (PersistentBase) this.d.get(i);

            if (persistentbase.d()) {
                this.a(persistentbase);
                persistentbase.a(false);
            }
        }

    }

    private void a(PersistentBase persistentbase) {
        if (this.c != null) {
            try {
                File file = this.c.getDataFile(persistentbase.getId());

                if (file != null) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    nbttagcompound.set("data", persistentbase.b(new NBTTagCompound()));
                    nbttagcompound.setInt("DataVersion", 1519);
                    FileOutputStream fileoutputstream = new FileOutputStream(file);

                    NBTCompressedStreamTools.a(nbttagcompound, (OutputStream) fileoutputstream);
                    fileoutputstream.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
    }

    private void b() {
        try {
            this.e.clear();
            if (this.c == null) {
                return;
            }

            File file = this.c.getDataFile("idcounts");

            if (file != null && file.exists()) {
                DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(datainputstream);

                datainputstream.close();
                Iterator iterator = nbttagcompound.getKeys().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    if (nbttagcompound.hasKeyOfType(s, 99)) {
                        this.e.put(s, nbttagcompound.getInt(s));
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public int a(String s) {
        int i = this.e.getInt(s) + 1;

        this.e.put(s, i);
        if (this.c == null) {
            return i;
        } else {
            try {
                File file = this.c.getDataFile("idcounts");

                if (file != null) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    ObjectIterator objectiterator = this.e.object2IntEntrySet().iterator();

                    while (objectiterator.hasNext()) {
                        Entry entry = (Entry) objectiterator.next();

                        nbttagcompound.setInt((String) entry.getKey(), entry.getIntValue());
                    }

                    DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file));

                    NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream);
                    dataoutputstream.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return i;
        }
    }
}
