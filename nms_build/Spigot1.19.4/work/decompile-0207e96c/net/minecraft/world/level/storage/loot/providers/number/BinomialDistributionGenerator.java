package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;

public final class BinomialDistributionGenerator implements NumberProvider {

    final NumberProvider n;
    final NumberProvider p;

    BinomialDistributionGenerator(NumberProvider numberprovider, NumberProvider numberprovider1) {
        this.n = numberprovider;
        this.p = numberprovider1;
    }

    @Override
    public LootNumberProviderType getType() {
        return NumberProviders.BINOMIAL;
    }

    @Override
    public int getInt(LootTableInfo loottableinfo) {
        int i = this.n.getInt(loottableinfo);
        float f = this.p.getFloat(loottableinfo);
        RandomSource randomsource = loottableinfo.getRandom();
        int j = 0;

        for (int k = 0; k < i; ++k) {
            if (randomsource.nextFloat() < f) {
                ++j;
            }
        }

        return j;
    }

    @Override
    public float getFloat(LootTableInfo loottableinfo) {
        return (float) this.getInt(loottableinfo);
    }

    public static BinomialDistributionGenerator binomial(int i, float f) {
        return new BinomialDistributionGenerator(ConstantValue.exactly((float) i), ConstantValue.exactly(f));
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return Sets.union(this.n.getReferencedContextParams(), this.p.getReferencedContextParams());
    }

    public static class a implements LootSerializer<BinomialDistributionGenerator> {

        public a() {}

        @Override
        public BinomialDistributionGenerator deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "n", jsondeserializationcontext, NumberProvider.class);
            NumberProvider numberprovider1 = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "p", jsondeserializationcontext, NumberProvider.class);

            return new BinomialDistributionGenerator(numberprovider, numberprovider1);
        }

        public void serialize(JsonObject jsonobject, BinomialDistributionGenerator binomialdistributiongenerator, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("n", jsonserializationcontext.serialize(binomialdistributiongenerator.n));
            jsonobject.add("p", jsonserializationcontext.serialize(binomialdistributiongenerator.p));
        }
    }
}
