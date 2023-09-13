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
    public LootNumberProviderType getType() {
        return NumberProviders.UNIFORM;
    }

    public static UniformGenerator between(float f, float f1) {
        return new UniformGenerator(ConstantValue.exactly(f), ConstantValue.exactly(f1));
    }

    @Override
    public int getInt(LootTableInfo loottableinfo) {
        return MathHelper.nextInt(loottableinfo.getRandom(), this.min.getInt(loottableinfo), this.max.getInt(loottableinfo));
    }

    @Override
    public float getFloat(LootTableInfo loottableinfo) {
        return MathHelper.nextFloat(loottableinfo.getRandom(), this.min.getFloat(loottableinfo), this.max.getFloat(loottableinfo));
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return Sets.union(this.min.getReferencedContextParams(), this.max.getReferencedContextParams());
    }

    public static class a implements LootSerializer<UniformGenerator> {

        public a() {}

        @Override
        public UniformGenerator deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "min", jsondeserializationcontext, NumberProvider.class);
            NumberProvider numberprovider1 = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "max", jsondeserializationcontext, NumberProvider.class);

            return new UniformGenerator(numberprovider, numberprovider1);
        }

        public void serialize(JsonObject jsonobject, UniformGenerator uniformgenerator, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("min", jsonserializationcontext.serialize(uniformgenerator.min));
            jsonobject.add("max", jsonserializationcontext.serialize(uniformgenerator.max));
        }
    }
}
