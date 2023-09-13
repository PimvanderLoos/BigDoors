package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatisticManager extends StatisticManager {

    private static final Logger b = LogManager.getLogger();
    private final MinecraftServer c;
    private final File d;
    private final Set<Statistic<?>> e = Sets.newHashSet();
    private int f = -300;

    public ServerStatisticManager(MinecraftServer minecraftserver, File file) {
        this.c = minecraftserver;
        this.d = file;
        if (file.isFile()) {
            try {
                this.a(minecraftserver.aB(), FileUtils.readFileToString(file));
            } catch (IOException ioexception) {
                ServerStatisticManager.b.error("Couldn\'t read statistics file {}", file, ioexception);
            } catch (JsonParseException jsonparseexception) {
                ServerStatisticManager.b.error("Couldn\'t parse statistics file {}", file, jsonparseexception);
            }
        }

    }

    public void a() {
        try {
            FileUtils.writeStringToFile(this.d, this.b());
        } catch (IOException ioexception) {
            ServerStatisticManager.b.error("Couldn\'t save stats", ioexception);
        }

    }

    public void setStatistic(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        super.setStatistic(entityhuman, statistic, i);
        this.e.add(statistic);
    }

    private Set<Statistic<?>> d() {
        HashSet hashset = Sets.newHashSet(this.e);

        this.e.clear();
        return hashset;
    }

    public void a(DataFixer datafixer, String s) {
        try {
            JsonReader jsonreader = new JsonReader(new StringReader(s));
            Throwable throwable = null;

            try {
                jsonreader.setLenient(false);
                JsonElement jsonelement = Streams.parse(jsonreader);
                NBTTagCompound nbttagcompound = a(jsonelement.getAsJsonObject());

                if (!nbttagcompound.hasKeyOfType("DataVersion", 99)) {
                    nbttagcompound.setInt("DataVersion", 1343);
                }

                nbttagcompound = GameProfileSerializer.a(datafixer, DataFixTypes.STATS, nbttagcompound, nbttagcompound.getInt("DataVersion"));
                if (nbttagcompound.hasKeyOfType("stats", 10)) {
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("stats");
                    Iterator iterator = nbttagcompound1.getKeys().iterator();

                    while (iterator.hasNext()) {
                        String s1 = (String) iterator.next();

                        if (nbttagcompound1.hasKeyOfType(s1, 10)) {
                            StatisticWrapper statisticwrapper = (StatisticWrapper) StatisticList.REGISTRY.get(new MinecraftKey(s1));

                            if (statisticwrapper == null) {
                                ServerStatisticManager.b.warn("Invalid statistic type in {}: Don\'t know what {} is", this.d, s1);
                            } else {
                                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound(s1);
                                Iterator iterator1 = nbttagcompound2.getKeys().iterator();

                                while (iterator1.hasNext()) {
                                    String s2 = (String) iterator1.next();

                                    if (nbttagcompound2.hasKeyOfType(s2, 99)) {
                                        Statistic statistic = this.a(statisticwrapper, s2);

                                        if (statistic == null) {
                                            ServerStatisticManager.b.warn("Invalid statistic in {}: Don\'t know what {} is", this.d, s2);
                                        } else {
                                            this.a.put(statistic, nbttagcompound2.getInt(s2));
                                        }
                                    } else {
                                        ServerStatisticManager.b.warn("Invalid statistic value in {}: Don\'t know what {} is for key {}", this.d, nbttagcompound2.get(s2), s2);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (jsonreader != null) {
                    if (throwable != null) {
                        try {
                            jsonreader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        jsonreader.close();
                    }
                }

            }
        } catch (IOException | JsonParseException jsonparseexception) {
            ServerStatisticManager.b.error("Unable to parse Stat data from {}", this.d, jsonparseexception);
        }

    }

    @Nullable
    private <T> Statistic<T> a(StatisticWrapper<T> statisticwrapper, String s) {
        Object object = statisticwrapper.a().get(new MinecraftKey(s));

        return object == null ? null : statisticwrapper.b(object);
    }

    private static NBTTagCompound a(JsonObject jsonobject) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            JsonElement jsonelement = (JsonElement) entry.getValue();

            if (jsonelement.isJsonObject()) {
                nbttagcompound.set((String) entry.getKey(), a(jsonelement.getAsJsonObject()));
            } else if (jsonelement.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();

                if (jsonprimitive.isNumber()) {
                    nbttagcompound.setInt((String) entry.getKey(), jsonprimitive.getAsInt());
                }
            }
        }

        return nbttagcompound;
    }

    protected String b() {
        HashMap hashmap = Maps.newHashMap();
        ObjectIterator objectiterator = this.a.object2IntEntrySet().iterator();

        while (objectiterator.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry it_unimi_dsi_fastutil_objects_object2intmap_entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry) objectiterator.next();
            Statistic statistic = (Statistic) it_unimi_dsi_fastutil_objects_object2intmap_entry.getKey();

            ((JsonObject) hashmap.computeIfAbsent(statistic.a(), (statisticwrapper) -> {
                return new JsonObject();
            })).addProperty(b(statistic).toString(), Integer.valueOf(it_unimi_dsi_fastutil_objects_object2intmap_entry.getIntValue()));
        }

        JsonObject jsonobject = new JsonObject();
        Iterator iterator = hashmap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            jsonobject.add(((MinecraftKey) StatisticList.REGISTRY.b(entry.getKey())).toString(), (JsonElement) entry.getValue());
        }

        JsonObject jsonobject1 = new JsonObject();

        jsonobject1.add("stats", jsonobject);
        jsonobject1.addProperty("DataVersion", Integer.valueOf(1519));
        return jsonobject1.toString();
    }

    private static <T> MinecraftKey b(Statistic<T> statistic) {
        return (MinecraftKey) statistic.a().a().b(statistic.b());
    }

    public void c() {
        this.e.addAll(this.a.keySet());
    }

    public void a(EntityPlayer entityplayer) {
        int i = this.c.aj();
        Object2IntOpenHashMap object2intopenhashmap = new Object2IntOpenHashMap();

        if (i - this.f > 300) {
            this.f = i;
            Iterator iterator = this.d().iterator();

            while (iterator.hasNext()) {
                Statistic statistic = (Statistic) iterator.next();

                object2intopenhashmap.put(statistic, this.getStatisticValue(statistic));
            }
        }

        entityplayer.playerConnection.sendPacket(new PacketPlayOutStatistic(object2intopenhashmap));
    }
}
