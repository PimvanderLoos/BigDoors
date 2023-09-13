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
    public MinecraftKey a() {
        return CriterionSlideDownBlock.ID;
    }

    @Override
    public CriterionSlideDownBlock.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        Block block = a(jsonobject);
        CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("state"));

        if (block != null) {
            criteriontriggerproperties.a(block.getStates(), (s) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + s);
            });
        }

        return new CriterionSlideDownBlock.a(criterionconditionentity_b, block, criteriontriggerproperties);
    }

    @Nullable
    private static Block a(JsonObject jsonobject) {
        if (jsonobject.has("block")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "block"));

            return (Block) IRegistry.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block type '" + minecraftkey + "'");
            });
        } else {
            return null;
        }
    }

    public void a(EntityPlayer entityplayer, IBlockData iblockdata) {
        this.a(entityplayer, (criterionslidedownblock_a) -> {
            return criterionslidedownblock_a.a(iblockdata);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final Block block;
        private final CriterionTriggerProperties state;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable Block block, CriterionTriggerProperties criteriontriggerproperties) {
            super(CriterionSlideDownBlock.ID, criterionconditionentity_b);
            this.block = block;
            this.state = criteriontriggerproperties;
        }

        public static CriterionSlideDownBlock.a a(Block block) {
            return new CriterionSlideDownBlock.a(CriterionConditionEntity.b.ANY, block, CriterionTriggerProperties.ANY);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            if (this.block != null) {
                jsonobject.addProperty("block", IRegistry.BLOCK.getKey(this.block).toString());
            }

            jsonobject.add("state", this.state.a());
            return jsonobject;
        }

        public boolean a(IBlockData iblockdata) {
            return this.block != null && !iblockdata.a(this.block) ? false : this.state.a(iblockdata);
        }
    }
}
