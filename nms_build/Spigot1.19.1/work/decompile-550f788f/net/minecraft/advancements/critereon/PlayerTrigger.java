package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerTrigger extends CriterionTriggerAbstract<PlayerTrigger.a> {

    final MinecraftKey id;

    public PlayerTrigger(MinecraftKey minecraftkey) {
        this.id = minecraftkey;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public PlayerTrigger.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        return new PlayerTrigger.a(this.id, criterionconditionentity_b);
    }

    public void trigger(EntityPlayer entityplayer) {
        this.trigger(entityplayer, (playertrigger_a) -> {
            return true;
        });
    }

    public static class a extends CriterionInstanceAbstract {

        public a(MinecraftKey minecraftkey, CriterionConditionEntity.b criterionconditionentity_b) {
            super(minecraftkey, criterionconditionentity_b);
        }

        public static PlayerTrigger.a located(CriterionConditionLocation criterionconditionlocation) {
            return new PlayerTrigger.a(CriterionTriggers.LOCATION.id, CriterionConditionEntity.b.wrap(CriterionConditionEntity.a.entity().located(criterionconditionlocation).build()));
        }

        public static PlayerTrigger.a located(CriterionConditionEntity criterionconditionentity) {
            return new PlayerTrigger.a(CriterionTriggers.LOCATION.id, CriterionConditionEntity.b.wrap(criterionconditionentity));
        }

        public static PlayerTrigger.a sleptInBed() {
            return new PlayerTrigger.a(CriterionTriggers.SLEPT_IN_BED.id, CriterionConditionEntity.b.ANY);
        }

        public static PlayerTrigger.a raidWon() {
            return new PlayerTrigger.a(CriterionTriggers.RAID_WIN.id, CriterionConditionEntity.b.ANY);
        }

        public static PlayerTrigger.a avoidVibration() {
            return new PlayerTrigger.a(CriterionTriggers.AVOID_VIBRATION.id, CriterionConditionEntity.b.ANY);
        }

        public static PlayerTrigger.a walkOnBlockWithEquipment(Block block, Item item) {
            return located(CriterionConditionEntity.a.entity().equipment(CriterionConditionEntityEquipment.a.equipment().feet(CriterionConditionItem.a.item().of(item).build()).build()).steppingOn(CriterionConditionLocation.a.location().setBlock(CriterionConditionBlock.a.block().of(block).build()).build()).build());
        }
    }
}
