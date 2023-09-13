package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionEntityProperty;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.ScoreboardTeamBase;

public class CriterionConditionEntity {

    public static final CriterionConditionEntity ANY = new CriterionConditionEntity(CriterionConditionEntityType.ANY, CriterionConditionDistance.ANY, CriterionConditionLocation.ANY, CriterionConditionLocation.ANY, CriterionConditionMobEffect.ANY, CriterionConditionNBT.ANY, CriterionConditionEntityFlags.ANY, CriterionConditionEntityEquipment.ANY, CriterionConditionPlayer.ANY, CriterionConditionInOpenWater.ANY, LighthingBoltPredicate.ANY, (String) null, (MinecraftKey) null);
    private final CriterionConditionEntityType entityType;
    private final CriterionConditionDistance distanceToPlayer;
    private final CriterionConditionLocation location;
    private final CriterionConditionLocation steppingOnLocation;
    private final CriterionConditionMobEffect effects;
    private final CriterionConditionNBT nbt;
    private final CriterionConditionEntityFlags flags;
    private final CriterionConditionEntityEquipment equipment;
    private final CriterionConditionPlayer player;
    private final CriterionConditionInOpenWater fishingHook;
    private final LighthingBoltPredicate lighthingBolt;
    private final CriterionConditionEntity vehicle;
    private final CriterionConditionEntity passenger;
    private final CriterionConditionEntity targetedEntity;
    @Nullable
    private final String team;
    @Nullable
    private final MinecraftKey catType;

    private CriterionConditionEntity(CriterionConditionEntityType criterionconditionentitytype, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation, CriterionConditionLocation criterionconditionlocation1, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionNBT criterionconditionnbt, CriterionConditionEntityFlags criterionconditionentityflags, CriterionConditionEntityEquipment criterionconditionentityequipment, CriterionConditionPlayer criterionconditionplayer, CriterionConditionInOpenWater criterionconditioninopenwater, LighthingBoltPredicate lighthingboltpredicate, @Nullable String s, @Nullable MinecraftKey minecraftkey) {
        this.entityType = criterionconditionentitytype;
        this.distanceToPlayer = criterionconditiondistance;
        this.location = criterionconditionlocation;
        this.steppingOnLocation = criterionconditionlocation1;
        this.effects = criterionconditionmobeffect;
        this.nbt = criterionconditionnbt;
        this.flags = criterionconditionentityflags;
        this.equipment = criterionconditionentityequipment;
        this.player = criterionconditionplayer;
        this.fishingHook = criterionconditioninopenwater;
        this.lighthingBolt = lighthingboltpredicate;
        this.passenger = this;
        this.vehicle = this;
        this.targetedEntity = this;
        this.team = s;
        this.catType = minecraftkey;
    }

    CriterionConditionEntity(CriterionConditionEntityType criterionconditionentitytype, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation, CriterionConditionLocation criterionconditionlocation1, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionNBT criterionconditionnbt, CriterionConditionEntityFlags criterionconditionentityflags, CriterionConditionEntityEquipment criterionconditionentityequipment, CriterionConditionPlayer criterionconditionplayer, CriterionConditionInOpenWater criterionconditioninopenwater, LighthingBoltPredicate lighthingboltpredicate, CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1, CriterionConditionEntity criterionconditionentity2, @Nullable String s, @Nullable MinecraftKey minecraftkey) {
        this.entityType = criterionconditionentitytype;
        this.distanceToPlayer = criterionconditiondistance;
        this.location = criterionconditionlocation;
        this.steppingOnLocation = criterionconditionlocation1;
        this.effects = criterionconditionmobeffect;
        this.nbt = criterionconditionnbt;
        this.flags = criterionconditionentityflags;
        this.equipment = criterionconditionentityequipment;
        this.player = criterionconditionplayer;
        this.fishingHook = criterionconditioninopenwater;
        this.lighthingBolt = lighthingboltpredicate;
        this.vehicle = criterionconditionentity;
        this.passenger = criterionconditionentity1;
        this.targetedEntity = criterionconditionentity2;
        this.team = s;
        this.catType = minecraftkey;
    }

    public boolean a(EntityPlayer entityplayer, @Nullable Entity entity) {
        return this.a(entityplayer.getWorldServer(), entityplayer.getPositionVector(), entity);
    }

