package net.minecraft.world.level.storage.loot.providers.number;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
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
    public LootNumberProviderType a() {
        return NumberProviders.BINOMIAL;
    }

    @Override
    public int a(LootTableInfo loottableinfo) {
        int i = this.n.a(loottableinfo);
        float f = this.p.b(loottableinfo);
        Random random = loottableinfo.a();
        int j = 0;

        for (int k = 0; k < i; ++k) {
            if (random.nextFloat() < f) {
                ++j;
            }
        }

        return j;
    }

    @Override
    public float b(LootTableInfo loottableinfo) {
        return (float) this.a(loottableinfo);
    }

    public static BinomialDistributionGenerator a(int i, float f) {
        return new BinomialDistributionGenerator(ConstantValue.a((float) i), ConstantValue.a(f));
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return Sets.union(this.n.b(), this.p.b());
    }

    public static class a implements LootSerializer<BinomialDistributionGenerator> {

        public a() {}

        @Override
        public BinomialDistributionGenerator a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.a(jsonobject, "n", jsondeserializationcontext, NumberProvider.class);
            NumberProvider numberprovider1 = (NumberProvider) ChatDeserializer.a(jsonobject, "p", jsondeserializationcontext, NumberProvider.class);

            return new BinomialDistributionGenerator(numberprovider, numberprovider1);
        }

        public void a(JsonObject jsonobject, BinomialDistributionGenerator binomialdistributiongenerator, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("n", jsonserializationcontext.serialize(binomialdistributiongenerator.n));
            jsonobject.add("p", jsonserializationcontext.serialize(binomialdistributiongenerator.p));
        }
    }
}
