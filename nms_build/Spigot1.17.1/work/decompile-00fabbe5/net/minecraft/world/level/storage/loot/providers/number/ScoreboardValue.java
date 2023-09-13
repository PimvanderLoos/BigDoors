package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.scores.ScoreboardObjective;

public class ScoreboardValue implements NumberProvider {

    final ScoreboardNameProvider target;
    final String score;
    final float scale;

    ScoreboardValue(ScoreboardNameProvider scoreboardnameprovider, String s, float f) {
        this.target = scoreboardnameprovider;
        this.score = s;
        this.scale = f;
    }

    @Override
    public LootNumberProviderType a() {
        return NumberProviders.SCORE;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.target.b();
    }

    public static ScoreboardValue a(LootTableInfo.EntityTarget loottableinfo_entitytarget, String s) {
        return a(loottableinfo_entitytarget, s, 1.0F);
    }

    public static ScoreboardValue a(LootTableInfo.EntityTarget loottableinfo_entitytarget, String s, float f) {
        return new ScoreboardValue(ContextScoreboardNameProvider.a(loottableinfo_entitytarget), s, f);
    }

    @Override
    public float b(LootTableInfo loottableinfo) {
        String s = this.target.a(loottableinfo);

        if (s == null) {
            return 0.0F;
        } else {
            ScoreboardServer scoreboardserver = loottableinfo.getWorld().getScoreboard();
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective(this.score);

            return scoreboardobjective == null ? 0.0F : (!scoreboardserver.b(s, scoreboardobjective) ? 0.0F : (float) scoreboardserver.getPlayerScoreForObjective(s, scoreboardobjective).getScore() * this.scale);
        }
    }

    public static class a implements LootSerializer<ScoreboardValue> {

        public a() {}

        @Override
        public ScoreboardValue a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.h(jsonobject, "score");
            float f = ChatDeserializer.a(jsonobject, "scale", 1.0F);
            ScoreboardNameProvider scoreboardnameprovider = (ScoreboardNameProvider) ChatDeserializer.a(jsonobject, "target", jsondeserializationcontext, ScoreboardNameProvider.class);

            return new ScoreboardValue(scoreboardnameprovider, s, f);
        }

        public void a(JsonObject jsonobject, ScoreboardValue scoreboardvalue, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("score", scoreboardvalue.score);
            jsonobject.add("target", jsonserializationcontext.serialize(scoreboardvalue.target));
            jsonobject.addProperty("scale", scoreboardvalue.scale);
        }
    }
}
