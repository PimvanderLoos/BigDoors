package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionExplosionDecay extends LootItemFunctionConditional {

    LootItemFunctionExplosionDecay(LootItemCondition[] alootitemcondition) {
        super(alootitemcondition);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.EXPLOSION_DECAY;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        Float ofloat = (Float) loottableinfo.getParamOrNull(LootContextParameters.EXPLOSION_RADIUS);

        if (ofloat != null) {
            RandomSource randomsource = loottableinfo.getRandom();
            float f = 1.0F / ofloat;
            int i = itemstack.getCount();
            int j = 0;

            for (int k = 0; k < i; ++k) {
                if (randomsource.nextFloat() <= f) {
                    ++j;
                }
            }

            itemstack.setCount(j);
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> explosionDecay() {
        return simpleBuilder(LootItemFunctionExplosionDecay::new);
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionExplosionDecay> {

        public a() {}

        @Override
        public LootItemFunctionExplosionDecay deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionExplosionDecay(alootitemcondition);
        }
    }
}
