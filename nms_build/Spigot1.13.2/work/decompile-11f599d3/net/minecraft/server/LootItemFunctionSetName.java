package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import javax.annotation.Nullable;

public class LootItemFunctionSetName extends LootItemFunction {

    private final IChatBaseComponent a;

    public LootItemFunctionSetName(LootItemCondition[] alootitemcondition, @Nullable IChatBaseComponent ichatbasecomponent) {
        super(alootitemcondition);
        this.a = ichatbasecomponent;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        if (this.a != null) {
            itemstack.a(this.a);
        }

        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootItemFunctionSetName> {

        public a() {
            super(new MinecraftKey("set_name"), LootItemFunctionSetName.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetName lootitemfunctionsetname, JsonSerializationContext jsonserializationcontext) {
            if (lootitemfunctionsetname.a != null) {
                jsonobject.add("name", IChatBaseComponent.ChatSerializer.b(lootitemfunctionsetname.a));
            }

        }

        public LootItemFunctionSetName b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(jsonobject.get("name"));

            return new LootItemFunctionSetName(alootitemcondition, ichatbasecomponent);
        }
    }
}
