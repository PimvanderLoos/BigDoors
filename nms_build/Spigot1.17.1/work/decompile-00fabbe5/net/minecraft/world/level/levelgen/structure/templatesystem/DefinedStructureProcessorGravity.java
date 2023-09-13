package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.levelgen.HeightMap;

public class DefinedStructureProcessorGravity extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorGravity> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(HeightMap.Type.CODEC.fieldOf("heightmap").orElse(HeightMap.Type.WORLD_SURFACE_WG).forGetter((definedstructureprocessorgravity) -> {
            return definedstructureprocessorgravity.heightmap;
        }), Codec.INT.fieldOf("offset").orElse(0).forGetter((definedstructureprocessorgravity) -> {
            return definedstructureprocessorgravity.offset;
        })).apply(instance, DefinedStructureProcessorGravity::new);
    });
    private final HeightMap.Type heightmap;
    private final int offset;

    public DefinedStructureProcessorGravity(HeightMap.Type heightmap_type, int i) {
        this.heightmap = heightmap_type;
        this.offset = i;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        HeightMap.Type heightmap_type;

        if (iworldreader instanceof WorldServer) {
            if (this.heightmap == HeightMap.Type.WORLD_SURFACE_WG) {
                heightmap_type = HeightMap.Type.WORLD_SURFACE;
            } else if (this.heightmap == HeightMap.Type.OCEAN_FLOOR_WG) {
                heightmap_type = HeightMap.Type.OCEAN_FLOOR;
            } else {
                heightmap_type = this.heightmap;
            }
        } else {
            heightmap_type = this.heightmap;
        }

        int i = iworldreader.a(heightmap_type, definedstructure_blockinfo1.pos.getX(), definedstructure_blockinfo1.pos.getZ()) + this.offset;
        int j = definedstructure_blockinfo.pos.getY();

        return new DefinedStructure.BlockInfo(new BlockPosition(definedstructure_blockinfo1.pos.getX(), i + j, definedstructure_blockinfo1.pos.getZ()), definedstructure_blockinfo1.state, definedstructure_blockinfo1.nbt);
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.GRAVITY;
    }
}
