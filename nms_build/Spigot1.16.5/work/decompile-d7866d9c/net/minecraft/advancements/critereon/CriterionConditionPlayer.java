package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.stats.RecipeBookServer;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EnumGamemode;

public class CriterionConditionPlayer {

    public static final CriterionConditionPlayer a = (new CriterionConditionPlayer.d()).b();
    private final CriterionConditionValue.IntegerRange b;
    private final EnumGamemode c;
    private final Map<Statistic<?>, CriterionConditionValue.IntegerRange> d;
    private final Object2BooleanMap<MinecraftKey> e;
    private final Map<MinecraftKey, CriterionConditionPlayer.c> f;

    private static CriterionConditionPlayer.c b(JsonElement jsonelement) {
        if (jsonelement.isJsonPrimitive()) {
            boolean flag = jsonelement.getAsBoolean();

            return new CriterionConditionPlayer.b(flag);
        } else {
            Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap();
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "criterion data");

            jsonobject.entrySet().forEach((entry) -> {
                boolean flag1 = ChatDeserializer.c((JsonElement) entry.getValue(), "criterion test");

                object2booleanmap.put(entry.getKey(), flag1);
            });
            return new CriterionConditionPlayer.a(object2booleanmap);
        }
    }

    private CriterionConditionPlayer(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, EnumGamemode enumgamemode, Map<Statistic<?>, CriterionConditionValue.IntegerRange> map, Object2BooleanMap<MinecraftKey> object2booleanmap, Map<MinecraftKey, CriterionConditionPlayer.c> map1) {
        this.b = criterionconditionvalue_integerrange;
        this.c = enumgamemode;
        this.d = map;
        this.e = object2booleanmap;
        this.f = map1;
    }

    public boolean a(Entity entity) {
        if (this == CriterionConditionPlayer.a) {
            return true;
        } else if (!(entity instanceof EntityPlayer)) {
            return false;
        } else {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            if (!this.b.d(entityplayer.expLevel)) {
                return false;
            } else if (this.c != EnumGamemode.NOT_SET && this.c != entityplayer.playerInteractManager.getGameMode()) {
                return false;
            } else {
                ServerStatisticManager serverstatisticmanager = entityplayer.getStatisticManager();
                Iterator iterator = this.d.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<Statistic<?>, CriterionConditionValue.IntegerRange> entry = (Entry) iterator.next();
                    int i = serverstatisticmanager.getStatisticValue((Statistic) entry.getKey());

                    if (!((CriterionConditionValue.IntegerRange) entry.getValue()).d(i)) {
                        return false;
                    }
                }

                RecipeBookServer recipebookserver = entityplayer.getRecipeBook();
                ObjectIterator objectiterator = this.e.object2BooleanEntrySet().iterator();

                while (objectiterator.hasNext()) {
                    it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<MinecraftKey> it_unimi_dsi_fastutil_objects_object2booleanmap_entry = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry) objectiterator.next();

                    if (recipebookserver.hasDiscoveredRecipe((MinecraftKey) it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getKey()) != it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getBooleanValue()) {
                        return false;
                    }
                }

                if (!this.f.isEmpty()) {
                    AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancementData();
                    AdvancementDataWorld advancementdataworld = entityplayer.getMinecraftServer().getAdvancementData();
                    Iterator iterator1 = this.f.entrySet().iterator();

                    while (iterator1.hasNext()) {
                        Entry<MinecraftKey, CriterionConditionPlayer.c> entry1 = (Entry) iterator1.next();
                        Advancement advancement = advancementdataworld.a((MinecraftKey) entry1.getKey());

                        if (advancement == null || !((CriterionConditionPlayer.c) entry1.getValue()).test(advancementdataplayer.getProgress(advancement))) {
                            return false;
                        }
                    }
                }

                return true;
            }
        }
    }

    public static CriterionConditionPlayer a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "player");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("level"));
            String s = ChatDeserializer.a(jsonobject, "gamemode", "");
            EnumGamemode enumgamemode = EnumGamemode.a(s, EnumGamemode.NOT_SET);
            Map<Statistic<?>, CriterionConditionValue.IntegerRange> map = Maps.newHashMap();
            JsonArray jsonarray = ChatDeserializer.a(jsonobject, "stats", (JsonArray) null);

            if (jsonarray != null) {
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();
                    JsonObject jsonobject1 = ChatDeserializer.m(jsonelement1, "stats entry");
                    MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject1, "type"));
                    StatisticWrapper<?> statisticwrapper = (StatisticWrapper) IRegistry.STATS.get(minecraftkey);

                    if (statisticwrapper == null) {
                        throw new JsonParseException("Invalid stat type: " + minecraftkey);
                    }

                    MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.h(jsonobject1, "stat"));
                    Statistic<?> statistic = a(statisticwrapper, minecraftkey1);
                    CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.a(jsonobject1.get("value"));

                    map.put(statistic, criterionconditionvalue_integerrange1);
                }
            }

            Object2BooleanMap<MinecraftKey> object2booleanmap = new Object2BooleanOpenHashMap();
            JsonObject jsonobject2 = ChatDeserializer.a(jsonobject, "recipes", new JsonObject());
            Iterator iterator1 = jsonobject2.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry<String, JsonElement> entry = (Entry) iterator1.next();
                MinecraftKey minecraftkey2 = new MinecraftKey((String) entry.getKey());
                boolean flag = ChatDeserializer.c((JsonElement) entry.getValue(), "recipe present");

                object2booleanmap.put(minecraftkey2, flag);
            }

            Map<MinecraftKey, CriterionConditionPlayer.c> map1 = Maps.newHashMap();
            JsonObject jsonobject3 = ChatDeserializer.a(jsonobject, "advancements", new JsonObject());
            Iterator iterator2 = jsonobject3.entrySet().iterator();

            while (iterator2.hasNext()) {
                Entry<String, JsonElement> entry1 = (Entry) iterator2.next();
                MinecraftKey minecraftkey3 = new MinecraftKey((String) entry1.getKey());
                CriterionConditionPlayer.c criterionconditionplayer_c = b((JsonElement) entry1.getValue());

                map1.put(minecraftkey3, criterionconditionplayer_c);
            }

            return new CriterionConditionPlayer(criterionconditionvalue_integerrange, enumgamemode, map, object2booleanmap, map1);
        } else {
            return CriterionConditionPlayer.a;
        }
    }

    private static <T> Statistic<T> a(StatisticWrapper<T> statisticwrapper, MinecraftKey minecraftkey) {
        IRegistry<T> iregistry = statisticwrapper.getRegistry();
        T t0 = iregistry.get(minecraftkey);

        if (t0 == null) {
            throw new JsonParseException("Unknown object " + minecraftkey + " for stat type " + IRegistry.STATS.getKey(statisticwrapper));
        } else {
            return statisticwrapper.b(t0);
        }
    }

    private static <T> MinecraftKey a(Statistic<T> statistic) {
        return statistic.getWrapper().getRegistry().getKey(statistic.b());
    }

    public JsonElement a() {
        if (this == CriterionConditionPlayer.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("level", this.b.d());
            if (this.c != EnumGamemode.NOT_SET) {
                jsonobject.addProperty("gamemode", this.c.b());
            }

            if (!this.d.isEmpty()) {
                JsonArray jsonarray = new JsonArray();

                this.d.forEach((statistic, criterionconditionvalue_integerrange) -> {
                    JsonObject jsonobject1 = new JsonObject();

                    jsonobject1.addProperty("type", IRegistry.STATS.getKey(statistic.getWrapper()).toString());
                    jsonobject1.addProperty("stat", a(statistic).toString());
                    jsonobject1.add("value", criterionconditionvalue_integerrange.d());
                    jsonarray.add(jsonobject1);
                });
                jsonobject.add("stats", jsonarray);
            }

            JsonObject jsonobject1;

            if (!this.e.isEmpty()) {
                jsonobject1 = new JsonObject();
                this.e.forEach((minecraftkey, obool) -> {
                    jsonobject1.addProperty(minecraftkey.toString(), obool);
                });
                jsonobject.add("recipes", jsonobject1);
            }

            if (!this.f.isEmpty()) {
                jsonobject1 = new JsonObject();
                this.f.forEach((minecraftkey, criterionconditionplayer_c) -> {
                    jsonobject1.add(minecraftkey.toString(), criterionconditionplayer_c.a());
                });
                jsonobject.add("advancements", jsonobject1);
            }

            return jsonobject;
        }
    }

    public static class d {

        private CriterionConditionValue.IntegerRange a;
        private EnumGamemode b;
        private final Map<Statistic<?>, CriterionConditionValue.IntegerRange> c;
        private final Object2BooleanMap<MinecraftKey> d;
        private final Map<MinecraftKey, CriterionConditionPlayer.c> e;

        public d() {
            this.a = CriterionConditionValue.IntegerRange.e;
            this.b = EnumGamemode.NOT_SET;
            this.c = Maps.newHashMap();
            this.d = new Object2BooleanOpenHashMap();
            this.e = Maps.newHashMap();
        }

        public CriterionConditionPlayer b() {
            return new CriterionConditionPlayer(this.a, this.b, this.c, this.d, this.e);
        }
    }

    static class a implements CriterionConditionPlayer.c {

        private final Object2BooleanMap<String> a;

        public a(Object2BooleanMap<String> object2booleanmap) {
            this.a = object2booleanmap;
        }

        @Override
        public JsonElement a() {
            JsonObject jsonobject = new JsonObject();

            this.a.forEach(jsonobject::addProperty);
            return jsonobject;
        }

        public boolean test(AdvancementProgress advancementprogress) {
            ObjectIterator objectiterator = this.a.object2BooleanEntrySet().iterator();

            it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry it_unimi_dsi_fastutil_objects_object2booleanmap_entry;
            CriterionProgress criterionprogress;

            do {
                if (!objectiterator.hasNext()) {
                    return true;
                }

                it_unimi_dsi_fastutil_objects_object2booleanmap_entry = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry) objectiterator.next();
                criterionprogress = advancementprogress.getCriterionProgress((String) it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getKey());
            } while (criterionprogress != null && criterionprogress.a() == it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getBooleanValue());

            return false;
        }
    }

    static class b implements CriterionConditionPlayer.c {

        private final boolean a;

        public b(boolean flag) {
            this.a = flag;
        }

        @Override
        public JsonElement a() {
            return new JsonPrimitive(this.a);
        }

        public boolean test(AdvancementProgress advancementprogress) {
            return advancementprogress.isDone() == this.a;
        }
    }

    interface c extends Predicate<AdvancementProgress> {

        JsonElement a();
    }
}
