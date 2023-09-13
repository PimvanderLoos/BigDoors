package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class CriterionSlideDownBlock extends CriterionTriggerAbstract<CriterionSlideDownBlock.a> {

    static final MinecraftKey ID = new MinecraftKey("slide_down_block");

    public CriterionSlideDownBlock() {}

    @Override
    public MinecraftKey getId() {
        return CriterionSlideDownBlock.ID;
    }

    @Override
    public CriterionSlideDownBlock.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        Block block = deserializeBlock(jsonobject);
        CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.fromJson(jsonobject.get("state"));

        if (block != null) {
            criteriontriggerproperties.checkState(block.getStateDefinition(), (s) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + s);
            });
        }

        return new CriterionSlideDownBlock.a(criterionconditionentity_b, block, criteriontriggerproperties);
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

    public void trigger(EntityPlayer entityplayer, IBlockData iblockdata) {
        this.trigger(entityplayer, (criterionslidedownblock_a) -> {
            return criterionslidedownblock_a.matches(iblockdata);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final Block block;
        private final CriterionTriggerProperties state;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable Block block, CriterionTriggerProperties criteriontriggerproperties) {
            super(CriterionSlideDownBlock.ID, criterionconditionentity_b);
            this.block = block;
            this.state = criteriontriggerproperties;
        }

        public static CriterionSlideDownBlock.a slidesDownBlock(Block block) {
            return new CriterionSlideDownBlock.a(CriterionConditionEntity.b.ANY, block, CriterionTriggerProperties.ANY);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (this.block != null) {
                jsonobject.addProperty("block", IRegistry.BLOCK.getKey(this.block).toString());
            }

            jsonobject.add("state", this.state.serializeToJson());
            return jsonobject;
        }

        public boolean matches(IBlockData iblockdata) {
            return this.block != null && !iblockdata.is(this.block) ? false : this.state.matches(iblockdata);
        }
    }
}
