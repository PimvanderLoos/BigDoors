package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutStatistic;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.EntityHuman;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatisticManager extends StatisticManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftServer server;
    private final File file;
    private final Set<Statistic<?>> dirty = Sets.newHashSet();

    public ServerStatisticManager(MinecraftServer minecraftserver, File file) {
        this.server = minecraftserver;
        this.file = file;
        if (file.isFile()) {
            try {
                this.a(minecraftserver.getDataFixer(), FileUtils.readFileToString(file));
            } catch (IOException ioexception) {
                ServerStatisticManager.LOGGER.error("Couldn't read statistics file {}", file, ioexception);
            } catch (JsonParseException jsonparseexception) {
                ServerStatisticManager.LOGGER.error("Couldn't parse statistics file {}", file, jsonparseexception);
            }
        }

    }

    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.b());
        } catch (IOException ioexception) {
            ServerStatisticManager.LOGGER.error("Couldn't save stats", ioexception);
        }

    }

    @Override
    public void setStatistic(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        super.setStatistic(entityhuman, statistic, i);
        this.dirty.add(statistic);
    }

    private Set<Statistic<?>> d() {
        Set<Statistic<?>> set = Sets.newHashSet(this.dirty);

        this.dirty.clear();
        return set;
    }

    public void a(DataFixer datafixer, String s) {
        try {
            JsonReader jsonreader = new JsonReader(new StringReader(s));

            label52:
            {
                try {
                    jsonreader.setLenient(false);
                    JsonElement jsonelement = Streams.parse(jsonreader);

                    if (!jsonelement.isJsonNull()) {
                        NBTTagCompound nbttagcompound = a(jsonelement.getAsJsonObject());

                        if (!nbttagcompound.hasKeyOfType("DataVersion", 99)) {
                            nbttagcompound.setInt("DataVersion", 1343);
                        }

                        nbttagcompound = GameProfileSerializer.a(datafixer, DataFixTypes.STATS, nbttagcompound, nbttagcompound.getInt("DataVersion"));
                        if (!nbttagcompound.hasKeyOfType("stats", 10)) {
                            break label52;
                        }

                        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("stats");
                        Iterator iterator = nbttagcompound1.getKeys().iterator();

                        while (true) {
                            if (!iterator.hasNext()) {
                                break label52;
                            }

                            String s1 = (String) iterator.next();

                            if (nbttagcompound1.hasKeyOfType(s1, 10)) {
                                SystemUtils.a(IRegistry.STAT_TYPE.getOptional(new MinecraftKey(s1)), (statisticwrapper) -> {
                                    NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound(s1);
                                    Iterator iterator1 = nbttagcompound2.getKeys().iterator();

                                    while (iterator1.hasNext()) {
                                        String s2 = (String) iterator1.next();

                                        if (nbttagcompound2.hasKeyOfType(s2, 99)) {
                                            SystemUtils.a(this.a(statisticwrapper, s2), (statistic) -> {
                                                this.stats.put(statistic, nbttagcompound2.getInt(s2));
                                            }, () -> {
                                                ServerStatisticManager.LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, s2);
                                            });
                                        } else {
                                            ServerStatisticManager.LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.file, nbttagcompound2.get(s2), s2);
                                        }
                                    }

                                }, () -> {
                                    ServerStatisticManager.LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, s1);
                                });
                            }
                        }
                    }

                    ServerStatisticManager.LOGGER.error("Unable to parse Stat data from {}", this.file);
                } catch (Throwable throwable) {
                    try {
                        jsonreader.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                jsonreader.close();
                return;
            }

            jsonreader.close();
        } catch (IOException | JsonParseException jsonparseexception) {
            ServerStatisticManager.LOGGER.error("Unable to parse Stat data from {}", this.file, jsonparseexception);
        }

    }

    private <T> Optional<Statistic<T>> a(StatisticWrapper<T> statisticwrapper, String s) {
        Optional optional = Optional.ofNullable(MinecraftKey.a(s));
        IRegistry iregistry = statisticwrapper.getRegistry();

        Objects.requireNonNull(iregistry);
        optional = optional.flatMap(iregistry::getOptional);
        Objects.requireNonNull(statisticwrapper);
        return optional.map(statisticwrapper::b);
    }

    private static NBTTagCompound a(JsonObject jsonobject) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = jsonobject.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry) iterator.next();
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
        Map<StatisticWrapper<?>, JsonObject> map = Maps.newHashMap();
        ObjectIterator objectiterator = this.stats.object2IntEntrySet().iterator();

        while (objectiterator.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Statistic<?>> it_unimi_dsi_fastutil_objects_object2intmap_entry = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry) objectiterator.next();
            Statistic<?> statistic = (Statistic) it_unimi_dsi_fastutil_objects_object2intmap_entry.getKey();

            ((JsonObject) map.computeIfAbsent(statistic.getWrapper(), (statisticwrapper) -> {
                return new JsonObject();
            })).addProperty(b(statistic).toString(), it_unimi_dsi_fastutil_objects_object2intmap_entry.getIntValue());
        }

        JsonObject jsonobject = new JsonObject();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<StatisticWrapper<?>, JsonObject> entry = (Entry) iterator.next();

            jsonobject.add(IRegistry.STAT_TYPE.getKey((StatisticWrapper) entry.getKey()).toString(), (JsonElement) entry.getValue());
        }

        JsonObject jsonobject1 = new JsonObject();

        jsonobject1.add("stats", jsonobject);
        jsonobject1.addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return jsonobject1.toString();
    }

    private static <T> MinecraftKey b(Statistic<T> statistic) {
        return statistic.getWrapper().getRegistry().getKey(statistic.b());
    }

    public void c() {
        this.dirty.addAll(this.stats.keySet());
    }

    public void a(EntityPlayer entityplayer) {
        Object2IntMap<Statistic<?>> object2intmap = new Object2IntOpenHashMap();
        Iterator iterator = this.d().iterator();

        while (iterator.hasNext()) {
            Statistic<?> statistic = (Statistic) iterator.next();

            object2intmap.put(statistic, this.getStatisticValue(statistic));
        }

        entityplayer.connection.sendPacket(new PacketPlayOutStatistic(object2intmap));
    }
}
