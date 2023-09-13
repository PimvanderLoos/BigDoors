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

    final Block block;
    final CriterionTriggerProperties properties;

    LootItemConditionBlockStateProperty(Block block, CriterionTriggerProperties criteriontriggerproperties) {
        this.block = block;
        this.properties = criteriontriggerproperties;
    }

    @Override
    public LootItemConditionType a() {
        return LootItemConditions.BLOCK_STATE_PROPERTY;
    }

    @Override
    public Set<LootContextParameter<?>> b() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    public boolean test(LootTableInfo loottableinfo) {
        IBlockData iblockdata = (IBlockData) loottableinfo.getContextParameter(LootContextParameters.BLOCK_STATE);

        return iblockdata != null && iblockdata.a(this.block) && this.properties.a(iblockdata);
    }

    public static LootItemConditionBlockStateProperty.a a(Block block) {
        return new LootItemConditionBlockStateProperty.a(block);
    }

    public static class a implements LootItemCondition.a {

        private final Block block;
        private CriterionTriggerProperties properties;

        public a(Block block) {
            this.properties = CriterionTriggerProperties.ANY;
            this.block = block;
        }

        public LootItemConditionBlockStateProperty.a a(CriterionTriggerProperties.a criteriontriggerproperties_a) {
            this.properties = criteriontriggerproperties_a.b();
            return this;
        }

        @Override
        public LootItemCondition build() {
            return new LootItemConditionBlockStateProperty(this.block, this.properties);
        }
    }

    public static class b implements LootSerializer<LootItemConditionBlockStateProperty> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemConditionBlockStateProperty lootitemconditionblockstateproperty, JsonSerializationContext jsonserializationcontext) {
            jsonobject.addProperty("block", IRegistry.BLOCK.getKey(lootitemconditionblockstateproperty.block).toString());
            jsonobject.add("properties", lootitemconditionblockstateproperty.properties.a());
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
}