    public boolean a(WorldServer worldserver, @Nullable Vec3D vec3d, @Nullable Entity entity) {
        if (this == CriterionConditionEntity.ANY) {
            return true;
        } else if (entity == null) {
            return false;
        } else if (!this.entityType.a(entity.getEntityType())) {
            return false;
        } else {
            if (vec3d == null) {
                if (this.distanceToPlayer != CriterionConditionDistance.ANY) {
                    return false;
                }
            } else if (!this.distanceToPlayer.a(vec3d.x, vec3d.y, vec3d.z, entity.locX(), entity.locY(), entity.locZ())) {
                return false;
            }

            if (!this.location.a(worldserver, entity.locX(), entity.locY(), entity.locZ())) {
                return false;
            } else {
                if (this.steppingOnLocation != CriterionConditionLocation.ANY) {
                    Vec3D vec3d1 = Vec3D.a((BaseBlockPosition) entity.av());

                    if (!this.steppingOnLocation.a(worldserver, vec3d1.getX(), vec3d1.getY(), vec3d1.getZ())) {
                        return false;
                    }
                }

                if (!this.effects.a(entity)) {
                    return false;
                } else if (!this.nbt.a(entity)) {
                    return false;
                } else if (!this.flags.a(entity)) {
                    return false;
                } else if (!this.equipment.a(entity)) {
                    return false;
                } else if (!this.player.a(entity)) {
                    return false;
                } else if (!this.fishingHook.a(entity)) {
                    return false;
                } else if (!this.lighthingBolt.a(entity, worldserver, vec3d)) {
                    return false;
                } else if (!this.vehicle.a(worldserver, vec3d, entity.getVehicle())) {
                    return false;
                } else if (this.passenger != CriterionConditionEntity.ANY && entity.getPassengers().stream().noneMatch((entity1) -> {
                    return this.passenger.a(worldserver, vec3d, entity1);
                })) {
                    return false;
                } else if (!this.targetedEntity.a(worldserver, vec3d, entity instanceof EntityInsentient ? ((EntityInsentient) entity).getGoalTarget() : null)) {
                    return false;
                } else {
                    if (this.team != null) {
                        ScoreboardTeamBase scoreboardteambase = entity.getScoreboardTeam();

                        if (scoreboardteambase == null || !this.team.equals(scoreboardteambase.getName())) {
                            return false;
                        }
                    }

                    return this.catType == null || entity instanceof EntityCat && ((EntityCat) entity).fE().equals(this.catType);
                }
            }
        }
    }

