package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.IBlockData;

public class CriterionTriggerInteractBlock extends CriterionTriggerAbstract<CriterionTriggerInteractBlock.a> {

    static final MinecraftKey ID = new MinecraftKey("item_used_on_block");

    public CriterionTriggerInteractBlock() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerInteractBlock.ID;
    }

    @Override
    public CriterionTriggerInteractBlock.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.fromJson(jsonobject.get("location"));
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new CriterionTriggerInteractBlock.a(criterionconditionentity_b, criterionconditionlocation, criterionconditionitem);
    }

    public void trigger(EntityPlayer entityplayer, BlockPosition blockposition, ItemStack itemstack) {
        IBlockData iblockdata = entityplayer.getLevel().getBlockState(blockposition);

        this.trigger(entityplayer, (criteriontriggerinteractblock_a) -> {
            return criteriontriggerinteractblock_a.matches(iblockdata, entityplayer.getLevel(), blockposition, itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionLocation location;
        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionLocation criterionconditionlocation, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerInteractBlock.ID, criterionconditionentity_b);
            this.location = criterionconditionlocation;
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerInteractBlock.a itemUsedOnBlock(CriterionConditionLocation.a criterionconditionlocation_a, CriterionConditionItem.a criterionconditionitem_a) {
            return new CriterionTriggerInteractBlock.a(CriterionConditionEntity.b.ANY, criterionconditionlocation_a.build(), criterionconditionitem_a.build());
        }

        public boolean matches(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
            return !this.location.matches(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) ? false : this.item.matches(itemstack);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("location", this.location.serializeToJson());
            jsonobject.add("item", this.item.serializeToJson());
            return jsonobject;
        }
    }
}
