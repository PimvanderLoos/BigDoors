package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerCuredZombieVillager extends CriterionTriggerAbstract<CriterionTriggerCuredZombieVillager.a> {

    static final MinecraftKey ID = new MinecraftKey("cured_zombie_villager");

    public CriterionTriggerCuredZombieVillager() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerCuredZombieVillager.ID;
    }

    @Override
    public CriterionTriggerCuredZombieVillager.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionEntity.b criterionconditionentity_b1 = CriterionConditionEntity.b.fromJson(jsonobject, "zombie", lootdeserializationcontext);
        CriterionConditionEntity.b criterionconditionentity_b2 = CriterionConditionEntity.b.fromJson(jsonobject, "villager", lootdeserializationcontext);

        return new CriterionTriggerCuredZombieVillager.a(criterionconditionentity_b, criterionconditionentity_b1, criterionconditionentity_b2);
    }

    public void trigger(EntityPlayer entityplayer, EntityZombie entityzombie, EntityVillager entityvillager) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entityzombie);
        LootTableInfo loottableinfo1 = CriterionConditionEntity.createContext(entityplayer, entityvillager);

        this.trigger(entityplayer, (criteriontriggercuredzombievillager_a) -> {
            return criteriontriggercuredzombievillager_a.matches(loottableinfo, loottableinfo1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionEntity.b zombie;
        private final CriterionConditionEntity.b villager;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionEntity.b criterionconditionentity_b1, CriterionConditionEntity.b criterionconditionentity_b2) {
            super(CriterionTriggerCuredZombieVillager.ID, criterionconditionentity_b);
            this.zombie = criterionconditionentity_b1;
            this.villager = criterionconditionentity_b2;
        }

        public static CriterionTriggerCuredZombieVillager.a curedZombieVillager() {
            return new CriterionTriggerCuredZombieVillager.a(CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY, CriterionConditionEntity.b.ANY);
        }

        public boolean matches(LootTableInfo loottableinfo, LootTableInfo loottableinfo1) {
            return !this.zombie.matches(loottableinfo) ? false : this.villager.matches(loottableinfo1);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("zombie", this.zombie.toJson(lootserializationcontext));
            jsonobject.add("villager", this.villager.toJson(lootserializationcontext));
            return jsonobject;
        }
    }
}
