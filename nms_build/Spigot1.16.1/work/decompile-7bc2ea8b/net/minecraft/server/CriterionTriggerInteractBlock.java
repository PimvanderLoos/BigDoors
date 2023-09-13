package net.minecraft.server;

import com.google.gson.JsonObject;

public class CriterionTriggerInteractBlock extends CriterionTriggerAbstract<CriterionTriggerInteractBlock.a> {

    private static final MinecraftKey a = new MinecraftKey("item_used_on_block");

    public CriterionTriggerInteractBlock() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerInteractBlock.a;
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

        private final CriterionConditionLocation a;
        private final CriterionConditionItem b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionLocation criterionconditionlocation, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerInteractBlock.a, criterionconditionentity_b);
            this.a = criterionconditionlocation;
            this.b = criterionconditionitem;
        }

        public static CriterionTriggerInteractBlock.a a(CriterionConditionLocation.a criterionconditionlocation_a, CriterionConditionItem.a criterionconditionitem_a) {
            return new CriterionTriggerInteractBlock.a(CriterionConditionEntity.b.a, criterionconditionlocation_a.b(), criterionconditionitem_a.b());
        }

        public boolean a(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
            return !this.a.a(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) ? false : this.b.a(itemstack);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            jsonobject.add("location", this.a.a());
            jsonobject.add("item", this.b.a());
            return jsonobject;
        }
    }
}
