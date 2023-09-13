package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.advancements.critereon.CriterionTriggerProperties;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class LootItemConditionBlockStateProperty implements LootItemCondition {

    private final Block a;
    private final CriterionTriggerProperties b;

    private LootItemConditionBlockStateProperty(Block block, CriterionTriggerProperties criteriontriggerproperties) {
        this.a = block;
        this.b = criteriontriggerproperties;
    }

    @Override
    public LootItemConditionType b() {
        return LootItemConditions.h;
    }

    @Override
    public Set<LootContextParameter<?>> a() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    public boolean test(LootTableInfo loottableinfo) {
        IBlockData iblockdata = (IBlockData) loottableinfo.getContextParameter(LootContextParameters.BLOCK_STATE);

        return iblockdata != null && this.a == iblockdata.getBlock() && this.b.a(iblockdata);
    }

    public static LootItemConditionBlockStateProperty.a a(Block block) {
        return new LootItemConditionBlockStateProperty.a(block);
    }

    public static class b implements LootSerializer<LootItemConditionBlockStateProperty> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemConditionBlockStateProperty lootitemconditionblockstateproperty, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("block", IRegistry.BLOCK.getKey(lootitemconditionblockstateproperty.a).toString());
            jsonobject.add("properties", lootitemconditionblockstateproperty.b.a());
        }

        @Override
        public LootItemConditionBlockStateProperty a(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "block"));
            Block block = (Block) IRegistry.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                return new IllegalArgumentException("Can't find block " + minecraftkey);
            });
            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("properties"));

            criteriontriggerproperties.a(block.getStates(), (s) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + s);
            });
            return new LootItemConditionBlockStateProperty(block, criteriontriggerproperties);
        }
    }

    public static class a implements LootItemCondition.a {

        private final Block a;
        private CriterionTriggerProperties b;

        public a(Block block) {
            this.b = CriterionTriggerProperties.a;
            this.a = block;
        }

        public LootItemConditionBlockStateProperty.a a(CriterionTriggerProperties.a criteriontriggerproperties_a) {
            this.b = criteriontriggerproperties_a.b();
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new LootItemConditionBlockStateProperty(this.a, this.b);
        }
    }
}
