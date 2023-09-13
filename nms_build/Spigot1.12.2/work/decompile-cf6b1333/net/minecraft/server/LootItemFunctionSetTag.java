package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Random;

public class LootItemFunctionSetTag extends LootItemFunction {

    private final NBTTagCompound a;

    public LootItemFunctionSetTag(LootItemCondition[] alootitemcondition, NBTTagCompound nbttagcompound) {
        super(alootitemcondition);
        this.a = nbttagcompound;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound == null) {
            nbttagcompound = this.a.g();
        } else {
            nbttagcompound.a(this.a);
        }

        itemstack.setTag(nbttagcompound);
        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootItemFunctionSetTag> {

        public a() {
            super(new MinecraftKey("set_nbt"), LootItemFunctionSetTag.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetTag lootitemfunctionsettag, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("tag", lootitemfunctionsettag.a.toString());
        }

        public LootItemFunctionSetTag a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            try {
                NBTTagCompound nbttagcompound = MojangsonParser.parse(ChatDeserializer.h(jsonobject, "tag"));

                return new LootItemFunctionSetTag(alootitemcondition, nbttagcompound);
            } catch (MojangsonParseException mojangsonparseexception) {
                throw new JsonSyntaxException(mojangsonparseexception);
            }
        }

        public LootItemFunction b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            return this.a(jsonobject, jsondeserializationcontext, alootitemcondition);
        }
    }
}
