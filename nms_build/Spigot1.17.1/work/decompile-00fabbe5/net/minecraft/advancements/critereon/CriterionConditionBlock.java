package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class CriterionConditionBlock {

    public static final CriterionConditionBlock ANY = new CriterionConditionBlock((Tag) null, (Set) null, CriterionTriggerProperties.ANY, CriterionConditionNBT.ANY);
    @Nullable
    private final Tag<Block> tag;
    @Nullable
    private final Set<Block> blocks;
    private final CriterionTriggerProperties properties;
    private final CriterionConditionNBT nbt;

    public CriterionConditionBlock(@Nullable Tag<Block> tag, @Nullable Set<Block> set, CriterionTriggerProperties criteriontriggerproperties, CriterionConditionNBT criterionconditionnbt) {
        this.tag = tag;
        this.blocks = set;
        this.properties = criteriontriggerproperties;
        this.nbt = criterionconditionnbt;
    }

    public boolean a(WorldServer worldserver, BlockPosition blockposition) {
        if (this == CriterionConditionBlock.ANY) {
            return true;
        } else if (!worldserver.o(blockposition)) {
            return false;
        } else {
            IBlockData iblockdata = worldserver.getType(blockposition);

            if (this.tag != null && !iblockdata.a(this.tag)) {
                return false;
            } else if (this.blocks != null && !this.blocks.contains(iblockdata.getBlock())) {
                return false;
            } else if (!this.properties.a(iblockdata)) {
                return false;
            } else {
                if (this.nbt != CriterionConditionNBT.ANY) {
                    TileEntity tileentity = worldserver.getTileEntity(blockposition);

                    if (tileentity == null || !this.nbt.a((NBTBase) tileentity.save(new NBTTagCompound()))) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static CriterionConditionBlock a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "block");
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.a(jsonobject.get("nbt"));
            Set<Block> set = null;
            JsonArray jsonarray = ChatDeserializer.a(jsonobject, "blocks", (JsonArray) null);

            if (jsonarray != null) {
                Builder<Block> builder = ImmutableSet.builder();
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();
                    MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.a(jsonelement1, "block"));

                    builder.add((Block) IRegistry.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown block id '" + minecraftkey + "'");
                    }));
                }

                set = builder.build();
            }

            Tag<Block> tag = null;

            if (jsonobject.has("tag")) {
                MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.h(jsonobject, "tag"));

                tag = TagsInstance.a().a(IRegistry.BLOCK_REGISTRY, minecraftkey1, (minecraftkey2) -> {
                    return new JsonSyntaxException("Unknown block tag '" + minecraftkey2 + "'");
                });
            }

            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("state"));

            return new CriterionConditionBlock(tag, set, criteriontriggerproperties, criterionconditionnbt);
        } else {
            return CriterionConditionBlock.ANY;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionBlock.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.blocks != null) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = this.blocks.iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();

                    jsonarray.add(IRegistry.BLOCK.getKey(block).toString());
                }

                jsonobject.add("blocks", jsonarray);
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", TagsInstance.a().a(IRegistry.BLOCK_REGISTRY, this.tag, () -> {
                    return new IllegalStateException("Unknown block tag");
                }).toString());
            }

            jsonobject.add("nbt", this.nbt.a());
            jsonobject.add("state", this.properties.a());
            return jsonobject;
        }
    }

    public static class a {

        @Nullable
        private Set<Block> blocks;
        @Nullable
        private Tag<Block> tag;
        private CriterionTriggerProperties properties;
        private CriterionConditionNBT nbt;

        private a() {
            this.properties = CriterionTriggerProperties.ANY;
            this.nbt = CriterionConditionNBT.ANY;
        }

        public static CriterionConditionBlock.a a() {
            return new CriterionConditionBlock.a();
        }

        public CriterionConditionBlock.a a(Block... ablock) {
            this.blocks = ImmutableSet.copyOf(ablock);
            return this;
        }

        public CriterionConditionBlock.a a(Iterable<Block> iterable) {
            this.blocks = ImmutableSet.copyOf(iterable);
            return this;
        }

        public CriterionConditionBlock.a a(Tag<Block> tag) {
            this.tag = tag;
            return this;
        }

        public CriterionConditionBlock.a a(NBTTagCompound nbttagcompound) {
            this.nbt = new CriterionConditionNBT(nbttagcompound);
            return this;
        }

        public CriterionConditionBlock.a a(CriterionTriggerProperties criteriontriggerproperties) {
            this.properties = criteriontriggerproperties;
            return this;
        }

        public CriterionConditionBlock b() {
            return new CriterionConditionBlock(this.tag, this.blocks, this.properties, this.nbt);
        }
    }
}
