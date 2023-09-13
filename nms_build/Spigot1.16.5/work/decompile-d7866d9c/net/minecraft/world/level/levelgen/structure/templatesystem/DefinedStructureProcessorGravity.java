package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.levelgen.HeightMap;

public class DefinedStructureProcessorGravity extends DefinedStructureProcessor {

    public static final Codec<DefinedStructureProcessorGravity> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(HeightMap.Type.g.fieldOf("heightmap").orElse(HeightMap.Type.WORLD_SURFACE_WG).forGetter((definedstructureprocessorgravity) -> {
            return definedstructureprocessorgravity.b;
        }), Codec.INT.fieldOf("offset").orElse(0).forGetter((definedstructureprocessorgravity) -> {
            return definedstructureprocessorgravity.c;
        })).apply(instance, DefinedStructureProcessorGravity::new);
    });
    private final HeightMap.Type b;
    private final int c;

    public DefinedStructureProcessorGravity(HeightMap.Type heightmap_type, int i) {
        this.b = heightmap_type;
        this.c = i;
    }

    @Nullable
    @Override
    public DefinedStructure.BlockInfo a(IWorldReader iworldreader, BlockPosition blockposition, BlockPosition blockposition1, DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1, DefinedStructureInfo definedstructureinfo) {
        HeightMap.Type heightmap_type;

        if (iworldreader instanceof WorldServer) {
            if (this.b == HeightMap.Type.WORLD_SURFACE_WG) {
                heightmap_type = HeightMap.Type.WORLD_SURFACE;
            } else if (this.b == HeightMap.Type.OCEAN_FLOOR_WG) {
                heightmap_type = HeightMap.Type.OCEAN_FLOOR;
            } else {
                heightmap_type = this.b;
            }
        } else {
            heightmap_type = this.b;
        }

        int i = iworldreader.a(heightmap_type, definedstructure_blockinfo1.a.getX(), definedstructure_blockinfo1.a.getZ()) + this.c;
        int j = definedstructure_blockinfo.a.getY();

        return new DefinedStructure.BlockInfo(new BlockPosition(definedstructure_blockinfo1.a.getX(), i + j, definedstructure_blockinfo1.a.getZ()), definedstructure_blockinfo1.b, definedstructure_blockinfo1.c);
    }

    @Override
    protected DefinedStructureStructureProcessorType<?> a() {
        return DefinedStructureStructureProcessorType.c;
    }
}
