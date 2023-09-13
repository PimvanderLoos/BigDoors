package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.slf4j.Logger;

public record PiecesContainer(List<StructurePiece> pieces) {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MinecraftKey JIGSAW_RENAME = new MinecraftKey("jigsaw");
    private static final Map<MinecraftKey, MinecraftKey> RENAMES = ImmutableMap.builder().put(new MinecraftKey("nvi"), PiecesContainer.JIGSAW_RENAME).put(new MinecraftKey("pcp"), PiecesContainer.JIGSAW_RENAME).put(new MinecraftKey("bastionremnant"), PiecesContainer.JIGSAW_RENAME).put(new MinecraftKey("runtime"), PiecesContainer.JIGSAW_RENAME).build();

    public PiecesContainer(List<StructurePiece> list) {
        this.pieces = List.copyOf(list);
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public boolean isInsidePiece(BlockPosition blockposition) {
        Iterator iterator = this.pieces.iterator();

        StructurePiece structurepiece;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            structurepiece = (StructurePiece) iterator.next();
        } while (!structurepiece.getBoundingBox().isInside(blockposition));

        return true;
    }

    public NBTBase save(StructurePieceSerializationContext structurepieceserializationcontext) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.pieces.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            nbttaglist.add(structurepiece.createTag(structurepieceserializationcontext));
        }

        return nbttaglist;
    }

    public static PiecesContainer load(NBTTagList nbttaglist, StructurePieceSerializationContext structurepieceserializationcontext) {
        List<StructurePiece> list = Lists.newArrayList();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            String s = nbttagcompound.getString("id").toLowerCase(Locale.ROOT);
            MinecraftKey minecraftkey = new MinecraftKey(s);
            MinecraftKey minecraftkey1 = (MinecraftKey) PiecesContainer.RENAMES.getOrDefault(minecraftkey, minecraftkey);
            WorldGenFeatureStructurePieceType worldgenfeaturestructurepiecetype = (WorldGenFeatureStructurePieceType) BuiltInRegistries.STRUCTURE_PIECE.get(minecraftkey1);

            if (worldgenfeaturestructurepiecetype == null) {
                PiecesContainer.LOGGER.error("Unknown structure piece id: {}", minecraftkey1);
            } else {
                try {
                    StructurePiece structurepiece = worldgenfeaturestructurepiecetype.load(structurepieceserializationcontext, nbttagcompound);

                    list.add(structurepiece);
                } catch (Exception exception) {
                    PiecesContainer.LOGGER.error("Exception loading structure piece with id {}", minecraftkey1, exception);
                }
            }
        }

        return new PiecesContainer(list);
    }

    public StructureBoundingBox calculateBoundingBox() {
        return StructurePiece.createBoundingBox(this.pieces.stream());
    }
}
