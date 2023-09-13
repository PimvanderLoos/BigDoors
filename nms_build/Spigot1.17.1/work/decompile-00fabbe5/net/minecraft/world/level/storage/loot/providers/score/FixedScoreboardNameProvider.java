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

    public static ScoreboardNameProvider a(String s) {
        return new FixedScoreboardNameProvider(s);
    }

    @Override
    public LootScoreProviderType a() {
        return ScoreboardNameProviders.FIXED;
    }

    public String c() {
        return this.name;
    }

    @Nullable
    @Override
    public String a(LootTableInfo loottableinfo) {
        return this.name;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of();
    }

    public static class a implements LootSerializer<FixedScoreboardNameProvider> {

        public a() {}

        public void a(JsonObject jsonobject, FixedScoreboardNameProvider fixedscoreboardnameprovider, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("name", fixedscoreboardnameprovider.name);
        }

        @Override
        public FixedScoreboardNameProvider a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            String s = ChatDeserializer.h(jsonobject, "name");

            return new FixedScoreboardNameProvider(s);
        }
    }
}
