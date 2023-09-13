package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootValueBounds;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionSetDamage extends LootItemFunctionConditional {

    private static final Logger LOGGER = LogManager.getLogger();
    private final LootValueBounds b;

    private LootItemFunctionSetDamage(LootItemCondition[] alootitemcondition, LootValueBounds lootvaluebounds) {
        super(alootitemcondition);
        this.b = lootvaluebounds;
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.h;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.e()) {
            float f = 1.0F - this.b.b(loottableinfo.a());

            itemstack.setDamage(MathHelper.d(f * (float) itemstack.h()));
        } else {
            LootItemFunctionSetDamage.LOGGER.warn("Couldn't set damage of loot item {}", itemstack);
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> a(LootValueBounds lootvaluebounds) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetDamage(alootitemcondition, lootvaluebounds);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetDamage> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetDamage lootitemfunctionsetdamage, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetdamage, jsonserializationcontext);
            jsonobject.add("damage", jsonserializationcontext.serialize(lootitemfunctionsetdamage.b));
        }

        @Override
        public LootItemFunctionSetDamage b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionSetDamage(alootitemcondition, (LootValueBounds) ChatDeserializer.a(jsonobject, "damage", jsondeserializationcontext, LootValueBounds.class));
        }
    }
}
