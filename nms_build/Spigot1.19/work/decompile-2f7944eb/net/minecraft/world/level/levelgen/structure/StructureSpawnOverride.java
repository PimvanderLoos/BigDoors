package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.INamable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.BiomeSettingsMobs;

public record StructureSpawnOverride(StructureSpawnOverride.a boundingBox, WeightedRandomList<BiomeSettingsMobs.c> spawns) {

    public static final Codec<StructureSpawnOverride> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSpawnOverride.a.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox), WeightedRandomList.codec(BiomeSettingsMobs.c.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)).apply(instance, StructureSpawnOverride::new);
    });

    public static enum a implements INamable {

        PIECE("piece"), STRUCTURE("full");

        public static final Codec<StructureSpawnOverride.a> CODEC = INamable.fromEnum(StructureSpawnOverride.a::values);
        private final String id;

        private a(String s) {
            this.id = s;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }
    }
}
