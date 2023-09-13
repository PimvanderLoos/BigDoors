package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerKilled extends CriterionTriggerAbstract<CriterionTriggerKilled.a> {

    private final MinecraftKey a;

    public CriterionTriggerKilled(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    @Override
    public MinecraftKey a() {
        return this.a;
    }

    @Override
    public CriterionTriggerKilled.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        return new CriterionTriggerKilled.a(this.a, criterionconditionentity_b, CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext), CriterionConditionDamageSource.a(jsonobject.get("killing_blow")));
    }

    public void a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggerkilled_a) -> {
            return criteriontriggerkilled_a.a(entityplayer, loottableinfo, damagesource);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b a;
        private final CriterionConditionDamageSource b;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionDamageSource criterionconditiondamagesource) {
            super(minecraftkey, criterionconditionentity_b);
            this.a = criterionconditionentity_b1;
            this.b = criterionconditiondamagesource;
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.b.a, CriterionConditionEntity.b.a, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), CriterionConditionDamageSource.a);
        }

        public static CriterionTriggerKilled.a c() {
            return new CriterionTriggerKilled.a(CriterionTriggers.b.a, CriterionConditionEntity.b.a, CriterionConditionEntity.b.a, CriterionConditionDamageSource.a);
        }

        public static CriterionTriggerKilled.a a(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.b.a, CriterionConditionEntity.b.a, CriterionConditionEntity.b.a(criterionconditionentity_a.b()), criterionconditiondamagesource_a.b());
        }

        public static CriterionTriggerKilled.a d() {
            return new CriterionTriggerKilled.a(CriterionTriggers.c.a, CriterionConditionEntity.b.a, CriterionConditionEntity.b.a, CriterionConditionDamageSource.a);
        }

        public boolean a(EntityPlayer entityplayer, LootTableInfo loottableinfo, DamageSource damagesource) {
            return !this.b.a(entityplayer, damagesource) ? false : this.a.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("entity", this.a.a(lootserializationcontext));
            jsonobject.add("killing_blow", this.b.a());
            return jsonobject;
        }
    }
}
