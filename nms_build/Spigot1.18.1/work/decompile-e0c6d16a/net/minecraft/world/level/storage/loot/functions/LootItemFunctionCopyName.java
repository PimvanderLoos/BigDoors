package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.INamableTileEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionCopyName extends LootItemFunctionConditional {

    final LootItemFunctionCopyName.Source source;

    LootItemFunctionCopyName(LootItemCondition[] alootitemcondition, LootItemFunctionCopyName.Source lootitemfunctioncopyname_source) {
        super(alootitemcondition);
        this.source = lootitemfunctioncopyname_source;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_NAME;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.source.param);
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        Object object = loottableinfo.getParamOrNull(this.source.param);

        if (object instanceof INamableTileEntity) {
            INamableTileEntity inamabletileentity = (INamableTileEntity) object;

            if (inamabletileentity.hasCustomName()) {
                itemstack.setHoverName(inamabletileentity.getDisplayName());
            }
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> copyName(LootItemFunctionCopyName.Source lootitemfunctioncopyname_source) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionCopyName(alootitemcondition, lootitemfunctioncopyname_source);
        });
    }

    public static enum Source {

        THIS("this", LootContextParameters.THIS_ENTITY), KILLER("killer", LootContextParameters.KILLER_ENTITY), KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER), BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY);

        public final String name;
        public final LootContextParameter<?> param;

        private Source(String s, LootContextParameter lootcontextparameter) {
            this.name = s;
            this.param = lootcontextparameter;
        }

        public static LootItemFunctionCopyName.Source getByName(String s) {
            LootItemFunctionCopyName.Source[] alootitemfunctioncopyname_source = values();
            int i = alootitemfunctioncopyname_source.length;

            for (int j = 0; j < i; ++j) {
                LootItemFunctionCopyName.Source lootitemfunctioncopyname_source = alootitemfunctioncopyname_source[j];

                if (lootitemfunctioncopyname_source.name.equals(s)) {
                    return lootitemfunctioncopyname_source;
                }
            }

            throw new IllegalArgumentException("Invalid name source " + s);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionCopyName> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionCopyName lootitemfunctioncopyname, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctioncopyname, jsonserializationcontext);
            jsonobject.addProperty("source", lootitemfunctioncopyname.source.name);
        }

        @Override
        public LootItemFunctionCopyName deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootItemFunctionCopyName.Source lootitemfunctioncopyname_source = LootItemFunctionCopyName.Source.getByName(ChatDeserializer.getAsString(jsonobject, "source"));

            return new LootItemFunctionCopyName(alootitemcondition, lootitemfunctioncopyname_source);
        }
    }
}
