package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerKilled extends CriterionTriggerAbstract<CriterionTriggerKilled.a> {

    final MinecraftKey id;

    public CriterionTriggerKilled(MinecraftKey minecraftkey) {
        this.id = minecraftkey;
    }

    @Override
    public MinecraftKey a() {
        return this.id;
    }

    @Override
    public CriterionTriggerKilled.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        return new CriterionTriggerKilled.a(this.id, criterionconditionentity_b, CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext), CriterionConditionDamageSource.a(jsonobject.get("killing_blow")));
    }

    public void a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggerkilled_a) -> {
            return criteriontriggerkilled_a.a(entityplayer, loottableinfo, damagesource);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b entityPredicate;
        private final CriterionConditionDamageSource killingBlow;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionDamageSource criterionconditiondamagesource) {
            super(minecraftkey, criterionconditionentity_b);
            this.entityPredicate = criterionconditionentity_b1;
            this.killingBlow = criterionconditiondamagesource;
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a c() {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), criterionconditiondamagesource_a.b());
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), criterionconditiondamagesource_a.b());
        }

        public static CriterionTriggerKilled.a b(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a b(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a d() {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a b(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a b(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a b(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity), criterionconditiondamagesource_a.b());
        }

        public static CriterionTriggerKilled.a b(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), criterionconditiondamagesource_a.b());
        }

        public boolean a(EntityPlayer entityplayer, LootTableInfo loottableinfo, DamageSource damagesource) {
            return !this.killingBlow.a(entityplayer, damagesource) ? false : this.entityPredicate.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("entity", this.entityPredicate.a(lootserializationcontext));
            jsonobject.add("killing_blow", this.killingBlow.a());
            return jsonobject;
        }
    }
}
