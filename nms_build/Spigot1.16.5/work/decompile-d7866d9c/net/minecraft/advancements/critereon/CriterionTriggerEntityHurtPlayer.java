package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class CriterionTriggerEntityHurtPlayer extends CriterionTriggerAbstract<CriterionTriggerEntityHurtPlayer.a> {

    private static final MinecraftKey a = new MinecraftKey("entity_hurt_player");

    public CriterionTriggerEntityHurtPlayer() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEntityHurtPlayer.a;
    }

    @Override
    public CriterionTriggerEntityHurtPlayer.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionDamage criterionconditiondamage = CriterionConditionDamage.a(jsonobject.get("damage"));

        return new CriterionTriggerEntityHurtPlayer.a(criterionconditionentity_b, criterionconditiondamage);
    }

    public void a(EntityPlayer entityplayer, DamageSource damagesource, float f, float f1, boolean flag) {
        this.a(entityplayer, (criteriontriggerentityhurtplayer_a) -> {
            return criteriontriggerentityhurtplayer_a.a(entityplayer, damagesource, f, f1, flag);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionDamage a;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionDamage criterionconditiondamage) {
            super(CriterionTriggerEntityHurtPlayer.a, criterionconditionentity_b);
            this.a = criterionconditiondamage;
        }

        public static CriterionTriggerEntityHurtPlayer.a a(CriterionConditionDamage.a criterionconditiondamage_a) {
            return new CriterionTriggerEntityHurtPlayer.a(CriterionConditionEntity.b.a, criterionconditiondamage_a.b());
        }

        public boolean a(EntityPlayer entityplayer, DamageSource damagesource, float f, float f1, boolean flag) {
            return this.a.a(entityplayer, damagesource, f, f1, flag);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("damage", this.a.a());
            return jsonobject;
        }
    }
}
