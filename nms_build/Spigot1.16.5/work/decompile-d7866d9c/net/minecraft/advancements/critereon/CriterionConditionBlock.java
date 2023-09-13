package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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

    public static final CriterionConditionBlock a = new CriterionConditionBlock((Tag) null, (Block) null, CriterionTriggerProperties.a, CriterionConditionNBT.a);
    @Nullable
    private final Tag<Block> b;
    @Nullable
    private final Block c;
    private final CriterionTriggerProperties d;
    private final CriterionConditionNBT e;

    public CriterionConditionBlock(@Nullable Tag<Block> tag, @Nullable Block block, CriterionTriggerProperties criteriontriggerproperties, CriterionConditionNBT criterionconditionnbt) {
        this.b = tag;
        this.c = block;
        this.d = criteriontriggerproperties;
        this.e = criterionconditionnbt;
    }

    public boolean a(WorldServer worldserver, BlockPosition blockposition) {
        if (this == CriterionConditionBlock.a) {
            return true;
        } else if (!worldserver.p(blockposition)) {
            return false;
        } else {
            IBlockData iblockdata = worldserver.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (this.b != null && !this.b.isTagged(block)) {
                return false;
            } else if (this.c != null && block != this.c) {
                return false;
            } else if (!this.d.a(iblockdata)) {
                return false;
            } else {
                if (this.e != CriterionConditionNBT.a) {
                    TileEntity tileentity = worldserver.getTileEntity(blockposition);

                    if (tileentity == null || !this.e.a((NBTBase) tileentity.save(new NBTTagCompound()))) {
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
            Block block = null;

            if (jsonobject.has("block")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "block"));

                block = (Block) IRegistry.BLOCK.get(minecraftkey);
            }

            Tag<Block> tag = null;

            if (jsonobject.has("tag")) {
                MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.h(jsonobject, "tag"));

                tag = TagsInstance.a().getBlockTags().a(minecraftkey1);
                if (tag == null) {
                    throw new JsonSyntaxException("Unknown block tag '" + minecraftkey1 + "'");
                }
            }

            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("state"));

            return new CriterionConditionBlock(tag, block, criteriontriggerproperties, criterionconditionnbt);
        } else {
            return CriterionConditionBlock.a;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionBlock.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.c != null) {
                jsonobject.addProperty("block", IRegistry.BLOCK.getKey(this.c).toString());
            }

            if (this.b != null) {
                jsonobject.addProperty("tag", TagsInstance.a().getBlockTags().b(this.b).toString());
            }

            jsonobject.add("nbt", this.e.a());
            jsonobject.add("state", this.d.a());
            return jsonobject;
        }
    }

    public static class a {

        @Nullable
        private Block a;
        @Nullable
        private Tag<Block> b;
        private CriterionTriggerProperties c;
        private CriterionConditionNBT d;

        private a() {
            this.c = CriterionTriggerProperties.a;
            this.d = CriterionConditionNBT.a;
        }

        public static CriterionConditionBlock.a a() {
            return new CriterionConditionBlock.a();
        }

        public CriterionConditionBlock.a a(Block block) {
            this.a = block;
            return this;
        }

        public CriterionConditionBlock.a a(Tag<Block> tag) {
            this.b = tag;
            return this;
        }

        public CriterionConditionBlock.a a(CriterionTriggerProperties criteriontriggerproperties) {
            this.c = criteriontriggerproperties;
            return this;
        }

        public CriterionConditionBlock b() {
            return new CriterionConditionBlock(this.b, this.a, this.c, this.d);
        }
    }
}
