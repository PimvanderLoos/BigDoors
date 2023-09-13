package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;
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

    private final Block a;
    private final Set<IBlockState<?>> b;

    private LootItemFunctionCopyState(LootItemCondition[] alootitemcondition, Block block, Set<IBlockState<?>> set) {
        super(alootitemcondition);
        this.a = block;
        this.b = set;
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.v;
    }

    @Override
    public Set<LootContextParameter<?>> a() {
        return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
    }

    @Override
    protected ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        IBlockData iblockdata = (IBlockData) loottableinfo.getContextParameter(LootContextParameters.BLOCK_STATE);

        if (iblockdata != null) {
            NBTTagCompound nbttagcompound = itemstack.getOrCreateTag();
            NBTTagCompound nbttagcompound1;

            if (nbttagcompound.hasKeyOfType("BlockStateTag", 10)) {
                nbttagcompound1 = nbttagcompound.getCompound("BlockStateTag");
            } else {
                nbttagcompound1 = new NBTTagCompound();
                nbttagcompound.set("BlockStateTag", nbttagcompound1);
            }

            Stream stream = this.b.stream();

            iblockdata.getClass();
            stream.filter(iblockdata::b).forEach((iblockstate) -> {
                nbttagcompound1.setString(iblockstate.getName(), a(iblockdata, iblockstate));
            });
        }

        return itemstack;
    }

    public static LootItemFunctionCopyState.a a(Block block) {
        return new LootItemFunctionCopyState.a(block);
    }

    private static <T extends Comparable<T>> String a(IBlockData iblockdata, IBlockState<T> iblockstate) {
        T t0 = iblockdata.get(iblockstate);

        return iblockstate.a(t0);
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionCopyState> {

        public b() {}

        public void a(JsonObject jsonobject, LootItemFunctionCopyState lootitemfunctioncopystate, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctioncopystate, jsonserializationcontext);
            jsonobject.addProperty("block", IRegistry.BLOCK.getKey(lootitemfunctioncopystate.a).toString());
            JsonArray jsonarray = new JsonArray();

            lootitemfunctioncopystate.b.forEach((iblockstate) -> {
                jsonarray.add(iblockstate.getName());
            });
            jsonobject.add("properties", jsonarray);
        }

        @Override
        public LootItemFunctionCopyState b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "block"));
            Block block = (Block) IRegistry.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                return new IllegalArgumentException("Can't find block " + minecraftkey);
            });
            BlockStateList<Block, IBlockData> blockstatelist = block.getStates();
            Set<IBlockState<?>> set = Sets.newHashSet();
            JsonArray jsonarray = ChatDeserializer.a(jsonobject, "properties", (JsonArray) null);

            if (jsonarray != null) {
                jsonarray.forEach((jsonelement) -> {
                    set.add(blockstatelist.a(ChatDeserializer.a(jsonelement, "property")));
                });
            }

            return new LootItemFunctionCopyState(alootitemcondition, block, set);
        }
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionCopyState.a> {

        private final Block a;
        private final Set<IBlockState<?>> b;

        private a(Block block) {
            this.b = Sets.newHashSet();
            this.a = block;
        }

        public LootItemFunctionCopyState.a a(IBlockState<?> iblockstate) {
            if (!this.a.getStates().d().contains(iblockstate)) {
                throw new IllegalStateException("Property " + iblockstate + " is not present on block " + this.a);
            } else {
                this.b.add(iblockstate);
                return this;
            }
        }

        @Override
        protected LootItemFunctionCopyState.a d() {
            return this;
        }

        @Override
        public LootItemFunction b() {
            return new LootItemFunctionCopyState(this.g(), this.a, this.b);
        }
    }
}
