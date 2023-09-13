package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class CriterionTriggerBeeNestDestroyed extends CriterionTriggerAbstract<CriterionTriggerBeeNestDestroyed.a> {

    private static final MinecraftKey a = new MinecraftKey("bee_nest_destroyed");

    public CriterionTriggerBeeNestDestroyed() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerBeeNestDestroyed.a;
    }

    @Override
    public CriterionTriggerBeeNestDestroyed.a a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
        Block block = a(jsonobject);
        CriterionConditionItem criterionconditionitem = CriterionConditionItem.a(jsonobject.get("item"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject.get("num_bees_inside"));

        return new CriterionTriggerBeeNestDestroyed.a(block, criterionconditionitem, criterionconditionvalue_integerrange);
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

    public void a(EntityPlayer entityplayer, Block block, ItemStack itemstack, int i) {
        this.a(entityplayer.getAdvancementData(), (criteriontriggerbeenestdestroyed_a) -> {
            return criteriontriggerbeenestdestroyed_a.a(block, itemstack, i);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final Block a;
        private final CriterionConditionItem b;
        private final CriterionConditionValue.IntegerRange c;

        public a(Block block, CriterionConditionItem criterionconditionitem, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            super(CriterionTriggerBeeNestDestroyed.a);
            this.a = block;
            this.b = criterionconditionitem;
            this.c = criterionconditionvalue_integerrange;
        }

        public static CriterionTriggerBeeNestDestroyed.a a(Block block, CriterionConditionItem.a criterionconditionitem_a, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange) {
            return new CriterionTriggerBeeNestDestroyed.a(block, criterionconditionitem_a.b(), criterionconditionvalue_integerrange);
        }

        public boolean a(Block block, ItemStack itemstack, int i) {
            return this.a != null && block != this.a ? false : (!this.b.a(itemstack) ? false : this.c.d(i));
        }

        @Override
        public JsonElement b() {
            JsonObject jsonobject = new JsonObject();

            if (this.a != null) {
                jsonobject.addProperty("block", IRegistry.BLOCK.getKey(this.a).toString());
            }

            jsonobject.add("item", this.b.a());
            jsonobject.add("num_bees_inside", this.c.d());
            return jsonobject;
        }
    }
}
