package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.util.INamable;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.biome.BiomeSettingsMobs;

public record StructureSpawnOverride(StructureSpawnOverride.a b, WeightedRandomList<BiomeSettingsMobs.c> c) {

    private final StructureSpawnOverride.a boundingBox;
    private final WeightedRandomList<BiomeSettingsMobs.c> spawns;
    public static final Codec<StructureSpawnOverride> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(StructureSpawnOverride.a.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox), WeightedRandomList.codec(BiomeSettingsMobs.c.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)).apply(instance, StructureSpawnOverride::new);
    });

    public StructureSpawnOverride(StructureSpawnOverride.a structurespawnoverride_a, WeightedRandomList<BiomeSettingsMobs.c> weightedrandomlist) {
        this.boundingBox = structurespawnoverride_a;
        this.spawns = weightedrandomlist;
    }

    public StructureSpawnOverride.a boundingBox() {
        return this.boundingBox;
    }

    public WeightedRandomList<BiomeSettingsMobs.c> spawns() {
        return this.spawns;
    }

    public static enum a implements INamable {

        PIECE("piece"), STRUCTURE("full");

        public static final StructureSpawnOverride.a[] VALUES = values();
        public static final Codec<StructureSpawnOverride.a> CODEC = INamable.fromEnum(() -> {
            return StructureSpawnOverride.a.VALUES;
        }, StructureSpawnOverride.a::byName);
        private final String id;

        private a(String s) {
            this.id = s;
        }

        @Override
        public String getSerializedName() {
            return this.id;
        }

        @Nullable
        public static StructureSpawnOverride.a byName(@Nullable String s) {
            if (s == null) {
                return null;
            } else {
                StructureSpawnOverride.a[] astructurespawnoverride_a = StructureSpawnOverride.a.VALUES;
                int i = astructurespawnoverride_a.length;

                for (int j = 0; j < i; ++j) {
                    StructureSpawnOverride.a structurespawnoverride_a = astructurespawnoverride_a[j];

                    if (structurespawnoverride_a.id.equals(s)) {
                        return structurespawnoverride_a;
                    }
                }

                return null;
            }
        }
    }
}
