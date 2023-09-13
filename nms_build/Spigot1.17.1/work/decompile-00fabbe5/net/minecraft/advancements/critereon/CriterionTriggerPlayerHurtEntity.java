package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerPlayerHurtEntity extends CriterionTriggerAbstract<CriterionTriggerPlayerHurtEntity.a> {

    static final MinecraftKey ID = new MinecraftKey("player_hurt_entity");

    public CriterionTriggerPlayerHurtEntity() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerPlayerHurtEntity.ID;
    }

    @Override
    public CriterionTriggerPlayerHurtEntity.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionDamage criterionconditiondamage = CriterionConditionDamage.a(jsonobject.get("damage"));
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.a(jsonobject, "entity", lootdeserializationcontext);

        return new CriterionTriggerPlayerHurtEntity.a(criterionconditionentity_b, criterionconditiondamage, criterionconditionentity_b1);
    }

    public void a(EntityPlayer entityplayer, Entity entity, DamageSource damagesource, float f, float f1, boolean flag) {
        LootTableInfo loottableinfo = CriterionConditionEntity.b(entityplayer, entity);

        this.a(entityplayer, (criteriontriggerplayerhurtentity_a) -> {
            return criteriontriggerplayerhurtentity_a.a(entityplayer, loottableinfo, damagesource, f, f1, flag);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionDamage damage;
        private final CriterionConditionEntity.b entity;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionDamage criterionconditiondamage, CriterionConditionEntity.b criterionconditionentity_b1) {
            super(CriterionTriggerPlayerHurtEntity.ID, criterionconditionentity_b);
            this.damage = criterionconditiondamage;
            this.entity = criterionconditionentity_b1;
        }

        public static CriterionTriggerPlayerHurtEntity.a c() {
            return new CriterionTriggerPlayerHurtEntity.a(CriterionConditionEntity.b.ANY, CriterionConditionDamage.ANY, CriterionConditionEntity.b.ANY);
        }

        public static CriterionTriggerPlayerHurtEntity.a a(CriterionConditionDamage criterionconditiondamage) {
            return new CriterionTriggerPlayerHurtEntity.a(CriterionConditionEntity.b.ANY, criterionconditiondamage, CriterionConditionEntity.b.ANY);
        }

        public static CriterionTriggerPlayerHurtEntity.a a(CriterionConditionDamage.a criterionconditiondamage_a) {
            return new CriterionTriggerPlayerHurtEntity.a(CriterionConditionEntity.b.ANY, criterionconditiondamage_a.b(), CriterionConditionEntity.b.ANY);
        }

        public static CriterionTriggerPlayerHurtEntity.a a(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerPlayerHurtEntity.a(CriterionConditionEntity.b.ANY, CriterionConditionDamage.ANY, CriterionConditionEntity.b.a(criterionconditionentity));
        }

        public static CriterionTriggerPlayerHurtEntity.a a(CriterionConditionDamage criterionconditiondamage, CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerPlayerHurtEntity.a(CriterionConditionEntity.b.ANY, criterionconditiondamage, CriterionConditionEntity.b.a(criterionconditionentity));
        }

        public static CriterionTriggerPlayerHurtEntity.a a(CriterionConditionDamage.a criterionconditiondamage_a, CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerPlayerHurtEntity.a(CriterionConditionEntity.b.ANY, criterionconditiondamage_a.b(), CriterionConditionEntity.b.a(criterionconditionentity));
        }

        public boolean a(EntityPlayer entityplayer, LootTableInfo loottableinfo, DamageSource damagesource, float f, float f1, boolean flag) {
            return !this.damage.a(entityplayer, damagesource, f, f1, flag) ? false : this.entity.a(loottableinfo);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("damage", this.damage.a());
            jsonobject.add("entity", this.entity.a(lootserializationcontext));
            return jsonobject;
        }
    }
}
