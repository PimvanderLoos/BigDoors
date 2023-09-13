package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.slf4j.Logger;

public class LootItemFunctionSetDamage extends LootItemFunctionConditional {

    private static final Logger LOGGER = LogUtils.getLogger();
    final NumberProvider damage;
    final boolean add;

    LootItemFunctionSetDamage(LootItemCondition[] alootitemcondition, NumberProvider numberprovider, boolean flag) {
        super(alootitemcondition);
        this.damage = numberprovider;
        this.add = flag;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_DAMAGE;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return this.damage.getReferencedContextParams();
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.isDamageableItem()) {
            int i = itemstack.getMaxDamage();
            float f = this.add ? 1.0F - (float) itemstack.getDamageValue() / (float) i : 0.0F;
            float f1 = 1.0F - MathHelper.clamp(this.damage.getFloat(loottableinfo) + f, 0.0F, 1.0F);

            itemstack.setDamageValue(MathHelper.floor(f1 * (float) i));
        } else {
            LootItemFunctionSetDamage.LOGGER.warn("Couldn't set damage of loot item {}", itemstack);
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> setDamage(NumberProvider numberprovider) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionSetDamage(alootitemcondition, numberprovider, false);
        });
    }

    public static LootItemFunctionConditional.a<?> setDamage(NumberProvider numberprovider, boolean flag) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionSetDamage(alootitemcondition, numberprovider, flag);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetDamage> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionSetDamage lootitemfunctionsetdamage, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetdamage, jsonserializationcontext);
            jsonobject.add("damage", jsonserializationcontext.serialize(lootitemfunctionsetdamage.damage));
            jsonobject.addProperty("add", lootitemfunctionsetdamage.add);
        }

        @Override
        public LootItemFunctionSetDamage deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.getAsObject(jsonobject, "damage", jsondeserializationcontext, NumberProvider.class);
            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "add", false);

            return new LootItemFunctionSetDamage(alootitemcondition, numberprovider, flag);
        }
    }
}
