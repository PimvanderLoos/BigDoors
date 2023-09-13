package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.advancements.critereon.CriterionTriggerProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.storage.loot.LootSerializer;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class LootItemConditionBlockStateProperty implements LootItemCondition {

    final Block block;
    final CriterionTriggerProperties properties;

    LootItemConditionBlockStateProperty(Block block, CriterionTriggerProperties criteriontriggerproperties) {
        this.block = block;
        this.properties = criteriontriggerproperties;
    }

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.BLOCK_STATE_PROPERTY;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    public boolean test(LootTableInfo loottableinfo) {
        IBlockData iblockdata = (IBlockData) loottableinfo.getParamOrNull(LootContextParameters.BLOCK_STATE);

        return iblockdata != null && iblockdata.is(this.block) && this.properties.matches(iblockdata);
    }

    public static LootItemConditionBlockStateProperty.a hasBlockStateProperties(Block block) {
        return new LootItemConditionBlockStateProperty.a(block);
    }

    public static class a implements LootItemCondition.a {

        private final Block block;
        private CriterionTriggerProperties properties;

        public a(Block block) {
            this.properties = CriterionTriggerProperties.ANY;
            this.block = block;
        }

        public LootItemConditionBlockStateProperty.a setProperties(CriterionTriggerProperties.a criteriontriggerproperties_a) {
            this.properties = criteriontriggerproperties_a.build();
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new LootItemConditionBlockStateProperty(this.block, this.properties);
        }
    }

    public static class b implements LootSerializer<LootItemConditionBlockStateProperty> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemConditionBlockStateProperty lootitemconditionblockstateproperty, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("block", BuiltInRegistries.BLOCK.getKey(lootitemconditionblockstateproperty.block).toString());
            jsonobject.add("properties", lootitemconditionblockstateproperty.properties.serializeToJson());
        }

        @Override
        public LootItemConditionBlockStateProperty deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "block"));
            Block block = (Block) BuiltInRegistries.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                return new IllegalArgumentException("Can't find block " + minecraftkey);
            });
            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.fromJson(jsonobject.get("properties"));

            criteriontriggerproperties.checkState(block.getStateDefinition(), (s) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + s);
            });
            return new LootItemConditionBlockStateProperty(block, criteriontriggerproperties);
        }
    }
}
