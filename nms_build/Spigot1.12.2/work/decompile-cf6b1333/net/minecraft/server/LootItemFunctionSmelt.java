package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionSmelt extends LootItemFunction {

    private static final Logger a = LogManager.getLogger();

    public LootItemFunctionSmelt(LootItemCondition[] alootitemcondition) {
        super(alootitemcondition);
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        if (itemstack.isEmpty()) {
            return itemstack;
        } else {
            ItemStack itemstack1 = RecipesFurnace.getInstance().getResult(itemstack);

            if (itemstack1.isEmpty()) {
                LootItemFunctionSmelt.a.warn("Couldn\'t smelt {} because there is no smelting recipe", itemstack);
                return itemstack;
            } else {
                ItemStack itemstack2 = itemstack1.cloneItemStack();

                itemstack2.setCount(itemstack.getCount());
                return itemstack2;
            }
        }
    }

    public static class a extends LootItemFunction.a<LootItemFunctionSmelt> {

        protected a() {
            super(new MinecraftKey("furnace_smelt"), LootItemFunctionSmelt.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSmelt lootitemfunctionsmelt, JsonSerializationContext jsonserializationcontext) {}

        public LootItemFunctionSmelt a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return new LootItemFunctionSmelt(alootitemcondition);
        }

        public LootItemFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return this.a(jsonobject, jsondeserializationcontext, alootitemcondition);
        }
    }
}
