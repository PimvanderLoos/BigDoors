package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction extends LootItemFunctionConditional {

    final TagKey<Instrument> options;

    SetInstrumentFunction(LootItemCondition[] alootitemcondition, TagKey<Instrument> tagkey) {
        super(alootitemcondition);
        this.options = tagkey;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_INSTRUMENT;
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        InstrumentItem.setRandom(itemstack, this.options, loottableinfo.getRandom());
        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> setInstrumentOptions(TagKey<Instrument> tagkey) {
        return simpleBuilder((alootitemcondition) -> {
            return new SetInstrumentFunction(alootitemcondition, tagkey);
        });
    }

    public static class a extends LootItemFunctionConditional.c<SetInstrumentFunction> {

        public a() {}

        public void serialize(JsonObject jsonobject, SetInstrumentFunction setinstrumentfunction, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) setinstrumentfunction, jsonserializationcontext);
            jsonobject.addProperty("options", "#" + setinstrumentfunction.options.location());
        }

        @Override
        public SetInstrumentFunction deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            String s = ChatDeserializer.getAsString(jsonobject, "options");

            if (!s.startsWith("#")) {
                throw new JsonSyntaxException("Inline tag value not supported: " + s);
            } else {
                return new SetInstrumentFunction(alootitemcondition, TagKey.create(Registries.INSTRUMENT, new MinecraftKey(s.substring(1))));
            }
        }
    }
}
