package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;

public class LootItemFunctionSetTag extends LootItemFunction {

    private final NBTTagCompound a;

    public LootItemFunctionSetTag(LootItemCondition[] alootitemcondition, NBTTagCompound nbttagcompound) {
        super(alootitemcondition);
        this.a = nbttagcompound;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        itemstack.getOrCreateTag().a(this.a);
        return itemstack;
    }

    public static class a extends LootItemFunction.a<LootItemFunctionSetTag> {

        public a() {
            super(new MinecraftKey("set_nbt"), LootItemFunctionSetTag.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionSetTag lootitemfunctionsettag, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("tag", lootitemfunctionsettag.a.toString());
        }

        public LootItemFunctionSetTag b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            try {
                NBTTagCompound nbttagcompound = MojangsonParser.parse(ChatDeserializer.h(jsonobject, "tag"));

                return new LootItemFunctionSetTag(alootitemcondition, nbttagcompound);
            } catch (CommandSyntaxException commandsyntaxexception) {
                throw new JsonSyntaxException(commandsyntaxexception.getMessage());
            }
        }
    }
}
