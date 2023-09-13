package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
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
    public MinecraftKey a() {
        return this.id;
    }

    @Override
    public CriterionTriggerLocation.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "location", jsonobject);
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a((JsonElement) jsonobject1);

        return new CriterionTriggerLocation.a(this.id, criterionconditionentity_b, criterionconditionlocation);
    }

    public void a(EntityPlayer entityplayer) {
        this.a(entityplayer, (criteriontriggerlocation_a) -> {
            return criteriontriggerlocation_a.a(entityplayer.getWorldServer(), entityplayer.locX(), entityplayer.locY(), entityplayer.locZ());
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation location;

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionLocation criterionconditionlocation) {
            super(minecraftkey, criterionconditionentity_b);
            this.location = criterionconditionlocation;
        }

        public static CriterionTriggerLocation.a a(CriterionConditionLocation criterionconditionlocation) {
            return new CriterionTriggerLocation.a(CriterionTriggers.LOCATION.id, CriterionConditionEntity.b.ANY, criterionconditionlocation);
        }

        public static CriterionTriggerLocation.a a(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerLocation.a(CriterionTriggers.LOCATION.id, CriterionConditionEntity.b.a(criterionconditionentity), CriterionConditionLocation.ANY);
        }

        public static CriterionTriggerLocation.a c() {
            return new CriterionTriggerLocation.a(CriterionTriggers.SLEPT_IN_BED.id, CriterionConditionEntity.b.ANY, CriterionConditionLocation.ANY);
        }

        public static CriterionTriggerLocation.a d() {
            return new CriterionTriggerLocation.a(CriterionTriggers.RAID_WIN.id, CriterionConditionEntity.b.ANY, CriterionConditionLocation.ANY);
        }

        public static CriterionTriggerLocation.a a(Block block, Item item) {
            return a(CriterionConditionEntity.a.a().a(CriterionConditionEntityEquipment.a.a().d(CriterionConditionItem.a.a().a(item).b()).b()).b(CriterionConditionLocation.a.a().a(CriterionConditionBlock.a.a().a(block).b()).b()).b());
        }

        public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
            return this.location.a(worldserver, d0, d1, d2);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("location", this.location.a());
            return jsonobject;
        }
    }
}
