package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.JsonRegistry;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public class ContextScoreboardNameProvider implements ScoreboardNameProvider {

    final LootTableInfo.EntityTarget target;

    ContextScoreboardNameProvider(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        this.target = loottableinfo_entitytarget;
    }

    public static ScoreboardNameProvider forTarget(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return new ContextScoreboardNameProvider(loottableinfo_entitytarget);
    }

    @Override
    public LootScoreProviderType getType() {
        return ScoreboardNameProviders.CONTEXT;
    }

    @Nullable
    @Override
    public String getScoreboardName(LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getParamOrNull(this.target.getParam());

        return entity != null ? entity.getScoreboardName() : null;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.target.getParam());
    }

    public static class a implements JsonRegistry.b<ContextScoreboardNameProvider> {

        public a() {}

        public JsonElement serialize(ContextScoreboardNameProvider contextscoreboardnameprovider, JsonSerializationContext jsonserializationcontext) {
            return jsonserializationcontext.serialize(contextscoreboardnameprovider.target);
        }

        @Override
        public ContextScoreboardNameProvider deserialize(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext) {
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) jsondeserializationcontext.deserialize(jsonelement, LootTableInfo.EntityTarget.class);

            return new ContextScoreboardNameProvider(loottableinfo_entitytarget);
        }
    }

    public static class b implements LootSerializer<ContextScoreboardNameProvider> {

        public b() {}

        public void serialize(JsonObject jsonobject, ContextScoreboardNameProvider contextscoreboardnameprovider, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("target", contextscoreboardnameprovider.target.name());
        }

        @Override
        public ContextScoreboardNameProvider deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) ChatDeserializer.getAsObject(jsonobject, "target", jsondeserializationcontext, LootTableInfo.EntityTarget.class);

            return new ContextScoreboardNameProvider(loottableinfo_entitytarget);
        }
    }
}
