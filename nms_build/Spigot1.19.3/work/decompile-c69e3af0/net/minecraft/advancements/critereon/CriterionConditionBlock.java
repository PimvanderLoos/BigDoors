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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class CriterionConditionBlock {

    public static final CriterionConditionBlock ANY = new CriterionConditionBlock((TagKey) null, (Set) null, CriterionTriggerProperties.ANY, CriterionConditionNBT.ANY);
    @Nullable
    private final TagKey<Block> tag;
    @Nullable
    private final Set<Block> blocks;
    private final CriterionTriggerProperties properties;
    private final CriterionConditionNBT nbt;

    public CriterionConditionBlock(@Nullable TagKey<Block> tagkey, @Nullable Set<Block> set, CriterionTriggerProperties criteriontriggerproperties, CriterionConditionNBT criterionconditionnbt) {
        this.tag = tagkey;
        this.blocks = set;
        this.properties = criteriontriggerproperties;
        this.nbt = criterionconditionnbt;
    }

    public boolean matches(WorldServer worldserver, BlockPosition blockposition) {
        if (this == CriterionConditionBlock.ANY) {
            return true;
        } else if (!worldserver.isLoaded(blockposition)) {
            return false;
        } else {
            IBlockData iblockdata = worldserver.getBlockState(blockposition);

            if (this.tag != null && !iblockdata.is(this.tag)) {
                return false;
            } else if (this.blocks != null && !this.blocks.contains(iblockdata.getBlock())) {
                return false;
            } else if (!this.properties.matches(iblockdata)) {
                return false;
            } else {
                if (this.nbt != CriterionConditionNBT.ANY) {
                    TileEntity tileentity = worldserver.getBlockEntity(blockposition);

                    if (tileentity == null || !this.nbt.matches((NBTBase) tileentity.saveWithFullMetadata())) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static CriterionConditionBlock fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "block");
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.fromJson(jsonobject.get("nbt"));
            Set<Block> set = null;
            JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "blocks", (JsonArray) null);

            if (jsonarray != null) {
                Builder<Block> builder = ImmutableSet.builder();
                Iterator iterator = jsonarray.iterator();

                while (iterator.hasNext()) {
                    JsonElement jsonelement1 = (JsonElement) iterator.next();
                    MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.convertToString(jsonelement1, "block"));

                    builder.add((Block) BuiltInRegistries.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown block id '" + minecraftkey + "'");
                    }));
                }

                set = builder.build();
            }

            TagKey<Block> tagkey = null;

            if (jsonobject.has("tag")) {
                MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "tag"));

                tagkey = TagKey.create(Registries.BLOCK, minecraftkey1);
            }

            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.fromJson(jsonobject.get("state"));

            return new CriterionConditionBlock(tagkey, set, criteriontriggerproperties, criterionconditionnbt);
        } else {
            return CriterionConditionBlock.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionBlock.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.blocks != null) {
                JsonArray jsonarray = new JsonArray();
                Iterator iterator = this.blocks.iterator();

                while (iterator.hasNext()) {
                    Block block = (Block) iterator.next();

                    jsonarray.add(BuiltInRegistries.BLOCK.getKey(block).toString());
                }

                jsonobject.add("blocks", jsonarray);
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", this.tag.location().toString());
            }

            jsonobject.add("nbt", this.nbt.serializeToJson());
            jsonobject.add("state", this.properties.serializeToJson());
            return jsonobject;
        }
    }

    public static class a {

        @Nullable
        private Set<Block> blocks;
        @Nullable
        private TagKey<Block> tag;
        private CriterionTriggerProperties properties;
        private CriterionConditionNBT nbt;

        private a() {
            this.properties = CriterionTriggerProperties.ANY;
            this.nbt = CriterionConditionNBT.ANY;
        }

        public static CriterionConditionBlock.a block() {
            return new CriterionConditionBlock.a();
        }

        public CriterionConditionBlock.a of(Block... ablock) {
            this.blocks = ImmutableSet.copyOf(ablock);
            return this;
        }

        public CriterionConditionBlock.a of(Iterable<Block> iterable) {
            this.blocks = ImmutableSet.copyOf(iterable);
            return this;
        }

        public CriterionConditionBlock.a of(TagKey<Block> tagkey) {
            this.tag = tagkey;
            return this;
        }

        public CriterionConditionBlock.a hasNbt(NBTTagCompound nbttagcompound) {
            this.nbt = new CriterionConditionNBT(nbttagcompound);
            return this;
        }

        public CriterionConditionBlock.a setProperties(CriterionTriggerProperties criteriontriggerproperties) {
            this.properties = criteriontriggerproperties;
            return this;
        }

        public CriterionConditionBlock build() {
            return new CriterionConditionBlock(this.tag, this.blocks, this.properties, this.nbt);
        }
    }
}
