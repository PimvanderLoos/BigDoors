package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.MathHelper;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;

public record DimensionManager(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultraWarm, boolean natural, double coordinateScale, boolean bedWorks, boolean respawnAnchorWorks, int minY, int height, int logicalHeight, TagKey<Block> infiniburn, MinecraftKey effectsLocation, float ambientLight, DimensionManager.a monsterSettings) {

    public static final int BITS_FOR_Y = BlockPosition.PACKED_Y_LENGTH;
    public static final int MIN_HEIGHT = 16;
    public static final int Y_SIZE = (1 << DimensionManager.BITS_FOR_Y) - 32;
    public static final int MAX_Y = (DimensionManager.Y_SIZE >> 1) - 1;
    public static final int MIN_Y = DimensionManager.MAX_Y - DimensionManager.Y_SIZE + 1;
    public static final int WAY_ABOVE_MAX_Y = DimensionManager.MAX_Y << 4;
    public static final int WAY_BELOW_MIN_Y = DimensionManager.MIN_Y << 4;
    public static final Codec<DimensionManager> DIRECT_CODEC = ExtraCodecs.catchDecoderException(RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.asOptionalLong(Codec.LONG.optionalFieldOf("fixed_time")).forGetter(DimensionManager::fixedTime), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionManager::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionManager::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionManager::ultraWarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionManager::natural), Codec.doubleRange(9.999999747378752E-6D, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionManager::coordinateScale), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionManager::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionManager::respawnAnchorWorks), Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("min_y").forGetter(DimensionManager::minY), Codec.intRange(16, DimensionManager.Y_SIZE).fieldOf("height").forGetter(DimensionManager::height), Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("logical_height").forGetter(DimensionManager::logicalHeight), TagKey.hashedCodec(Registries.BLOCK).fieldOf("infiniburn").forGetter(DimensionManager::infiniburn), MinecraftKey.CODEC.fieldOf("effects").orElse(BuiltinDimensionTypes.OVERWORLD_EFFECTS).forGetter(DimensionManager::effectsLocation), Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionManager::ambientLight), DimensionManager.a.CODEC.forGetter(DimensionManager::monsterSettings)).apply(instance, DimensionManager::new);
    }));
    private static final int MOON_PHASES = 8;
    public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public static final Codec<Holder<DimensionManager>> CODEC = RegistryFileCodec.create(Registries.DIMENSION_TYPE, DimensionManager.DIRECT_CODEC);

    public DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, int i, int j, int k, TagKey<Block> tagkey, MinecraftKey minecraftkey, float f, DimensionManager.a dimensionmanager_a) {
        if (j < 16) {
            throw new IllegalStateException("height has to be at least 16");
        } else if (i + j > DimensionManager.MAX_Y + 1) {
            throw new IllegalStateException("min_y + height cannot be higher than: " + (DimensionManager.MAX_Y + 1));
        } else if (k > j) {
            throw new IllegalStateException("logical_height cannot be higher than height");
        } else if (j % 16 != 0) {
            throw new IllegalStateException("height has to be multiple of 16");
        } else if (i % 16 != 0) {
            throw new IllegalStateException("min_y has to be a multiple of 16");
        } else {
            this.fixedTime = optionallong;
            this.hasSkyLight = flag;
            this.hasCeiling = flag1;
            this.ultraWarm = flag2;
            this.natural = flag3;
            this.coordinateScale = d0;
            this.bedWorks = flag4;
            this.respawnAnchorWorks = flag5;
            this.minY = i;
            this.height = j;
            this.logicalHeight = k;
            this.infiniburn = tagkey;
            this.effectsLocation = minecraftkey;
            this.ambientLight = f;
            this.monsterSettings = dimensionmanager_a;
        }
    }

    /** @deprecated */
    @Deprecated
    public static DataResult<ResourceKey<World>> parseLegacy(Dynamic<?> dynamic) {
        Optional<Number> optional = dynamic.asNumber().result();

        if (optional.isPresent()) {
            int i = ((Number) optional.get()).intValue();

            if (i == -1) {
                return DataResult.success(World.NETHER);
            }

            if (i == 0) {
                return DataResult.success(World.OVERWORLD);
            }

            if (i == 1) {
                return DataResult.success(World.END);
            }
        }

        return World.RESOURCE_KEY_CODEC.parse(dynamic);
    }

    public static double getTeleportationScale(DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
        double d0 = dimensionmanager.coordinateScale();
        double d1 = dimensionmanager1.coordinateScale();

        return d0 / d1;
    }

    public static Path getStorageFolder(ResourceKey<World> resourcekey, Path path) {
        return resourcekey == World.OVERWORLD ? path : (resourcekey == World.END ? path.resolve("DIM1") : (resourcekey == World.NETHER ? path.resolve("DIM-1") : path.resolve("dimensions").resolve(resourcekey.location().getNamespace()).resolve(resourcekey.location().getPath())));
    }

    public boolean hasFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float timeOfDay(long i) {
        double d0 = MathHelper.frac((double) this.fixedTime.orElse(i) / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;

        return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    public int moonPhase(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    public boolean piglinSafe() {
        return this.monsterSettings.piglinSafe();
    }

    public boolean hasRaids() {
        return this.monsterSettings.hasRaids();
    }

    public IntProvider monsterSpawnLightTest() {
        return this.monsterSettings.monsterSpawnLightTest();
    }

    public int monsterSpawnBlockLightLimit() {
        return this.monsterSettings.monsterSpawnBlockLightLimit();
    }

    public static record a(boolean piglinSafe, boolean hasRaids, IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {

        public static final MapCodec<DimensionManager.a> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionManager.a::piglinSafe), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionManager.a::hasRaids), IntProvider.codec(0, 15).fieldOf("monster_spawn_light_level").forGetter(DimensionManager.a::monsterSpawnLightTest), Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(DimensionManager.a::monsterSpawnBlockLightLimit)).apply(instance, DimensionManager.a::new);
        });
    }
}
