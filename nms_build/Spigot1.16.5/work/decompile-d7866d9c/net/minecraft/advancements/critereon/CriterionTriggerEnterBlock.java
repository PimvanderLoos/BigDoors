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

public class CriterionTriggerEnterBlock extends CriterionTriggerAbstract<CriterionTriggerEnterBlock.a> {

    private static final MinecraftKey a = new MinecraftKey("enter_block");

    public CriterionTriggerEnterBlock() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerEnterBlock.a;
    }

    @Override
    public CriterionTriggerEnterBlock.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        Block block = a(jsonobject);
        CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("state"));

        if (block != null) {
            criteriontriggerproperties.a(block.getStates(), (s) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + s);
            });
        }

        return new CriterionTriggerEnterBlock.a(criterionconditionentity_b, block, criteriontriggerproperties);
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
        this.a(entityplayer, (criteriontriggerenterblock_a) -> {
            return criteriontriggerenterblock_a.a(iblockdata);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final Block a;
        private final CriterionTriggerProperties b;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable Block block, CriterionTriggerProperties criteriontriggerproperties) {
            super(CriterionTriggerEnterBlock.a, criterionconditionentity_b);
            this.a = block;
            this.b = criteriontriggerproperties;
        }

        public static CriterionTriggerEnterBlock.a a(Block block) {
            return new CriterionTriggerEnterBlock.a(CriterionConditionEntity.b.a, block, CriterionTriggerProperties.a);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            if (this.a != null) {
                jsonobject.addProperty("block", IRegistry.BLOCK.getKey(this.a).toString());
            }

            jsonobject.add("state", this.b.a());
            return jsonobject;
        }

        public boolean a(IBlockData iblockdata) {
            return this.a != null && !iblockdata.a(this.a) ? false : this.b.a(iblockdata);
        }
    }
}
