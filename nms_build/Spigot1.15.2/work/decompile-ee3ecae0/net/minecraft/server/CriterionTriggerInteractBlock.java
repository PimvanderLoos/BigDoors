package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CriterionTriggerInteractBlock extends CriterionTriggerAbstract<CriterionTriggerInteractBlock.a> {

    private final MinecraftKey a;

    public CriterionTriggerInteractBlock(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    @Override
    public MinecraftKey a() {
        return this.a;
    }

    @Override
    public CriterionTriggerInteractBlock.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        CriterionConditionBlock criterionconditionblock = CriterionConditionBlock.a(jsonobject.get("block"));
        CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("state"));
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));

        return new CriterionTriggerInteractBlock.a(this.a, criterionconditionblock, criteriontriggerproperties, criterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, BlockPosition blockposition, ItemStack itemstack) {
        IBlockData iblockdata = entityplayer.getWorldServer().getType(blockposition);

        this.a(entityplayer.getAdvancementData(), (criteriontriggerinteractblock_a) -> {
            return criteriontriggerinteractblock_a.a(iblockdata, entityplayer.getWorldServer(), blockposition, itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionBlock a;
        private final CriterionTriggerProperties b;
        private final CriterionConditionItem c;

        public a(MinecraftKey minecraftkey, CriterionConditionBlock criterionconditionblock, CriterionTriggerProperties criteriontriggerproperties, CriterionConditionItem criterionconditionitem) {
            super(minecraftkey);
            this.a = criterionconditionblock;
            this.b = criteriontriggerproperties;
            this.c = criterionconditionitem;
        }

        public static CriterionTriggerInteractBlock.a a(CriterionConditionBlock.a criterionconditionblock_a, CriterionConditionItem.a criterionconditionitem_a) {
            return new CriterionTriggerInteractBlock.a(CriterionTriggers.J.a, criterionconditionblock_a.b(), CriterionTriggerProperties.a, criterionconditionitem_a.b());
        }

        public boolean a(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, ItemStack itemstack) {
            return !this.a.a(worldserver, blockposition) ? false : (!this.b.a(iblockdata) ? false : this.c.a(itemstack));
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("block", this.a.a());
            jsonobject.add("state", this.b.a());
            jsonobject.add("item", this.c.a());
            return jsonobject;
        }
    }
}
