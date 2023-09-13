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
    public MinecraftKey a() {
        return CriterionTriggerInteractBlock.ID;
    }

    @Override
    public CriterionTriggerInteractBlock.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.a(jsonobject.get("location"));
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerInteractBlock.a(criterionconditionentity_b, criterionconditionlocation, criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, BlockPosition blockposition, ItemStack itemstack) {
        IBlockData iblockdata = entityplayer.getWorldServer().getType(blockposition);

        this.a(entityplayer, (criteriontriggerinteractblock_a) -> {
            return criteriontriggerinteractblock_a.a(iblockdata, entityplayer.getWorldServer(), blockposition, itemstack);
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

        public static CriterionTriggerInteractBlock.a a(CriterionConditionLocation.a criterionconditionlocation_a, CriterionConditionItem.a criterionconditionitem_a) {
            return new CriterionTriggerInteractBlock.a(CriterionConditionEntity.b.ANY, criterionconditionlocation_a.b(), criterionconditionitem_a.b());
        }

        public boolean a(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
            return !this.location.a(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) ? false : this.item.a(itemstack);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("location", this.location.a());
            jsonobject.add("item", this.item.a());
            return jsonobject;
        }
    }
}
