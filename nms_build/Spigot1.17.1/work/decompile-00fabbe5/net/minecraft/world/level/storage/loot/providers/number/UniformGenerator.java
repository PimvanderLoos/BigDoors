package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public class UniformGenerator implements NumberProvider {

    final NumberProvider min;
    final NumberProvider max;

    UniformGenerator(NumberProvider numberprovider, NumberProvider numberprovider1) {
        this.min = numberprovider;
        this.max = numberprovider1;
    }

    @Override
    public LootNumberProviderType a() {
        return NumberProviders.UNIFORM;
    }

    public static UniformGenerator a(float f, float f1) {
        return new UniformGenerator(ConstantValue.a(f), ConstantValue.a(f1));
    }

    @Override
    public int a(LootTableInfo loottableinfo) {
        return MathHelper.nextInt(loottableinfo.a(), this.min.a(loottableinfo), this.max.a(loottableinfo));
    }

    @Override
    public float b(LootTableInfo loottableinfo) {
        return MathHelper.a(loottableinfo.a(), this.min.b(loottableinfo), this.max.b(loottableinfo));
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return Sets.union(this.min.b(), this.max.b());
    }

    public static class a implements LootSerializer<UniformGenerator> {

        public a() {}

        @Override
        public UniformGenerator a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.a(jsonobject, "min", jsondeserializationcontext, NumberProvider.class);
            NumberProvider numberprovider1 = (NumberProvider) ChatDeserializer.a(jsonobject, "max", jsondeserializationcontext, NumberProvider.class);

            return new UniformGenerator(numberprovider, numberprovider1);
        }

        public void a(JsonObject jsonobject, UniformGenerator uniformgenerator, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("min", jsonserializationcontext.serialize(uniformgenerator.min));
            jsonobject.add("max", jsonserializationcontext.serialize(uniformgenerator.max));
        }
    }
}
