package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class LootItemConditionRandomChanceWithLooting implements LootItemCondition {

    final float percent;
    final float lootingMultiplier;

    LootItemConditionRandomChanceWithLooting(float f, float f1) {
        this.percent = f;
        this.lootingMultiplier = f1;
    }

    @Override
    public LootItemConditionType a() {
        return LootItemConditions.RANDOM_CHANCE_WITH_LOOTING;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of(LootContextParameters.KILLER_ENTITY);
    }

    public boolean test(LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getContextParameter(LootContextParameters.KILLER_ENTITY);
        int i = 0;

        if (entity instanceof EntityLiving) {
            i = EnchantmentManager.g((EntityLiving) entity);
        }

        return loottableinfo.a().nextFloat() < this.percent + (float) i * this.lootingMultiplier;
    }

    public static LootItemCondition.a a(float f, float f1) {
        return () -> {
            return new LootItemConditionRandomChanceWithLooting(f, f1);
        };
    }

    public static class a implements LootSerializer<LootItemConditionRandomChanceWithLooting> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionRandomChanceWithLooting lootitemconditionrandomchancewithlooting, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("chance", lootitemconditionrandomchancewithlooting.percent);
            jsonobject.addProperty("looting_multiplier", lootitemconditionrandomchancewithlooting.lootingMultiplier);
        }

        @Override
        public LootItemConditionRandomChanceWithLooting a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            return new LootItemConditionRandomChanceWithLooting(ChatDeserializer.l(jsonobject, "chance"), ChatDeserializer.l(jsonobject, "looting_multiplier"));
        }
    }
}
