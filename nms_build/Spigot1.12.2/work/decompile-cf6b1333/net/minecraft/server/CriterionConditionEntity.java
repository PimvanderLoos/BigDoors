package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class CriterionConditionEntity {

    public static final CriterionConditionEntity a = new CriterionConditionEntity((MinecraftKey) null, CriterionConditionDistance.a, CriterionConditionLocation.a, CriterionConditionMobEffect.a, CriterionConditionNBT.a);
    private final MinecraftKey b;
    private final CriterionConditionDistance c;
    private final CriterionConditionLocation d;
    private final CriterionConditionMobEffect e;
    private final CriterionConditionNBT f;

    public CriterionConditionEntity(@Nullable MinecraftKey minecraftkey, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionNBT criterionconditionnbt) {
        this.b = minecraftkey;
        this.c = criterionconditiondistance;
        this.d = criterionconditionlocation;
        this.e = criterionconditionmobeffect;
        this.f = criterionconditionnbt;
    }

    public boolean a(EntityPlayer entityplayer, @Nullable Entity entity) {
        return this == CriterionConditionEntity.a ? true : (entity == null ? false : (this.b != null && !EntityTypes.a(entity, this.b) ? false : (!this.c.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entity.locX, entity.locY, entity.locZ) ? false : (!this.d.a(entityplayer.x(), entity.locX, entity.locY, entity.locZ) ? false : (!this.e.a(entity) ? false : this.f.a(entity))))));
    }

    public static CriterionConditionEntity a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "entity");
            MinecraftKey minecraftkey = null;

            if (jsonobject.has("type")) {
                minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "type"));
                if (!EntityTypes.b(minecraftkey)) {
                    throw new JsonSyntaxException("Unknown entity type \'" + minecraftkey + "\', valid types are: " + EntityTypes.b());
                }
            }

            CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));
            CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("location"));
            CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.a(jsonobject.get("effects"));
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));

            return new CriterionConditionEntity(minecraftkey, criterionconditiondistance, criterionconditionlocation, criterionconditionmobeffect, criterionconditionnbt);
        } else {
            return CriterionConditionEntity.a;
        }
    }
}
