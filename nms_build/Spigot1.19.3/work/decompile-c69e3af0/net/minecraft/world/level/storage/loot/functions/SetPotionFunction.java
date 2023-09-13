package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetPotionFunction extends LootItemFunctionConditional {

    final PotionRegistry potion;

    SetPotionFunction(LootItemCondition[] alootitemcondition, PotionRegistry potionregistry) {
        super(alootitemcondition);
        this.potion = potionregistry;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_POTION;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        PotionUtil.setPotion(itemstack, this.potion);
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> setPotion(PotionRegistry potionregistry) {
        return simpleBuilder((alootitemcondition) -> {
            return new SetPotionFunction(alootitemcondition, potionregistry);
        });
    }

    public static class a extends LootItemFunctionConditional.c<SetPotionFunction> {

        public a() {}

        public void serialize(JsonObject jsonobject, SetPotionFunction setpotionfunction, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) setpotionfunction, jsonserializationcontext);
            jsonobject.addProperty("id", BuiltInRegistries.POTION.getKey(setpotionfunction.potion).toString());
        }

        @Override
        public SetPotionFunction deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            String s = ChatDeserializer.getAsString(jsonobject, "id");
            PotionRegistry potionregistry = (PotionRegistry) BuiltInRegistries.POTION.getOptional(MinecraftKey.tryParse(s)).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown potion '" + s + "'");
            });

            return new SetPotionFunction(alootitemcondition, potionregistry);
        }
    }
}
