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

    public static final CriterionConditionDamageSource ANY = CriterionConditionDamageSource.a.damageType().build();
    @Nullable
    private final Boolean isProjectile;
    @Nullable
    private final Boolean isExplosion;
    @Nullable
    private final Boolean bypassesArmor;
    @Nullable
    private final Boolean bypassesInvulnerability;
    @Nullable
    private final Boolean bypassesMagic;
    @Nullable
    private final Boolean isFire;
    @Nullable
    private final Boolean isMagic;
    @Nullable
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

    public boolean matches(EntityPlayer entityplayer, DamageSource damagesource) {
        return this.matches(entityplayer.getLevel(), entityplayer.position(), damagesource);
    }

    public boolean matches(WorldServer worldserver, Vec3D vec3d, DamageSource damagesource) {
        return this == CriterionConditionDamageSource.ANY ? true : (this.isProjectile != null && this.isProjectile != damagesource.isProjectile() ? false : (this.isExplosion != null && this.isExplosion != damagesource.isExplosion() ? false : (this.bypassesArmor != null && this.bypassesArmor != damagesource.isBypassArmor() ? false : (this.bypassesInvulnerability != null && this.bypassesInvulnerability != damagesource.isBypassInvul() ? false : (this.bypassesMagic != null && this.bypassesMagic != damagesource.isBypassMagic() ? false : (this.isFire != null && this.isFire != damagesource.isFire() ? false : (this.isMagic != null && this.isMagic != damagesource.isMagic() ? false : (this.isLightning != null && this.isLightning != (damagesource == DamageSource.LIGHTNING_BOLT) ? false : (!this.directEntity.matches(worldserver, vec3d, damagesource.getDirectEntity()) ? false : this.sourceEntity.matches(worldserver, vec3d, damagesource.getEntity()))))))))));
    }

    public static CriterionConditionDamageSource fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "damage type");
            Boolean obool = getOptionalBoolean(jsonobject, "is_projectile");
            Boolean obool1 = getOptionalBoolean(jsonobject, "is_explosion");
            Boolean obool2 = getOptionalBoolean(jsonobject, "bypasses_armor");
            Boolean obool3 = getOptionalBoolean(jsonobject, "bypasses_invulnerability");
            Boolean obool4 = getOptionalBoolean(jsonobject, "bypasses_magic");
            Boolean obool5 = getOptionalBoolean(jsonobject, "is_fire");
            Boolean obool6 = getOptionalBoolean(jsonobject, "is_magic");
            Boolean obool7 = getOptionalBoolean(jsonobject, "is_lightning");
            CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.fromJson(jsonobject.get("direct_entity"));
            CriterionConditionEntity criterionconditionentity1 = CriterionConditionEntity.fromJson(jsonobject.get("source_entity"));

            return new CriterionConditionDamageSource(obool, obool1, obool2, obool3, obool4, obool5, obool6, obool7, criterionconditionentity, criterionconditionentity1);
        } else {
            return CriterionConditionDamageSource.ANY;
        }
    }

    @Nullable
    private static Boolean getOptionalBoolean(JsonObject jsonobject, String s) {
        return jsonobject.has(s) ? ChatDeserializer.getAsBoolean(jsonobject, s) : null;
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionDamageSource.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            this.addOptionally(jsonobject, "is_projectile", this.isProjectile);
            this.addOptionally(jsonobject, "is_explosion", this.isExplosion);
            this.addOptionally(jsonobject, "bypasses_armor", this.bypassesArmor);
            this.addOptionally(jsonobject, "bypasses_invulnerability", this.bypassesInvulnerability);
            this.addOptionally(jsonobject, "bypasses_magic", this.bypassesMagic);
            this.addOptionally(jsonobject, "is_fire", this.isFire);
            this.addOptionally(jsonobject, "is_magic", this.isMagic);
            this.addOptionally(jsonobject, "is_lightning", this.isLightning);
            jsonobject.add("direct_entity", this.directEntity.serializeToJson());
            jsonobject.add("source_entity", this.sourceEntity.serializeToJson());
            return jsonobject;
        }
    }

    private void addOptionally(JsonObject jsonobject, String s, @Nullable Boolean obool) {
        if (obool != null) {
            jsonobject.addProperty(s, obool);
        }

    }

    public static class a {

        @Nullable
        private Boolean isProjectile;
        @Nullable
        private Boolean isExplosion;
        @Nullable
        private Boolean bypassesArmor;
        @Nullable
        private Boolean bypassesInvulnerability;
        @Nullable
        private Boolean bypassesMagic;
        @Nullable
        private Boolean isFire;
        @Nullable
        private Boolean isMagic;
        @Nullable
        private Boolean isLightning;
        private CriterionConditionEntity directEntity;
        private CriterionConditionEntity sourceEntity;

        public a() {
            this.directEntity = CriterionConditionEntity.ANY;
            this.sourceEntity = CriterionConditionEntity.ANY;
        }

        public static CriterionConditionDamageSource.a damageType() {
            return new CriterionConditionDamageSource.a();
        }

        public CriterionConditionDamageSource.a isProjectile(Boolean obool) {
            this.isProjectile = obool;
            return this;
        }

        public CriterionConditionDamageSource.a isExplosion(Boolean obool) {
            this.isExplosion = obool;
            return this;
        }

        public CriterionConditionDamageSource.a bypassesArmor(Boolean obool) {
            this.bypassesArmor = obool;
            return this;
        }

        public CriterionConditionDamageSource.a bypassesInvulnerability(Boolean obool) {
            this.bypassesInvulnerability = obool;
            return this;
        }

        public CriterionConditionDamageSource.a bypassesMagic(Boolean obool) {
            this.bypassesMagic = obool;
            return this;
        }

        public CriterionConditionDamageSource.a isFire(Boolean obool) {
            this.isFire = obool;
            return this;
        }

        public CriterionConditionDamageSource.a isMagic(Boolean obool) {
            this.isMagic = obool;
            return this;
        }

        public CriterionConditionDamageSource.a isLightning(Boolean obool) {
            this.isLightning = obool;
            return this;
        }

        public CriterionConditionDamageSource.a direct(CriterionConditionEntity criterionconditionentity) {
            this.directEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamageSource.a direct(CriterionConditionEntity.a criterionconditionentity_a) {
            this.directEntity = criterionconditionentity_a.build();
            return this;
        }

        public CriterionConditionDamageSource.a source(CriterionConditionEntity criterionconditionentity) {
            this.sourceEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionDamageSource.a source(CriterionConditionEntity.a criterionconditionentity_a) {
            this.sourceEntity = criterionconditionentity_a.build();
            return this;
        }

        public CriterionConditionDamageSource build() {
            return new CriterionConditionDamageSource(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
        }
    }
}
