package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionSetDamage extends LootItemFunctionConditional {

    private static final Logger LOGGER = LogManager.getLogger();
    final NumberProvider damage;
    final boolean add;

    LootItemFunctionSetDamage(LootItemCondition[] alootitemcondition, NumberProvider numberprovider, boolean flag) {
        super(alootitemcondition);
        this.damage = numberprovider;
        this.add = flag;
    }

    @Override
    public LootItemFunctionType a() {
        return LootItemFunctions.SET_DAMAGE;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return this.damage.b();
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.f()) {
            int i = itemstack.i();
            float f = this.add ? 1.0F - (float) itemstack.getDamage() / (float) i : 0.0F;
            float f1 = 1.0F - MathHelper.a(this.damage.b(loottableinfo) + f, 0.0F, 1.0F);

            itemstack.setDamage(MathHelper.d(f1 * (float) i));
        } else {
            LootItemFunctionSetDamage.LOGGER.warn("Couldn't set damage of loot item {}", itemstack);
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> a(NumberProvider numberprovider) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetDamage(alootitemcondition, numberprovider, false);
        });
    }

    public static LootItemFunctionConditional.a<?> a(NumberProvider numberprovider, boolean flag) {
        return a((alootitemcondition) -> {
            return new LootItemFunctionSetDamage(alootitemcondition, numberprovider, flag);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionSetDamage> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionSetDamage lootitemfunctionsetdamage, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionsetdamage, jsonserializationcontext);
            jsonobject.add("damage", jsonserializationcontext.serialize(lootitemfunctionsetdamage.damage));
            jsonobject.addProperty("add", lootitemfunctionsetdamage.add);
        }

        @Override
        public LootItemFunctionSetDamage b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            NumberProvider numberprovider = (NumberProvider) ChatDeserializer.a(jsonobject, "damage", jsondeserializationcontext, NumberProvider.class);
            boolean flag = ChatDeserializer.a(jsonobject, "add", false);

            return new LootItemFunctionSetDamage(alootitemcondition, numberprovider, flag);
        }
    }
}