    public static CriterionConditionEntity a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "entity");
            CriterionConditionEntityType criterionconditionentitytype = CriterionConditionEntityType.a(jsonobject.get("type"));
            CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.a(jsonobject.get("distance"));
            CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("location"));
            CriterionConditionLocation criterionconditionlocation1 = CriterionConditionLocation.a(jsonobject.get("stepping_on"));
            CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.a(jsonobject.get("effects"));
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));
            CriterionConditionEntityFlags criterionconditionentityflags = CriterionConditionEntityFlags.a(jsonobject.get("flags"));
            CriterionConditionEntityEquipment criterionconditionentityequipment = CriterionConditionEntityEquipment.a(jsonobject.get("equipment"));
            CriterionConditionPlayer criterionconditionplayer = CriterionConditionPlayer.a(jsonobject.get("player"));
            CriterionConditionInOpenWater criterionconditioninopenwater = CriterionConditionInOpenWater.a(jsonobject.get("fishing_hook"));
            CriterionConditionEntity criterionconditionentity = a(jsonobject.get("vehicle"));
            CriterionConditionEntity criterionconditionentity1 = a(jsonobject.get("passenger"));
            CriterionConditionEntity criterionconditionentity2 = a(jsonobject.get("targeted_entity"));
            LighthingBoltPredicate lighthingboltpredicate = LighthingBoltPredicate.a(jsonobject.get("lightning_bolt"));
            String s = ChatDeserializer.a(jsonobject, "team", (String) null);
            MinecraftKey minecraftkey = jsonobject.has("catType") ? new MinecraftKey(ChatDeserializer.h(jsonobject, "catType")) : null;

            return (new CriterionConditionEntity.a()).a(criterionconditionentitytype).a(criterionconditiondistance).a(criterionconditionlocation).b(criterionconditionlocation1).a(criterionconditionmobeffect).a(criterionconditionnbt).a(criterionconditionentityflags).a(criterionconditionentityequipment).a(criterionconditionplayer).a(criterionconditioninopenwater).a(lighthingboltpredicate).a(s).a(criterionconditionentity).b(criterionconditionentity1).c(criterionconditionentity2).b(minecraftkey).b();
        } else {
            return CriterionConditionEntity.ANY;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionEntity.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("type", this.entityType.a());
            jsonobject.add("distance", this.distanceToPlayer.a());
            jsonobject.add("location", this.location.a());
            jsonobject.add("stepping_on", this.steppingOnLocation.a());
            jsonobject.add("effects", this.effects.b());
            jsonobject.add("nbt", this.nbt.a());
            jsonobject.add("flags", this.flags.a());
            jsonobject.add("equipment", this.equipment.a());
            jsonobject.add("player", this.player.a());
            jsonobject.add("fishing_hook", this.fishingHook.a());
            jsonobject.add("lightning_bolt", this.lighthingBolt.a());
            jsonobject.add("vehicle", this.vehicle.a());
            jsonobject.add("passenger", this.passenger.a());
            jsonobject.add("targeted_entity", this.targetedEntity.a());
            jsonobject.addProperty("team", this.team);
            if (this.catType != null) {
                jsonobject.addProperty("catType", this.catType.toString());
            }

            return jsonobject;
        }
    }

    public static LootTableInfo b(EntityPlayer entityplayer, Entity entity) {
        return (new LootTableInfo.Builder(entityplayer.getWorldServer())).set(LootContextParameters.THIS_ENTITY, entity).set(LootContextParameters.ORIGIN, entityplayer.getPositionVector()).a(entityplayer.getRandom()).build(LootContextParameterSets.ADVANCEMENT_ENTITY);
    }

    public static class a {

        private CriterionConditionEntityType entityType;
        private CriterionConditionDistance distanceToPlayer;
        private CriterionConditionLocation location;
        private CriterionConditionLocation steppingOnLocation;
        private CriterionConditionMobEffect effects;
        private CriterionConditionNBT nbt;
        private CriterionConditionEntityFlags flags;
        private CriterionConditionEntityEquipment equipment;
        private CriterionConditionPlayer player;
        private CriterionConditionInOpenWater fishingHook;
        private LighthingBoltPredicate lighthingBolt;
        private CriterionConditionEntity vehicle;
        private CriterionConditionEntity passenger;
        private CriterionConditionEntity targetedEntity;
        private String team;
        private MinecraftKey catType;

        public a() {
            this.entityType = CriterionConditionEntityType.ANY;
            this.distanceToPlayer = CriterionConditionDistance.ANY;
            this.location = CriterionConditionLocation.ANY;
            this.steppingOnLocation = CriterionConditionLocation.ANY;
            this.effects = CriterionConditionMobEffect.ANY;
            this.nbt = CriterionConditionNBT.ANY;
            this.flags = CriterionConditionEntityFlags.ANY;
            this.equipment = CriterionConditionEntityEquipment.ANY;
            this.player = CriterionConditionPlayer.ANY;
            this.fishingHook = CriterionConditionInOpenWater.ANY;
            this.lighthingBolt = LighthingBoltPredicate.ANY;
            this.vehicle = CriterionConditionEntity.ANY;
            this.passenger = CriterionConditionEntity.ANY;
            this.targetedEntity = CriterionConditionEntity.ANY;
        }

        public static CriterionConditionEntity.a a() {
            return new CriterionConditionEntity.a();
        }

        public CriterionConditionEntity.a a(EntityTypes<?> entitytypes) {
            this.entityType = CriterionConditionEntityType.b(entitytypes);
            return this;
        }

        public CriterionConditionEntity.a a(Tag<EntityTypes<?>> tag) {
            this.entityType = CriterionConditionEntityType.a(tag);
            return this;
        }

        public CriterionConditionEntity.a a(MinecraftKey minecraftkey) {
            this.catType = minecraftkey;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionEntityType criterionconditionentitytype) {
            this.entityType = criterionconditionentitytype;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionDistance criterionconditiondistance) {
            this.distanceToPlayer = criterionconditiondistance;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionLocation criterionconditionlocation) {
            this.location = criterionconditionlocation;
            return this;
        }

        public CriterionConditionEntity.a b(CriterionConditionLocation criterionconditionlocation) {
            this.steppingOnLocation = criterionconditionlocation;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionMobEffect criterionconditionmobeffect) {
            this.effects = criterionconditionmobeffect;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionNBT criterionconditionnbt) {
            this.nbt = criterionconditionnbt;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionEntityFlags criterionconditionentityflags) {
            this.flags = criterionconditionentityflags;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionEntityEquipment criterionconditionentityequipment) {
            this.equipment = criterionconditionentityequipment;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionPlayer criterionconditionplayer) {
            this.player = criterionconditionplayer;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionInOpenWater criterionconditioninopenwater) {
            this.fishingHook = criterionconditioninopenwater;
            return this;
        }

        public CriterionConditionEntity.a a(LighthingBoltPredicate lighthingboltpredicate) {
            this.lighthingBolt = lighthingboltpredicate;
            return this;
        }

        public CriterionConditionEntity.a a(CriterionConditionEntity criterionconditionentity) {
            this.vehicle = criterionconditionentity;
            return this;
        }

        public CriterionConditionEntity.a b(CriterionConditionEntity criterionconditionentity) {
            this.passenger = criterionconditionentity;
            return this;
        }

        public CriterionConditionEntity.a c(CriterionConditionEntity criterionconditionentity) {
            this.targetedEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionEntity.a a(@Nullable String s) {
            this.team = s;
            return this;
        }

        public CriterionConditionEntity.a b(@Nullable MinecraftKey minecraftkey) {
            this.catType = minecraftkey;
            return this;
        }

        public CriterionConditionEntity b() {
            return new CriterionConditionEntity(this.entityType, this.distanceToPlayer, this.location, this.steppingOnLocation, this.effects, this.nbt, this.flags, this.equipment, this.player, this.fishingHook, this.lighthingBolt, this.vehicle, this.passenger, this.targetedEntity, this.team, this.catType);
        }
    }

    public static class b {

        public static final CriterionConditionEntity.b ANY = new CriterionConditionEntity.b(new LootItemCondition[0]);
        private final LootItemCondition[] conditions;
        private final Predicate<LootTableInfo> compositePredicates;

        private b(LootItemCondition[] alootitemcondition) {
            this.conditions = alootitemcondition;
            this.compositePredicates = LootItemConditions.a((Predicate[]) alootitemcondition);
        }

        public static CriterionConditionEntity.b a(LootItemCondition... alootitemcondition) {
            return new CriterionConditionEntity.b(alootitemcondition);
        }

        public static CriterionConditionEntity.b a(JsonObject jsonobject, String s, LootDeserializationContext lootdeserializationcontext) {
            JsonElement jsonelement = jsonobject.get(s);

            return a(s, lootdeserializationcontext, jsonelement);
        }

        public static CriterionConditionEntity.b[] b(JsonObject jsonobject, String s, LootDeserializationContext lootdeserializationcontext) {
            JsonElement jsonelement = jsonobject.get(s);

            if (jsonelement != null && !jsonelement.isJsonNull()) {
                JsonArray jsonarray = ChatDeserializer.n(jsonelement, s);
                CriterionConditionEntity.b[] acriterionconditionentity_b = new CriterionConditionEntity.b[jsonarray.size()];

                for (int i = 0; i < jsonarray.size(); ++i) {
                    acriterionconditionentity_b[i] = a(s + "[" + i + "]", lootdeserializationcontext, jsonarray.get(i));
                }

                return acriterionconditionentity_b;
            } else {
                return new CriterionConditionEntity.b[0];
            }
        }

        private static CriterionConditionEntity.b a(String s, LootDeserializationContext lootdeserializationcontext, @Nullable JsonElement jsonelement) {
            if (jsonelement != null && jsonelement.isJsonArray()) {
                LootItemCondition[] alootitemcondition = lootdeserializationcontext.a(jsonelement.getAsJsonArray(), lootdeserializationcontext.a() + "/" + s, LootContextParameterSets.ADVANCEMENT_ENTITY);

                return new CriterionConditionEntity.b(alootitemcondition);
            } else {
                CriterionConditionEntity criterionconditionentity = CriterionConditionEntity.a(jsonelement);

                return a(criterionconditionentity);
            }
        }

        public static CriterionConditionEntity.b a(CriterionConditionEntity criterionconditionentity) {
            if (criterionconditionentity == CriterionConditionEntity.ANY) {
                return CriterionConditionEntity.b.ANY;
            } else {
                LootItemCondition lootitemcondition = LootItemConditionEntityProperty.a(LootTableInfo.EntityTarget.THIS, criterionconditionentity).build();

                return new CriterionConditionEntity.b(new LootItemCondition[]{lootitemcondition});
            }
        }

        public boolean a(LootTableInfo loottableinfo) {
            return this.compositePredicates.test(loottableinfo);
        }

        public JsonElement a(LootSerializationContext lootserializationcontext) {
            return (JsonElement) (this.conditions.length == 0 ? JsonNull.INSTANCE : lootserializationcontext.a(this.conditions));
        }

        public static JsonElement a(CriterionConditionEntity.b[] acriterionconditionentity_b, LootSerializationContext lootserializationcontext) {
            if (acriterionconditionentity_b.length == 0) {
                return JsonNull.INSTANCE;
            } else {
                JsonArray jsonarray = new JsonArray();
                CriterionConditionEntity.b[] acriterionconditionentity_b1 = acriterionconditionentity_b;
                int i = acriterionconditionentity_b.length;

                for (int j = 0; j < i; ++j) {
                    CriterionConditionEntity.b criterionconditionentity_b = acriterionconditionentity_b1[j];

                    jsonarray.add(criterionconditionentity_b.a(lootserializationcontext));
                }

                return jsonarray;
            }
        }
    }
}
