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
    public LootNumberProviderType getType() {
        return NumberProviders.SCORE;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return this.target.getReferencedContextParams();
    }

    public static ScoreboardValue fromScoreboard(LootTableInfo.EntityTarget loottableinfo_entitytarget, String s) {
        return fromScoreboard(loottableinfo_entitytarget, s, 1.0F);
    }

    public static ScoreboardValue fromScoreboard(LootTableInfo.EntityTarget loottableinfo_entitytarget, String s, float f) {
        return new ScoreboardValue(ContextScoreboardNameProvider.forTarget(loottableinfo_entitytarget), s, f);
    }

    @Override
    public float getFloat(LootTableInfo loottableinfo) {
        String s = this.target.getScoreboardName(loottableinfo);

        if (s == null) {
            return 0.0F;
        } else {
            ScoreboardServer scoreboardserver = loottableinfo.getLevel().getScoreboard();
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjective(this.score);

            return scoreboardobjective == null ? 0.0F : (!scoreboardserver.hasPlayerScore(s, scoreboardobjective) ? 0.0F : (float) scoreboardserver.getOrCreatePlayerScore(s, scoreboardobjective).getScore() * this.scale);
        }
    }

    public static class a implements LootSerializer<ScoreboardValue> {

        public a() {}

        @Override
        public ScoreboardValue deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.getAsString(jsonobject, "score");
            float f = ChatDeserializer.getAsFloat(jsonobject, "scale", 1.0F);
            ScoreboardNameProvider scoreboardnameprovider = (ScoreboardNameProvider) ChatDeserializer.getAsObject(jsonobject, "target", jsondeserializationcontext, ScoreboardNameProvider.class);

            return new ScoreboardValue(scoreboardnameprovider, s, f);
        }

        public void serialize(JsonObject jsonobject, ScoreboardValue scoreboardvalue, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("score", scoreboardvalue.score);
            jsonobject.add("target", jsonserializationcontext.serialize(scoreboardvalue.target));
            jsonobject.addProperty("scale", scoreboardvalue.scale);
        }
    }
}
