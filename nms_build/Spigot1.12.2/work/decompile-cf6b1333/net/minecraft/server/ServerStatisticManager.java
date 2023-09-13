package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatisticManager extends StatisticManager {

    private static final Logger b = LogManager.getLogger();
    private final MinecraftServer c;
    private final File d;
    private final Set<Statistic> e = Sets.newHashSet();
    private int f = -300;

    public ServerStatisticManager(MinecraftServer minecraftserver, File file) {
        this.c = minecraftserver;
        this.d = file;
    }

    public void a() {
        if (this.d.isFile()) {
            try {
                this.a.clear();
                this.a.putAll(this.a(FileUtils.readFileToString(this.d)));
            } catch (IOException ioexception) {
                ServerStatisticManager.b.error("Couldn\'t read statistics file {}", this.d, ioexception);
            } catch (JsonParseException jsonparseexception) {
                ServerStatisticManager.b.error("Couldn\'t parse statistics file {}", this.d, jsonparseexception);
            }
        }

    }

    public void b() {
        try {
            FileUtils.writeStringToFile(this.d, a(this.a));
        } catch (IOException ioexception) {
            ServerStatisticManager.b.error("Couldn\'t save stats", ioexception);
        }

    }

    public void setStatistic(EntityHuman entityhuman, Statistic statistic, int i) {
        super.setStatistic(entityhuman, statistic, i);
        this.e.add(statistic);
    }

    private Set<Statistic> d() {
        HashSet hashset = Sets.newHashSet(this.e);

        this.e.clear();
        return hashset;
    }

    public Map<Statistic, StatisticWrapper> a(String s) {
        JsonElement jsonelement = (new JsonParser()).parse(s);

        if (!jsonelement.isJsonObject()) {
            return Maps.newHashMap();
        } else {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            HashMap hashmap = Maps.newHashMap();
            Iterator iterator = jsonobject.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                Statistic statistic = StatisticList.getStatistic((String) entry.getKey());

                if (statistic != null) {
                    StatisticWrapper statisticwrapper = new StatisticWrapper();

                    if (((JsonElement) entry.getValue()).isJsonPrimitive() && ((JsonElement) entry.getValue()).getAsJsonPrimitive().isNumber()) {
                        statisticwrapper.a(((JsonElement) entry.getValue()).getAsInt());
                    } else if (((JsonElement) entry.getValue()).isJsonObject()) {
                        JsonObject jsonobject1 = ((JsonElement) entry.getValue()).getAsJsonObject();

                        if (jsonobject1.has("value") && jsonobject1.get("value").isJsonPrimitive() && jsonobject1.get("value").getAsJsonPrimitive().isNumber()) {
                            statisticwrapper.a(jsonobject1.getAsJsonPrimitive("value").getAsInt());
                        }

                        if (jsonobject1.has("progress") && statistic.g() != null) {
                            try {
                                Constructor constructor = statistic.g().getConstructor(new Class[0]);
                                IJsonStatistic ijsonstatistic = (IJsonStatistic) constructor.newInstance(new Object[0]);

                                ijsonstatistic.a(jsonobject1.get("progress"));
                                statisticwrapper.a(ijsonstatistic);
                            } catch (Throwable throwable) {
                                ServerStatisticManager.b.warn("Invalid statistic progress in {}", this.d, throwable);
                            }
                        }
                    }

                    hashmap.put(statistic, statisticwrapper);
                } else {
                    ServerStatisticManager.b.warn("Invalid statistic in {}: Don\'t know what {} is", this.d, entry.getKey());
                }
            }

            return hashmap;
        }
    }

    public static String a(Map<Statistic, StatisticWrapper> map) {
        JsonObject jsonobject = new JsonObject();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((StatisticWrapper) entry.getValue()).b() != null) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.addProperty("value", Integer.valueOf(((StatisticWrapper) entry.getValue()).a()));

                try {
                    jsonobject1.add("progress", ((StatisticWrapper) entry.getValue()).b().a());
                } catch (Throwable throwable) {
                    ServerStatisticManager.b.warn("Couldn\'t save statistic {}: error serializing progress", ((Statistic) entry.getKey()).d(), throwable);
                }

                jsonobject.add(((Statistic) entry.getKey()).name, jsonobject1);
            } else {
                jsonobject.addProperty(((Statistic) entry.getKey()).name, Integer.valueOf(((StatisticWrapper) entry.getValue()).a()));
            }
        }

        return jsonobject.toString();
    }

    public void c() {
        this.e.addAll(this.a.keySet());
    }

    public void a(EntityPlayer entityplayer) {
        int i = this.c.aq();
        HashMap hashmap = Maps.newHashMap();

        if (i - this.f > 300) {
            this.f = i;
            Iterator iterator = this.d().iterator();

            while (iterator.hasNext()) {
                Statistic statistic = (Statistic) iterator.next();

                hashmap.put(statistic, Integer.valueOf(this.getStatisticValue(statistic)));
            }
        }

        entityplayer.playerConnection.sendPacket(new PacketPlayOutStatistic(hashmap));
    }
}
