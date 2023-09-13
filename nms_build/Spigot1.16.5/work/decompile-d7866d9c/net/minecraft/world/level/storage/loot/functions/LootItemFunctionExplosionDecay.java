package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionExplosionDecay extends LootItemFunctionConditional {

    private LootItemFunctionExplosionDecay(LootItemCondition[] alootitemcondition) {
        super(alootitemcondition);
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.r;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        Float ofloat = (Float) loottableinfo.getContextParameter(LootContextParameters.EXPLOSION_RADIUS);

        if (ofloat != null) {
            Random random = loottableinfo.a();
            float f = 1.0F / ofloat;
            int i = itemstack.getCount();
            int j = 0;

            for (int k = 0; k < i; ++k) {
                if (random.nextFloat() <= f) {
                    ++j;
                }
            }

            itemstack.setCount(j);
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> c() {
        return a(LootItemFunctionExplosionDecay::new);
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionExplosionDecay> {

        public a() {}

        @Override
        public LootItemFunctionExplosionDecay b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionExplosionDecay(alootitemcondition);
        }
    }
}
