package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionSetTag extends LootItemFunctionConditional {

    final NBTTagCompound tag;

    LootItemFunctionSetTag(LootItemCondition[] alootitemcondition, NBTTagCompound nbttagcompound) {
        super(alootitemcondition);
        this.tag = nbttagcompound;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_NBT;
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        itemstack.getOrCreateTag().a(this.tag);
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> a(NBTTagCompound nbttagcompound) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetTag(alootitemcondition, nbttagcompound);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetTag> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetTag lootitemfunctionsettag, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsettag, jsonserializationcontext);
            jsonobject.addProperty("tag", lootitemfunctionsettag.tag.toString());
        }

        @Override
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
