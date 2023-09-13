package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.CriterionConditionDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.Vec3D;

public class LootItemConditionDamageSourceProperties implements LootItemCondition {

    final CriterionConditionDamageSource predicate;

    LootItemConditionDamageSourceProperties(CriterionConditionDamageSource criterionconditiondamagesource) {
        this.predicate = criterionconditiondamagesource;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.DAMAGE_SOURCE_PROPERTIES;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParameters.ORIGIN, LootContextParameters.DAMAGE_SOURCE);
    }

    public boolean test(LootTableInfo loottableinfo) {
        DamageSource damagesource = (DamageSource) loottableinfo.getParamOrNull(LootContextParameters.DAMAGE_SOURCE);
        Vec3D vec3d = (Vec3D) loottableinfo.getParamOrNull(LootContextParameters.ORIGIN);

        return vec3d != null && damagesource != null && this.predicate.matches(loottableinfo.getLevel(), vec3d, damagesource);
    }

    public static LootItemCondition.a hasDamageSource(CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
        return () -> {
            return new LootItemConditionDamageSourceProperties(criterionconditiondamagesource_a.build());
        };
    }

    public static class a implements LootSerializer<LootItemConditionDamageSourceProperties> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemConditionDamageSourceProperties lootitemconditiondamagesourceproperties, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("predicate", lootitemconditiondamagesourceproperties.predicate.serializeToJson());
        }

        @Override
        public LootItemConditionDamageSourceProperties deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            CriterionConditionDamageSource criterionconditiondamagesource = CriterionConditionDamageSource.fromJson(jsonobject.get("predicate"));

            return new LootItemConditionDamageSourceProperties(criterionconditiondamagesource);
        }
    }
}
