package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardObjective;

public class LootItemConditionEntityScore implements LootItemCondition {

    final Map<String, IntRange> scores;
    final LootTableInfo.EntityTarget entityTarget;

    LootItemConditionEntityScore(Map<String, IntRange> map, LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        this.scores = ImmutableMap.copyOf(map);
        this.entityTarget = loottableinfo_entitytarget;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.ENTITY_SCORES;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return (Set) Stream.concat(Stream.of(this.entityTarget.getParam()), this.scores.values().stream().flatMap((intrange) -> {
            return intrange.getReferencedContextParams().stream();
        })).collect(ImmutableSet.toImmutableSet());
    }

    public boolean test(LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getParamOrNull(this.entityTarget.getParam());

        if (entity == null) {
            return false;
        } else {
            Scoreboard scoreboard = entity.level.getScoreboard();
            Iterator iterator = this.scores.entrySet().iterator();

            Entry entry;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entry = (Entry) iterator.next();
            } while (this.hasScore(loottableinfo, entity, scoreboard, (String) entry.getKey(), (IntRange) entry.getValue()));

            return false;
        }
    }

    protected boolean hasScore(LootTableInfo loottableinfo, Entity entity, Scoreboard scoreboard, String s, IntRange intrange) {
        ScoreboardObjective scoreboardobjective = scoreboard.getObjective(s);

        if (scoreboardobjective == null) {
            return false;
        } else {
            String s1 = entity.getScoreboardName();

            return !scoreboard.hasPlayerScore(s1, scoreboardobjective) ? false : intrange.test(loottableinfo, scoreboard.getOrCreatePlayerScore(s1, scoreboardobjective).getScore());
        }
    }

    public static LootItemConditionEntityScore.a hasScores(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return new LootItemConditionEntityScore.a(loottableinfo_entitytarget);
    }

    public static class a implements LootItemCondition.a {

        private final Map<String, IntRange> scores = Maps.newHashMap();
        private final LootTableInfo.EntityTarget entityTarget;

        public a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
            this.entityTarget = loottableinfo_entitytarget;
        }

        public LootItemConditionEntityScore.a withScore(String s, IntRange intrange) {
            this.scores.put(s, intrange);
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new LootItemConditionEntityScore(this.scores, this.entityTarget);
        }
    }

    public static class b implements LootSerializer<LootItemConditionEntityScore> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemConditionEntityScore lootitemconditionentityscore, JsonSerializationContext jsonserializationcontext) {
            JsonObject jsonobject1 = new JsonObject();
            Iterator iterator = lootitemconditionentityscore.scores.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, IntRange> entry = (Entry) iterator.next();

                jsonobject1.add((String) entry.getKey(), jsonserializationcontext.serialize(entry.getValue()));
            }

            jsonobject.add("scores", jsonobject1);
            jsonobject.add("entity", jsonserializationcontext.serialize(lootitemconditionentityscore.entityTarget));
        }

        @Override
        public LootItemConditionEntityScore deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            Set<Entry<String, JsonElement>> set = ChatDeserializer.getAsJsonObject(jsonobject, "scores").entrySet();
            Map<String, IntRange> map = Maps.newLinkedHashMap();
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                Entry<String, JsonElement> entry = (Entry) iterator.next();

                map.put((String) entry.getKey(), (IntRange) ChatDeserializer.convertToObject((JsonElement) entry.getValue(), "score", jsondeserializationcontext, IntRange.class));
            }

            return new LootItemConditionEntityScore(map, (LootTableInfo.EntityTarget) ChatDeserializer.getAsObject(jsonobject, "entity", jsondeserializationcontext, LootTableInfo.EntityTarget.class));
        }
    }
}
