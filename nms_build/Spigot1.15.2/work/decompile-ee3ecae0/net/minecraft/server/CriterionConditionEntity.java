package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class CriterionConditionEntity {

    public static final CriterionConditionEntity a = new CriterionConditionEntity(CriterionConditionEntityType.a, CriterionConditionDistance.a, CriterionConditionLocation.a, CriterionConditionMobEffect.a, CriterionConditionNBT.a, CriterionConditionEntityFlags.a, CriterionConditionEntityEquipment.a, CriterionConditionPlayer.a, (String) null, (MinecraftKey) null);
    public static final CriterionConditionEntity[] b = new CriterionConditionEntity[0];
    private final CriterionConditionEntityType c;
    private final CriterionConditionDistance d;
    private final CriterionConditionLocation e;
    private final CriterionConditionMobEffect f;
    private final CriterionConditionNBT g;
    private final CriterionConditionEntityFlags h;
    private final CriterionConditionEntityEquipment i;
    private final CriterionConditionPlayer j;
    @Nullable
    private final String k;
    @Nullable
    private final MinecraftKey l;

    private CriterionConditionEntity(CriterionConditionEntityType criterionconditionentitytype, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionNBT criterionconditionnbt, CriterionConditionEntityFlags criterionconditionentityflags, CriterionConditionEntityEquipment criterionconditionentityequipment, CriterionConditionPlayer criterionconditionplayer, @Nullable String s, @Nullable MinecraftKey minecraftkey) {
        this.c = criterionconditionentitytype;
        this.d = criterionconditiondistance;
        this.e = criterionconditionlocation;
        this.f = criterionconditionmobeffect;
        this.g = criterionconditionnbt;
        this.h = criterionconditionentityflags;
        this.i = criterionconditionentityequipment;
        this.j = criterionconditionplayer;
        this.k = s;
        this.l = minecraftkey;
    }

    public boolean a(EntityPlayer entityplayer, @Nullable Entity entity) {
        return this.a(entityplayer.getWorldServer(), entityplayer.getPositionVector(), entity);
    }

    public boolean a(WorldServer worldserver, @Nullable Vec3D vec3d, @Nullable Entity entity) {
        if (this == CriterionConditionEntity.a) {
            return true;
        } else if (entity == null) {
            return false;
        } else if (!this.c.a(entity.getEntityType())) {
            return false;
        } else {
            if (vec3d == null) {
                if (this.d != CriterionConditionDistance.a) {
                    return false;
                }
            } else if (!this.d.a(vec3d.x, vec3d.y, vec3d.z, entity.locX(), entity.locY(), entity.locZ())) {
                return false;
            }

            if (!this.e.a(worldserver, entity.locX(), entity.locY(), entity.locZ())) {
                return false;
            } else if (!this.f.a(entity)) {
                return false;
            } else if (!this.g.a(entity)) {
                return false;
            } else if (!this.h.a(entity)) {
                return false;
            } else if (!this.i.a(entity)) {
                return false;
            } else if (!this.j.a(entity)) {
                return false;
            } else {
                if (this.k != null) {
                    ScoreboardTeamBase scoreboardteambase = entity.getScoreboardTeam();

                    if (scoreboardteambase == null || !this.k.equals(scoreboardteambase.getName())) {
                        return false;
                    }
                }

                return this.l == null || entity instanceof EntityCat && ((EntityCat) entity).ez().equals(this.l);
            }
        }
    }

    public static CriterionConditionEntity a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "entity");
            CriterionConditionEntityType criterionconditionentitytype = CriterionConditionEntityType.a(jsonobject.get("type"));
            CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));
            CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("location"));
            CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.a(jsonobject.get("effects"));
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));
            CriterionConditionEntityFlags criterionconditionentityflags = CriterionConditionEntityFlags.a(jsonobject.get("flags"));
            CriterionConditionEntityEquipment criterionconditionentityequipment = CriterionConditionEntityEquipment.a(jsonobject.get("equipment"));
            CriterionConditionPlayer criterionconditionplayer = CriterionConditionPlayer.a(jsonobject.get("player"));
            String s = ChatDeserializer.a(jsonobject, "team", (String) null);
            MinecraftKey minecraftkey = jsonobject.has("catType") ? new MinecraftKey(ChatDeserializer.h(jsonobject, "catType")) : null;

            return (new CriterionConditionEntity.a()).a(criterionconditionentitytype).a(criterionconditiondistance).a(criterionconditionlocation).a(criterionconditionmobeffect).a(criterionconditionnbt).a(criterionconditionentityflags).a(criterionconditionentityequipment).a(criterionconditionplayer).a(s).b(minecraftkey).b();
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
            jsonobject.add("flags", this.h.a());
            jsonobject.add("equipment", this.i.a());
            jsonobject.add("player", this.j.a());
            jsonobject.addProperty("team", this.k);
            if (this.l != null) {
                jsonobject.addProperty("catType", this.l.toString());
            }

            return jsonobject;
        }
    }

    public static JsonElement a(CriterionConditionEntity[] acriterionconditionentity) {
        if (acriterionconditionentity == CriterionConditionEntity.b) {
            return JsonNull.INSTANCE;
        } else {
            JsonArray jsonarray = new JsonArray();
            CriterionConditionEntity[] acriterionconditionentity1 = acriterionconditionentity;
            int i = acriterionconditionentity.length;

            for (int j = 0; j < i; ++j) {
                CriterionConditionEntity criterionconditionentity = acriterionconditionentity1[j];
                JsonElement jsonelement = criterionconditionentity.a();

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
        private CriterionConditionEntityFlags f;
        private CriterionConditionEntityEquipment g;
        private CriterionConditionPlayer h;
        private String i;
        private MinecraftKey j;

        public a() {
            this.a = CriterionConditionEntityType.a;
            this.b = CriterionConditionDistance.a;
            this.c = CriterionConditionLocation.a;
            this.d = CriterionConditionMobEffect.a;
            this.e = CriterionConditionNBT.a;
            this.f = CriterionConditionEntityFlags.a;
            this.g = CriterionConditionEntityEquipment.a;
            this.h = CriterionConditionPlayer.a;
        }

        public static CriterionConditionEntity.a a() {
            return new CriterionConditionEntity.a();
        }

        public CriterionConditionEntity.a a(EntityTypes<?> entitytypes) {
            this.a = CriterionConditionEntityType.b(entitytypes);
            return this;
        }

        public CriterionConditionEntity.a a(Tag<EntityTypes<?>> tag) {
            this.a = CriterionConditionEntityType.a(tag);
            return this;
        }

        public CriterionConditionEntity.a a(MinecraftKey minecraftkey) {
            this.j = minecraftkey;
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

        public CriterionConditionEntity.a a(CriterionConditionEntityFlags criterionconditionentityflags) {
            this.f = criterionconditionentityflags;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionEntityEquipment criterionconditionentityequipment) {
            this.g = criterionconditionentityequipment;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionPlayer criterionconditionplayer) {
            this.h = criterionconditionplayer;
            return this;
        }

        public CriterionConditionEntity.a a(@Nullable String s) {
            this.i = s;
            return this;
        }

        public CriterionConditionEntity.a b(@Nullable MinecraftKey minecraftkey) {
            this.j = minecraftkey;
            return this;
        }

        public CriterionConditionEntity b() {
            return new CriterionConditionEntity(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j);
        }
    }
}
