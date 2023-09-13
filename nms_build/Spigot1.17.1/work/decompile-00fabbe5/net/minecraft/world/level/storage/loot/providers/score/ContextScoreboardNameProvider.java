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

    public static ScoreboardNameProvider a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return new ContextScoreboardNameProvider(loottableinfo_entitytarget);
    }

    @Override
    public LootScoreProviderType a() {
        return ScoreboardNameProviders.CONTEXT;
    }

    @Nullable
    @Override
    public String a(LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getContextParameter(this.target.a());

        return entity != null ? entity.getName() : null;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of(this.target.a());
    }

    public static class a implements JsonRegistry.b<ContextScoreboardNameProvider> {

        public a() {}

        public JsonElement a(ContextScoreboardNameProvider contextscoreboardnameprovider, JsonSerializationContext jsonserializationcontext) {
            return jsonserializationcontext.serialize(contextscoreboardnameprovider.target);
        }

        @Override
        public ContextScoreboardNameProvider a(JsonElement jsonelement, JsonDeserializationContext jsondeserializationcontext) {
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) jsondeserializationcontext.deserialize(jsonelement, LootTableInfo.EntityTarget.class);

            return new ContextScoreboardNameProvider(loottableinfo_entitytarget);
        }
    }

    public static class b implements LootSerializer<ContextScoreboardNameProvider> {

        public b() {}

        public void a(JsonObject jsonobject, ContextScoreboardNameProvider contextscoreboardnameprovider, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("target", contextscoreboardnameprovider.target.name());
        }

        @Override
        public ContextScoreboardNameProvider a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) ChatDeserializer.a(jsonobject, "target", jsondeserializationcontext, LootTableInfo.EntityTarget.class);

            return new ContextScoreboardNameProvider(loottableinfo_entitytarget);
        }
    }
}
