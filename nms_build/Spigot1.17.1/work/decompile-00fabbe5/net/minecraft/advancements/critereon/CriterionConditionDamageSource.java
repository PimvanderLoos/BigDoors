package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3D;

public class CriterionConditionDamageSource {

    public static final CriterionConditionDamageSource ANY = CriterionConditionDamageSource.a.a().b();
    private final Boolean isProjectile;
    private final Boolean isExplosion;
    private final Boolean bypassesArmor;
    private final Boolean bypassesInvulnerability;
    private final Boolean bypassesMagic;
    private final Boolean isFire;
    private final Boolean isMagic;
    private final Boolean isLightning;
    private final CriterionConditionEntity directEntity;
    private final CriterionConditionEntity sourceEntity;

    public CriterionConditionDamageSource(@Nullable Boolean obool, @Nullable Boolean obool1, @Nullable Boolean obool2, @Nullable Boolean obool3, @Nullable Boolean obool4, @Nullable Boolean obool5, @Nullable Boolean obool6, @Nullable Boolean obool7, CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1) {
        this.isProjectile = obool;
        this.isExplosion = obool1;
        this.bypassesArmor = obool2;
        this.bypassesInvulnerability = obool3;
        this.bypassesMagic = obool4;
        this.isFire = obool5;
        this.isMagic = obool6;
        this.isLightning = obool7;
        this.directEntity = criterionconditionentity;
        this.sourceEntity = criterionconditionentity1;
    }

    public boolean a(EntityPlayer entityplayer, DamageSource damagesource) {
        return this.a(entityplayer.getWorldServer(), entityplayer.getPositionVector(), damagesource);
    }

    public boolean a(WorldServer worldserver, Vec3D vec3d, DamageSource damagesource) {
        return this == CriterionConditionDamageSource.ANY ? true : (this.isProjectile != null && this.isProjectile != damagesource.b() ? false : (this.isExplosion != null && this.isExplosion != damagesource.isExplosion() ? false : (this.bypassesArmor != null && this.bypassesArmor != damagesource.ignoresArmor() ? false : (this.bypassesInvulnerability != null && this.bypassesInvulnerability != damagesource.ignoresInvulnerability() ? false : (this.bypassesMagic != null && this.bypassesMagic != damagesource.isStarvation() ? false : (this.isFire != null && this.isFire != damagesource.isFire() ? false : (this.isMagic != null && this.isMagic != damagesource.isMagic() ? false : (this.isLightning != null && this.isLightning != (damagesource == DamageSource.LIGHTNING_BOLT) ? false : (!this.directEntity.a(worldserver, vec3d, damagesource.k()) ? false : this.sourceEntity.a(worldserver, vec3d, damagesource.getEntity()))))))))));
    }

    public static CriterionConditionDamageSource a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "damage type");
            Boolean obool = a(jsonobject, "is_projectile");
            Boolean obool1 = a(jsonobject, "is_explosion");
            Boolean obool2 = a(jsonobject, "bypasses_armor");
            Boolean obool3 = a(jsonobject, "bypasses_invulnerability");
            Boolean obool4 = a(jsonobject, "bypasses_magic");
            Boolean obool5 = a(jsonobject, "is_fire");
            Boolean obool6 = a(jsonobject, "is_magic");
            Boolean obool7 = a(jsonobject, "is_lightning");
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonobject.get("direct_entity"));
            CriterionConditionEntity criterionconditionentity1 = CriterionConditionEntity.a(jsonobject.get("source_entity"));

            return new CriterionConditionDamageSource(obool, obool1, obool2, obool3, obool4, obool5, obool6, obool7, criterionconditionentity, criterionconditionentity1);
        } else {
            return CriterionConditionDamageSource.ANY;
        }
    }

    @Nullable
    private static Boolean a(JsonObject jsonobject, String s) {
        return jsonobject.has(s) ? ChatDeserializer.j(jsonobject, s) : null;
    }

    public JsonElement a() {
        if (this == CriterionConditionDamageSource.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            this.a(jsonobject, "is_projectile", this.isProjectile);
            this.a(jsonobject, "is_explosion", this.isExplosion);
            this.a(jsonobject, "bypasses_armor", this.bypassesArmor);
            this.a(jsonobject, "bypasses_invulnerability", this.bypassesInvulnerability);
            this.a(jsonobject, "bypasses_magic", this.bypassesMagic);
            this.a(jsonobject, "is_fire", this.isFire);
            this.a(jsonobject, "is_magic", this.isMagic);
            this.a(jsonobject, "is_lightning", this.isLightning);
            jsonobject.add("direct_entity", this.directEntity.a());
            jsonobject.add("source_entity", this.sourceEntity.a());
            return jsonobject;
        }
    }

    private void a(JsonObject jsonobject, String s, @Nullable Boolean obool) {
        if (obool != null) {
            jsonobject.addProperty(s, obool);
        }

    }

    public static class a {

        private Boolean isProjectile;
        private Boolean isExplosion;
        private Boolean bypassesArmor;
        private Boolean bypassesInvulnerability;
        private Boolean bypassesMagic;
        private Boolean isFire;
        private Boolean isMagic;
        private Boolean isLightning;
        private CriterionConditionEntity directEntity;
        private CriterionConditionEntity sourceEntity;

        public a() {
            this.directEntity = CriterionConditionEntity.ANY;
            this.sourceEntity = CriterionConditionEntity.ANY;
        }

        public static CriterionConditionDamageSource.a a() {
            return new CriterionConditionDamageSource.a();
        }

        public CriterionConditionDamageSource.a a(Boolean obool) {
            this.isProjectile = obool;
            return this;
        }

        public CriterionConditionDamageSource.a b(Boolean obool) {
            this.isExplosion = obool;
            return this;
        }

        public CriterionConditionDamageSource.a c(Boolean obool) {
            this.bypassesArmor = obool;
            return this;
        }

        public CriterionConditionDamageSource.a d(Boolean obool) {
            this.bypassesInvulnerability = obool;
            return this;
        }

        public CriterionConditionDamageSource.a e(Boolean obool) {
            this.bypassesMagic = obool;
            return this;
        }

        public CriterionConditionDamageSource.a f(Boolean obool) {
            this.isFire = obool;
            return this;
        }

        public CriterionConditionDamageSource.a g(Boolean obool) {
            this.isMagic = obool;
            return this;
        }

        public CriterionConditionDamageSource.a h(Boolean obool) {
            this.isLightning = obool;
            return this;
        }

        public CriterionConditionDamageSource.a a(CriterionConditionEntity criterionconditionentity) {
            this.directEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamageSource.a a(CriterionConditionEntity.a criterionconditionentity_a) {
            this.directEntity = criterionconditionentity_a.b();
            return this;
        }

        public CriterionConditionDamageSource.a b(CriterionConditionEntity criterionconditionentity) {
            this.sourceEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamageSource.a b(CriterionConditionEntity.a criterionconditionentity_a) {
            this.sourceEntity = criterionconditionentity_a.b();
            return this;
        }

        public CriterionConditionDamageSource b() {
            return new CriterionConditionDamageSource(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }
}
