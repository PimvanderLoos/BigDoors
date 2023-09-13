package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public class FixedScoreboardNameProvider implements ScoreboardNameProvider {

    final String name;

    FixedScoreboardNameProvider(String s) {
        this.name = s;
    }

    public static ScoreboardNameProvider forName(String s) {
        return new FixedScoreboardNameProvider(s);
    }

    @Override
    public LootScoreProviderType getType() {
        return ScoreboardNameProviders.FIXED;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    @Override
    public String getScoreboardName(LootTableInfo loottableinfo) {
        return this.name;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of();
    }

    public static class a implements LootSerializer<FixedScoreboardNameProvider> {

        public a() {}

        public void serialize(JsonObject jsonobject, FixedScoreboardNameProvider fixedscoreboardnameprovider, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("name", fixedscoreboardnameprovider.name);
        }

        @Override
        public FixedScoreboardNameProvider deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.getAsString(jsonobject, "name");

            return new FixedScoreboardNameProvider(s);
        }
    }
}
