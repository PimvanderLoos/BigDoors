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
import java.util.Objects;
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
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionEntity;
import net.minecraft.world.phys.Vec3D;

public class CriterionConditionPlayer {

    public static final CriterionConditionPlayer ANY = (new CriterionConditionPlayer.d()).build();
    public static final int LOOKING_AT_RANGE = 100;
    private final CriterionConditionValue.IntegerRange level;
    @Nullable
    private final EnumGamemode gameType;
    private final Map<Statistic<?>, CriterionConditionValue.IntegerRange> stats;
    private final Object2BooleanMap<MinecraftKey> recipes;
    private final Map<MinecraftKey, CriterionConditionPlayer.c> advancements;
    private final CriterionConditionEntity lookingAt;

    private static CriterionConditionPlayer.c advancementPredicateFromJson(JsonElement jsonelement) {
        if (jsonelement.isJsonPrimitive()) {
            boolean flag = jsonelement.getAsBoolean();

            return new CriterionConditionPlayer.b(flag);
        } else {
            Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap();
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "criterion data");

            jsonobject.entrySet().forEach((entry) -> {
                boolean flag1 = ChatDeserializer.convertToBoolean((JsonElement) entry.getValue(), "criterion test");

                object2booleanmap.put((String) entry.getKey(), flag1);
            });
            return new CriterionConditionPlayer.a(object2booleanmap);
        }
    }

    CriterionConditionPlayer(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, @Nullable EnumGamemode enumgamemode, Map<Statistic<?>, CriterionConditionValue.IntegerRange> map, Object2BooleanMap<MinecraftKey> object2booleanmap, Map<MinecraftKey, CriterionConditionPlayer.c> map1, CriterionConditionEntity criterionconditionentity) {
        this.level = criterionconditionvalue_integerrange;
        this.gameType = enumgamemode;
        this.stats = map;
        this.recipes = object2booleanmap;
        this.advancements = map1;
        this.lookingAt = criterionconditionentity;
    }

    public boolean matches(Entity entity) {
        if (this == CriterionConditionPlayer.ANY) {
            return true;
        } else if (!(entity instanceof EntityPlayer)) {
            return false;
        } else {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            if (!this.level.matches(entityplayer.experienceLevel)) {
                return false;
            } else if (this.gameType != null && this.gameType != entityplayer.gameMode.getGameModeForPlayer()) {
                return false;
            } else {
                ServerStatisticManager serverstatisticmanager = entityplayer.getStats();
                Iterator iterator = this.stats.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<Statistic<?>, CriterionConditionValue.IntegerRange> entry = (Entry) iterator.next();
                    int i = serverstatisticmanager.getValue((Statistic) entry.getKey());

                    if (!((CriterionConditionValue.IntegerRange) entry.getValue()).matches(i)) {
                        return false;
                    }
                }

                RecipeBookServer recipebookserver = entityplayer.getRecipeBook();
                ObjectIterator objectiterator = this.recipes.object2BooleanEntrySet().iterator();

                while (objectiterator.hasNext()) {
                    it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<MinecraftKey> it_unimi_dsi_fastutil_objects_object2booleanmap_entry = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry) objectiterator.next();

                    if (recipebookserver.contains((MinecraftKey) it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getKey()) != it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getBooleanValue()) {
                        return false;
                    }
                }

                if (!this.advancements.isEmpty()) {
                    AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancements();
                    AdvancementDataWorld advancementdataworld = entityplayer.getServer().getAdvancements();
                    Iterator iterator1 = this.advancements.entrySet().iterator();

                    while (iterator1.hasNext()) {
                        Entry<MinecraftKey, CriterionConditionPlayer.c> entry1 = (Entry) iterator1.next();
                        Advancement advancement = advancementdataworld.getAdvancement((MinecraftKey) entry1.getKey());

                        if (advancement == null || !((CriterionConditionPlayer.c) entry1.getValue()).test(advancementdataplayer.getOrStartProgress(advancement))) {
                            return false;
                        }
                    }
                }

                if (this.lookingAt != CriterionConditionEntity.ANY) {
                    Vec3D vec3d = entityplayer.getEyePosition();
                    Vec3D vec3d1 = entityplayer.getViewVector(1.0F);
                    Vec3D vec3d2 = vec3d.add(vec3d1.x * 100.0D, vec3d1.y * 100.0D, vec3d1.z * 100.0D);
                    MovingObjectPositionEntity movingobjectpositionentity = ProjectileHelper.getEntityHitResult(entityplayer.level, entityplayer, vec3d, vec3d2, (new AxisAlignedBB(vec3d, vec3d2)).inflate(1.0D), (entity1) -> {
                        return !entity1.isSpectator();
                    }, 0.0F);

                    if (movingobjectpositionentity == null || movingobjectpositionentity.getType() != MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                        return false;
                    }

                    Entity entity1 = movingobjectpositionentity.getEntity();

                    if (!this.lookingAt.matches(entityplayer, entity1) || !entityplayer.hasLineOfSight(entity1)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static CriterionConditionPlayer fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "player");
            CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("level"));
            String s = ChatDeserializer.getAsString(jsonobject, "gamemode", "");
            EnumGamemode enumgamemode = EnumGamemode.byName(s, (EnumGamemode) null);
            Map<Statistic<?>, CriterionConditionValue.IntegerRange> map = Maps.newHashMap();
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "stats", (JsonArray) null);

            if (jsonarray != null) {
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();
                    JsonObject jsonobject1 = ChatDeserializer.convertToJsonObject(jsonelement1, "stats entry");
                    MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject1, "type"));
                    StatisticWrapper<?> statisticwrapper = (StatisticWrapper) IRegistry.STAT_TYPE.get(minecraftkey);

                    if (statisticwrapper == null) {
                        throw new JsonParseException("Invalid stat type: " + minecraftkey);
                    }

                    MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.getAsString(jsonobject1, "stat"));
                    Statistic<?> statistic = getStat(statisticwrapper, minecraftkey1);
                    CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.fromJson(jsonobject1.get("value"));

                    map.put(statistic, criterionconditionvalue_integerrange1);
                }
            }

            Object2BooleanMap<MinecraftKey> object2booleanmap = new Object2BooleanOpenHashMap();
            JsonObject jsonobject2 = ChatDeserializer.getAsJsonObject(jsonobject, "recipes", new JsonObject());
            Iterator iterator1 = jsonobject2.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry<String, JsonElement> entry = (Entry) iterator1.next();
                MinecraftKey minecraftkey2 = new MinecraftKey((String) entry.getKey());
                boolean flag = ChatDeserializer.convertToBoolean((JsonElement) entry.getValue(), "recipe present");

                object2booleanmap.put(minecraftkey2, flag);
            }

            Map<MinecraftKey, CriterionConditionPlayer.c> map1 = Maps.newHashMap();
            JsonObject jsonobject3 = ChatDeserializer.getAsJsonObject(jsonobject, "advancements", new JsonObject());
            Iterator iterator2 = jsonobject3.entrySet().iterator();

            while (iterator2.hasNext()) {
                Entry<String, JsonElement> entry1 = (Entry) iterator2.next();
                MinecraftKey minecraftkey3 = new MinecraftKey((String) entry1.getKey());
                CriterionConditionPlayer.c criterionconditionplayer_c = advancementPredicateFromJson((JsonElement) entry1.getValue());

                map1.put(minecraftkey3, criterionconditionplayer_c);
            }

            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.fromJson(jsonobject.get("looking_at"));

            return new CriterionConditionPlayer(criterionconditionvalue_integerrange, enumgamemode, map, object2booleanmap, map1, criterionconditionentity);
        } else {
            return CriterionConditionPlayer.ANY;
        }
    }

    private static <T> Statistic<T> getStat(StatisticWrapper<T> statisticwrapper, MinecraftKey minecraftkey) {
        IRegistry<T> iregistry = statisticwrapper.getRegistry();
        T t0 = iregistry.get(minecraftkey);

        if (t0 == null) {
            throw new JsonParseException("Unknown object " + minecraftkey + " for stat type " + IRegistry.STAT_TYPE.getKey(statisticwrapper));
        } else {
            return statisticwrapper.get(t0);
        }
    }

    private static <T> MinecraftKey getStatValueId(Statistic<T> statistic) {
        return statistic.getType().getRegistry().getKey(statistic.getValue());
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionPlayer.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("level", this.level.serializeToJson());
            if (this.gameType != null) {
                jsonobject.addProperty("gamemode", this.gameType.getName());
            }

            if (!this.stats.isEmpty()) {
                JsonArray jsonarray = new JsonArray();

                this.stats.forEach((statistic, criterionconditionvalue_integerrange) -> {
                    JsonObject jsonobject1 = new JsonObject();

                    jsonobject1.addProperty("type", IRegistry.STAT_TYPE.getKey(statistic.getType()).toString());
                    jsonobject1.addProperty("stat", getStatValueId(statistic).toString());
                    jsonobject1.add("value", criterionconditionvalue_integerrange.serializeToJson());
                    jsonarray.add(jsonobject1);
                });
                jsonobject.add("stats", jsonarray);
            }

            JsonObject jsonobject1;

            if (!this.recipes.isEmpty()) {
                jsonobject1 = new JsonObject();
                this.recipes.forEach((minecraftkey, obool) -> {
                    jsonobject1.addProperty(minecraftkey.toString(), obool);
                });
                jsonobject.add("recipes", jsonobject1);
            }

            if (!this.advancements.isEmpty()) {
                jsonobject1 = new JsonObject();
                this.advancements.forEach((minecraftkey, criterionconditionplayer_c) -> {
                    jsonobject1.add(minecraftkey.toString(), criterionconditionplayer_c.toJson());
                });
                jsonobject.add("advancements", jsonobject1);
            }

            jsonobject.add("looking_at", this.lookingAt.serializeToJson());
            return jsonobject;
        }
    }

    private static class b implements CriterionConditionPlayer.c {

        private final boolean state;

        public b(boolean flag) {
            this.state = flag;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.state);
        }

        public boolean test(AdvancementProgress advancementprogress) {
            return advancementprogress.isDone() == this.state;
        }
    }

    private static class a implements CriterionConditionPlayer.c {

        private final Object2BooleanMap<String> criterions;

        public a(Object2BooleanMap<String> object2booleanmap) {
            this.criterions = object2booleanmap;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonobject = new JsonObject();
            Object2BooleanMap object2booleanmap = this.criterions;

            Objects.requireNonNull(jsonobject);
            object2booleanmap.forEach(jsonobject::addProperty);
            return jsonobject;
        }

        public boolean test(AdvancementProgress advancementprogress) {
            ObjectIterator objectiterator = this.criterions.object2BooleanEntrySet().iterator();

            it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry it_unimi_dsi_fastutil_objects_object2booleanmap_entry;
            CriterionProgress criterionprogress;

            do {
                if (!objectiterator.hasNext()) {
                    return true;
                }

                it_unimi_dsi_fastutil_objects_object2booleanmap_entry = (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry) objectiterator.next();
                criterionprogress = advancementprogress.getCriterion((String) it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getKey());
            } while (criterionprogress != null && criterionprogress.isDone() == it_unimi_dsi_fastutil_objects_object2booleanmap_entry.getBooleanValue());

            return false;
        }
    }

    private interface c extends Predicate<AdvancementProgress> {

        JsonElement toJson();
    }

    public static class d {

        private CriterionConditionValue.IntegerRange level;
        @Nullable
        private EnumGamemode gameType;
        private final Map<Statistic<?>, CriterionConditionValue.IntegerRange> stats;
        private final Object2BooleanMap<MinecraftKey> recipes;
        private final Map<MinecraftKey, CriterionConditionPlayer.c> advancements;
        private CriterionConditionEntity lookingAt;

        public d() {
            this.level = CriterionConditionValue.IntegerRange.ANY;
            this.stats = Maps.newHashMap();
            this.recipes = new Object2BooleanOpenHashMap();
            this.advancements = Maps.newHashMap();
            this.lookingAt = CriterionConditionEntity.ANY;
        }

        public static CriterionConditionPlayer.d player() {
            return new CriterionConditionPlayer.d();
        }

        public CriterionConditionPlayer.d setLevel(CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.level = criterionconditionvalue_integerrange;
            return this;
        }

        public CriterionConditionPlayer.d addStat(Statistic<?> statistic, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            this.stats.put(statistic, criterionconditionvalue_integerrange);
            return this;
        }

        public CriterionConditionPlayer.d addRecipe(MinecraftKey minecraftkey, boolean flag) {
            this.recipes.put(minecraftkey, flag);
            return this;
        }

        public CriterionConditionPlayer.d setGameType(EnumGamemode enumgamemode) {
            this.gameType = enumgamemode;
            return this;
        }

        public CriterionConditionPlayer.d setLookingAt(CriterionConditionEntity criterionconditionentity) {
            this.lookingAt = criterionconditionentity;
            return this;
        }

        public CriterionConditionPlayer.d checkAdvancementDone(MinecraftKey minecraftkey, boolean flag) {
            this.advancements.put(minecraftkey, new CriterionConditionPlayer.b(flag));
            return this;
        }

        public CriterionConditionPlayer.d checkAdvancementCriterions(MinecraftKey minecraftkey, Map<String, Boolean> map) {
            this.advancements.put(minecraftkey, new CriterionConditionPlayer.a(new Object2BooleanOpenHashMap(map)));
            return this;
        }

        public CriterionConditionPlayer build() {
            return new CriterionConditionPlayer(this.level, this.gameType, this.stats, this.recipes, this.advancements, this.lookingAt);
        }
    }
}
