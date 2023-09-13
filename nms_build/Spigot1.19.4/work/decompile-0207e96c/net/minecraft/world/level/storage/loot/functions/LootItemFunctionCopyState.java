package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionCopyState extends LootItemFunctionConditional {

    final Block block;
    final Set<IBlockState<?>> properties;

    LootItemFunctionCopyState(LootItemCondition[] alootitemcondition, Block block, Set<IBlockState<?>> set) {
        super(alootitemcondition);
        this.block = block;
        this.properties = set;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_STATE;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    @Override
    protected ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        IBlockData iblockdata = (IBlockData) loottableinfo.getParamOrNull(LootContextParameters.BLOCK_STATE);

        if (iblockdata != null) {
            NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();
            NBTTagCompound nbttagcompound1;

            if (nbttagcompound.contains("BlockStateTag", 10)) {
                nbttagcompound1 = nbttagcompound.getCompound("BlockStateTag");
            } else {
                nbttagcompound1 = new NBTTagCompound();
                nbttagcompound.put("BlockStateTag", nbttagcompound1);
            }

            Stream stream = this.properties.stream();

            Objects.requireNonNull(iblockdata);
            stream.filter(iblockdata::hasProperty).forEach((iblockstate) -> {
                nbttagcompound1.putString(iblockstate.getName(), serialize(iblockdata, iblockstate));
            });
        }

        return itemstack;
    }

    public static LootItemFunctionCopyState.a copyState(Block block) {
        return new LootItemFunctionCopyState.a(block);
    }

    private static <T extends Comparable<T>> String serialize(IBlockData iblockdata, IBlockState<T> iblockstate) {
        T t0 = iblockdata.getValue(iblockstate);

        return iblockstate.getName(t0);
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionCopyState.a> {

        private final Block block;
        private final Set<IBlockState<?>> properties = Sets.newHashSet();

        a(Block block) {
            this.block = block;
        }

        public LootItemFunctionCopyState.a copy(IBlockState<?> iblockstate) {
            if (!this.block.getStateDefinition().getProperties().contains(iblockstate)) {
                throw new IllegalStateException("Property " + iblockstate + " is not present on block " + this.block);
            } else {
                this.properties.add(iblockstate);
                return this;
            }
        }

        @Override
        protected LootItemFunctionCopyState.a getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootItemFunctionCopyState(this.getConditions(), this.block, this.properties);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionCopyState> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionCopyState lootitemfunctioncopystate, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctioncopystate, jsonserializationcontext);
            jsonobject.addProperty("block", BuiltInRegistries.BLOCK.getKey(lootitemfunctioncopystate.block).toString());
            JsonArray jsonarray = new JsonArray();

            lootitemfunctioncopystate.properties.forEach((iblockstate) -> {
                jsonarray.add(iblockstate.getName());
            });
            jsonobject.add("properties", jsonarray);
        }

        @Override
        public LootItemFunctionCopyState deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "block"));
            Block block = (Block) BuiltInRegistries.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                return new IllegalArgumentException("Can't find block " + minecraftkey);
            });
            BlockStateList<Block, IBlockData> blockstatelist = block.getStateDefinition();
            Set<IBlockState<?>> set = Sets.newHashSet();
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "properties", (JsonArray) null);

            if (jsonarray != null) {
                jsonarray.forEach((jsonelement) -> {
                    set.add(blockstatelist.getProperty(ChatDeserializer.convertToString(jsonelement, "property")));
                });
            }

            return new LootItemFunctionCopyState(alootitemcondition, block, set);
        }
    }
}
