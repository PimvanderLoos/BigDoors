package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionConditionEntity {

    public static final CriterionConditionEntity a = new CriterionConditionEntity(CriterionConditionEntityType.a, CriterionConditionDistance.a, CriterionConditionLocation.a, CriterionConditionMobEffect.a, CriterionConditionNBT.a);
    public static final CriterionConditionEntity[] b = new CriterionConditionEntity[0];
    private final CriterionConditionEntityType c;
    private final CriterionConditionDistance d;
    private final CriterionConditionLocation e;
    private final CriterionConditionMobEffect f;
    private final CriterionConditionNBT g;

    private CriterionConditionEntity(CriterionConditionEntityType criterionconditionentitytype, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionNBT criterionconditionnbt) {
        this.c = criterionconditionentitytype;
        this.d = criterionconditiondistance;
        this.e = criterionconditionlocation;
        this.f = criterionconditionmobeffect;
        this.g = criterionconditionnbt;
    }

    public boolean a(EntityPlayer entityplayer, @Nullable Entity entity) {
        return this == CriterionConditionEntity.a ? true : (entity == null ? false : (!this.c.a(entity.P()) ? false : (!this.d.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entity.locX, entity.locY, entity.locZ) ? false : (!this.e.a(entityplayer.getWorldServer(), entity.locX, entity.locY, entity.locZ) ? false : (!this.f.a(entity) ? false : this.g.a(entity))))));
    }

    public static CriterionConditionEntity a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "entity");
            CriterionConditionEntityType criterionconditionentitytype = CriterionConditionEntityType.a(jsonobject.get("type"));
            CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));
            CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("location"));
            CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.a(jsonobject.get("effects"));
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));

            return (new CriterionConditionEntity.a()).a(criterionconditionentitytype).a(criterionconditiondistance).a(criterionconditionlocation).a(criterionconditionmobeffect).a(criterionconditionnbt).b();
        } else {
            return CriterionConditionEntity.a;
        }
    }

    public static CriterionConditionEntity[] b(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = ChatDeserializer.n(jsonelement, "entities");
            CriterionConditionEntity[] acriterionconditionentity = new CriterionConditionEntity[jsonarray.size()];

            for (int i = 0; i < jsonarray.size(); ++i) {
                acriterionconditionentity[i] = a(jsonarray.get(i));
            }

            return acriterionconditionentity;
        } else {
            return CriterionConditionEntity.b;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionEntity.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("type", this.c.a());
            jsonobject.add("distance", this.d.a());
            jsonobject.add("location", this.e.a());
            jsonobject.add("effects", this.f.b());
            jsonobject.add("nbt", this.g.a());
            return jsonobject;
        }
    }

    public static JsonElement a(CriterionConditionEntity[] acriterionconditionentity) {
        if (acriterionconditionentity == CriterionConditionEntity.b) {
            return JsonNull.INSTANCE;
        } else {
            JsonArray jsonarray = new JsonArray();

            for (int i = 0; i < acriterionconditionentity.length; ++i) {
                JsonElement jsonelement = acriterionconditionentity[i].a();

                if (!jsonelement.isJsonNull()) {
                    jsonarray.add(jsonelement);
                }
            }

            return jsonarray;
        }
    }

    public static class a {

        private CriterionConditionEntityType a;
        private CriterionConditionDistance b;
        private CriterionConditionLocation c;
        private CriterionConditionMobEffect d;
        private CriterionConditionNBT e;

        public a() {
            this.a = CriterionConditionEntityType.a;
            this.b = CriterionConditionDistance.a;
            this.c = CriterionConditionLocation.a;
            this.d = CriterionConditionMobEffect.a;
            this.e = CriterionConditionNBT.a;
        }

        public static CriterionConditionEntity.a a() {
            return new CriterionConditionEntity.a();
        }

        public CriterionConditionEntity.a a(EntityTypes<?> entitytypes) {
            this.a = new CriterionConditionEntityType(entitytypes);
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionEntityType criterionconditionentitytype) {
            this.a = criterionconditionentitytype;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionDistance criterionconditiondistance) {
            this.b = criterionconditiondistance;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionLocation criterionconditionlocation) {
            this.c = criterionconditionlocation;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionMobEffect criterionconditionmobeffect) {
            this.d = criterionconditionmobeffect;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionNBT criterionconditionnbt) {
            this.e = criterionconditionnbt;
            return this;
        }

        public CriterionConditionEntity b() {
            return this.a == CriterionConditionEntityType.a && this.b == CriterionConditionDistance.a && this.c == CriterionConditionLocation.a && this.d == CriterionConditionMobEffect.a && this.e == CriterionConditionNBT.a ? CriterionConditionEntity.a : new CriterionConditionEntity(this.a, this.b, this.c, this.d, this.e);
        }
    }
}
