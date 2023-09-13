package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CriterionTriggerLocation extends CriterionTriggerAbstract<CriterionTriggerLocation.a> {

    final MinecraftKey id;

    public CriterionTriggerLocation(MinecraftKey minecraftkey) {
        this.id = minecraftkey;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public CriterionTriggerLocation.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "location", jsonobject);
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.fromJson(jsonobject1);

        return new CriterionTriggerLocation.a(this.id, criterionconditionentity_b, criterionconditionlocation);
    }

    public void trigger(EntityPlayer entityplayer) {
        this.trigger(entityplayer, (criteriontriggerlocation_a) -> {
            return criteriontriggerlocation_a.matches(entityplayer.getLevel(), entityplayer.getX(), entityplayer.getY(), entityplayer.getZ());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation location;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionLocation criterionconditionlocation) {
            super(minecraftkey, criterionconditionentity_b);
            this.location = criterionconditionlocation;
        }

        public static CriterionTriggerLocation.a located(CriterionConditionLocation criterionconditionlocation) {
            return new CriterionTriggerLocation.a(CriterionTriggers.LOCATION.id, CriterionConditionEntity.b.ANY, criterionconditionlocation);
        }

        public static CriterionTriggerLocation.a located(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerLocation.a(CriterionTriggers.LOCATION.id, CriterionConditionEntity.b.wrap(criterionconditionentity), CriterionConditionLocation.ANY);
        }

        public static CriterionTriggerLocation.a sleptInBed() {
            return new CriterionTriggerLocation.a(CriterionTriggers.SLEPT_IN_BED.id, CriterionConditionEntity.b.ANY, CriterionConditionLocation.ANY);
        }

        public static CriterionTriggerLocation.a raidWon() {
            return new CriterionTriggerLocation.a(CriterionTriggers.RAID_WIN.id, CriterionConditionEntity.b.ANY, CriterionConditionLocation.ANY);
        }

        public static CriterionTriggerLocation.a walkOnBlockWithEquipment(Block block, Item item) {
            return located(CriterionConditionEntity.a.entity().equipment(CriterionConditionEntityEquipment.a.equipment().feet(CriterionConditionItem.a.item().of(item).build()).build()).steppingOn(CriterionConditionLocation.a.location().setBlock(CriterionConditionBlock.a.block().of(block).build()).build()).build());
        }

        public boolean matches(WorldServer worldserver, double d0, double d1, double d2) {
            return this.location.matches(worldserver, d0, d1, d2);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("location", this.location.serializeToJson());
            return jsonobject;
        }
    }
}
