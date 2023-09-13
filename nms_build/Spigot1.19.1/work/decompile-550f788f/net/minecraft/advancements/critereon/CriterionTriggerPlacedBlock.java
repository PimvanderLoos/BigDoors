package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class CriterionTriggerPlacedBlock extends CriterionTriggerAbstract<CriterionTriggerPlacedBlock.a> {

    static final MinecraftKey ID = new MinecraftKey("placed_block");

    public CriterionTriggerPlacedBlock() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerPlacedBlock.ID;
    }

    @Override
    public CriterionTriggerPlacedBlock.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        Block block = deserializeBlock(jsonobject);
        CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.fromJson(jsonobject.get("state"));

        if (block != null) {
            criteriontriggerproperties.checkState(block.getStateDefinition(), (s) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + s + ":");
            });
        }

        CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.fromJson(jsonobject.get("location"));
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new CriterionTriggerPlacedBlock.a(criterionconditionentity_b, block, criteriontriggerproperties, criterionconditionlocation, criterionconditionitem);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject jsonobject) {
        if (jsonobject.has("block")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "block"));

            return (Block) IRegistry.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block type '" + minecraftkey + "'");
            });
        } else {
            return null;
        }
    }

    public void trigger(EntityPlayer entityplayer, BlockPosition blockposition, ItemStack itemstack) {
        IBlockData iblockdata = entityplayer.getLevel().getBlockState(blockposition);

        this.trigger(entityplayer, (criteriontriggerplacedblock_a) -> {
            return criteriontriggerplacedblock_a.matches(iblockdata, blockposition, entityplayer.getLevel(), itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final Block block;
        private final CriterionTriggerProperties state;
        private final CriterionConditionLocation location;
        private final CriterionConditionItem item;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable Block block, CriterionTriggerProperties criteriontriggerproperties, CriterionConditionLocation criterionconditionlocation, CriterionConditionItem criterionconditionitem) {
            super(CriterionTriggerPlacedBlock.ID, criterionconditionentity_b);
            this.block = block;
            this.state = criteriontriggerproperties;
            this.location = criterionconditionlocation;
            this.item = criterionconditionitem;
        }

        public static CriterionTriggerPlacedBlock.a placedBlock(Block block) {
            return new CriterionTriggerPlacedBlock.a(CriterionConditionEntity.b.ANY, block, CriterionTriggerProperties.ANY, CriterionConditionLocation.ANY, CriterionConditionItem.ANY);
        }

        public boolean matches(IBlockData iblockdata, BlockPosition blockposition, WorldServer worldserver, ItemStack itemstack) {
            return this.block != null && !iblockdata.is(this.block) ? false : (!this.state.matches(iblockdata) ? false : (!this.location.matches(worldserver, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()) ? false : this.item.matches(itemstack)));
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (this.block != null) {
                jsonobject.addProperty("block", IRegistry.BLOCK.getKey(this.block).toString());
            }

            jsonobject.add("state", this.state.serializeToJson());
            jsonobject.add("location", this.location.serializeToJson());
            jsonobject.add("item", this.item.serializeToJson());
            return jsonobject;
        }
    }
}
