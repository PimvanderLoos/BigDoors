package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.CriterionConditionEntity;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.phys.Vec3D;

public class LootItemConditionEntityProperty implements LootItemCondition {

    final CriterionConditionEntity predicate;
    final LootTableInfo.EntityTarget entityTarget;

    LootItemConditionEntityProperty(CriterionConditionEntity criterionconditionentity, LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        this.predicate = criterionconditionentity;
        this.entityTarget = loottableinfo_entitytarget;
    }

    @Override
    public LootItemConditionType a() {
        return LootItemConditions.ENTITY_PROPERTIES;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of(LootContextParameters.ORIGIN, this.entityTarget.a());
    }

    public boolean test(LootTableInfo loottableinfo) {
        Entity entity = (Entity) loottableinfo.getContextParameter(this.entityTarget.a());
        Vec3D vec3d = (Vec3D) loottableinfo.getContextParameter(LootContextParameters.ORIGIN);

        return this.predicate.a(loottableinfo.getWorld(), vec3d, entity);
    }

    public static LootItemCondition.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return a(loottableinfo_entitytarget, CriterionConditionEntity.a.a());
    }

    public static LootItemCondition.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget, CriterionConditionEntity.a criterionconditionentity_a) {
        return () -> {
            return new LootItemConditionEntityProperty(criterionconditionentity_a.b(), loottableinfo_entitytarget);
        };
    }

    public static LootItemCondition.a a(LootTableInfo.EntityTarget loottableinfo_entitytarget, CriterionConditionEntity criterionconditionentity) {
        return () -> {
            return new LootItemConditionEntityProperty(criterionconditionentity, loottableinfo_entitytarget);
        };
    }

    public static class a implements LootSerializer<LootItemConditionEntityProperty> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemConditionEntityProperty lootitemconditionentityproperty, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("predicate", lootitemconditionentityproperty.predicate.a());
            jsonobject.add("entity", jsonserializationcontext.serialize(lootitemconditionentityproperty.entityTarget));
        }

        @Override
        public LootItemConditionEntityProperty a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("predicate"));

            return new LootItemConditionEntityProperty(criterionconditionentity, (LootTableInfo.EntityTarget) ChatDeserializer.a(jsonobject, "entity", jsondeserializationcontext, LootTableInfo.EntityTarget.class));
        }
    }
}
