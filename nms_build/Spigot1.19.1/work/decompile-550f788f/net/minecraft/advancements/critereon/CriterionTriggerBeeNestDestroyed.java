package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class CriterionTriggerBeeNestDestroyed extends CriterionTriggerAbstract<CriterionTriggerBeeNestDestroyed.a> {

    static final MinecraftKey ID = new MinecraftKey("bee_nest_destroyed");

    public CriterionTriggerBeeNestDestroyed() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerBeeNestDestroyed.ID;
    }

    @Override
    public CriterionTriggerBeeNestDestroyed.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        Block block = deserializeBlock(jsonobject);
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.fromJson(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject.get("num_bees_inside"));

        return new CriterionTriggerBeeNestDestroyed.a(criterionconditionentity_b, block, criterionconditionitem, criterionconditionvalue_integerrange);
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

    public void trigger(EntityPlayer entityplayer, IBlockData iblockdata, ItemStack itemstack, int i) {
        this.trigger(entityplayer, (criteriontriggerbeenestdestroyed_a) -> {
            return criteriontriggerbeenestdestroyed_a.matches(iblockdata, itemstack, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final Block block;
        private final CriterionConditionItem item;
        private final CriterionConditionValue.IntegerRange numBees;

        public a(CriterionConditionEntity.b criterionconditionentity_b, @Nullable Block block, CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerBeeNestDestroyed.ID, criterionconditionentity_b);
            this.block = block;
            this.item = criterionconditionitem;
            this.numBees = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerBeeNestDestroyed.a destroyedBeeNest(Block block, CriterionConditionItem.a criterionconditionitem_a, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerBeeNestDestroyed.a(CriterionConditionEntity.b.ANY, block, criterionconditionitem_a.build(), criterionconditionvalue_integerrange);
        }

        public boolean matches(IBlockData iblockdata, ItemStack itemstack, int i) {
            return this.block != null && !iblockdata.is(this.block) ? false : (!this.item.matches(itemstack) ? false : this.numBees.matches(i));
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (this.block != null) {
                jsonobject.addProperty("block", IRegistry.BLOCK.getKey(this.block).toString());
            }

            jsonobject.add("item", this.item.serializeToJson());
            jsonobject.add("num_bees_inside", this.numBees.serializeToJson());
            return jsonobject;
        }
    }
}
