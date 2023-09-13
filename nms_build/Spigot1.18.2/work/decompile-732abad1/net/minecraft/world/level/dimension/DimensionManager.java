package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class DimensionManager {

    public static final int BITS_FOR_Y = BlockPosition.PACKED_Y_LENGTH;
    public static final int MIN_HEIGHT = 16;
    public static final int Y_SIZE = (1 << DimensionManager.BITS_FOR_Y) - 32;
    public static final int MAX_Y = (DimensionManager.Y_SIZE >> 1) - 1;
    public static final int MIN_Y = DimensionManager.MAX_Y - DimensionManager.Y_SIZE + 1;
    public static final int WAY_ABOVE_MAX_Y = DimensionManager.MAX_Y << 4;
    public static final int WAY_BELOW_MIN_Y = DimensionManager.MIN_Y << 4;
    public static final MinecraftKey OVERWORLD_EFFECTS = new MinecraftKey("overworld");
    public static final MinecraftKey NETHER_EFFECTS = new MinecraftKey("the_nether");
    public static final MinecraftKey END_EFFECTS = new MinecraftKey("the_end");
    public static final Codec<DimensionManager> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((optional) -> {
            return (OptionalLong) optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
        }, (optionallong) -> {
            return optionallong.isPresent() ? Optional.of(optionallong.getAsLong()) : Optional.empty();
        }).forGetter((dimensionmanager) -> {
            return dimensionmanager.fixedTime;
        }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionManager::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionManager::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionManager::ultraWarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionManager::natural), Codec.doubleRange(9.999999747378752E-6D, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionManager::coordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionManager::piglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionManager::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionManager::respawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionManager::hasRaids), Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("min_y").forGetter(DimensionManager::minY), Codec.intRange(16, DimensionManager.Y_SIZE).fieldOf("height").forGetter(DimensionManager::height), Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("logical_height").forGetter(DimensionManager::logicalHeight), TagKey.hashedCodec(IRegistry.BLOCK_REGISTRY).fieldOf("infiniburn").forGetter((dimensionmanager) -> {
            return dimensionmanager.infiniburn;
        }), MinecraftKey.CODEC.fieldOf("effects").orElse(DimensionManager.OVERWORLD_EFFECTS).forGetter((dimensionmanager) -> {
            return dimensionmanager.effectsLocation;
        }), Codec.FLOAT.fieldOf("ambient_light").forGetter((dimensionmanager) -> {
            return dimensionmanager.ambientLight;
        })).apply(instance, DimensionManager::new);
    }).comapFlatMap(DimensionManager::guardY, Function.identity());
    private static final int MOON_PHASES = 8;
    public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public static final ResourceKey<DimensionManager> OVERWORLD_LOCATION = ResourceKey.create(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<DimensionManager> NETHER_LOCATION = ResourceKey.create(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("the_nether"));
    public static final ResourceKey<DimensionManager> END_LOCATION = ResourceKey.create(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("the_end"));
    protected static final DimensionManager DEFAULT_OVERWORLD = create(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, -64, 384, 384, TagsBlock.INFINIBURN_OVERWORLD, DimensionManager.OVERWORLD_EFFECTS, 0.0F);
    protected static final DimensionManager DEFAULT_NETHER = create(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, false, true, false, 0, 256, 128, TagsBlock.INFINIBURN_NETHER, DimensionManager.NETHER_EFFECTS, 0.1F);
    protected static final DimensionManager DEFAULT_END = create(OptionalLong.of(6000L), false, false, false, false, 1.0D, true, false, false, false, true, 0, 256, 256, TagsBlock.INFINIBURN_END, DimensionManager.END_EFFECTS, 0.0F);
    public static final ResourceKey<DimensionManager> OVERWORLD_CAVES_LOCATION = ResourceKey.create(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("overworld_caves"));
    protected static final DimensionManager DEFAULT_OVERWORLD_CAVES = create(OptionalLong.empty(), true, true, false, true, 1.0D, false, false, true, false, true, -64, 384, 384, TagsBlock.INFINIBURN_OVERWORLD, DimensionManager.OVERWORLD_EFFECTS, 0.0F);
    public static final Codec<Holder<DimensionManager>> CODEC = RegistryFileCodec.create(IRegistry.DIMENSION_TYPE_REGISTRY, DimensionManager.DIRECT_CODEC);
    private final OptionalLong fixedTime;
    private final boolean hasSkylight;
    private final boolean hasCeiling;
    private final boolean ultraWarm;
    private final boolean natural;
    private final double coordinateScale;
    private final boolean createDragonFight;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int minY;
    private final int height;
    private final int logicalHeight;
    private final TagKey<Block> infiniburn;
    private final MinecraftKey effectsLocation;
    private final float ambientLight;
    private final transient float[] brightnessRamp;

    private static DataResult<DimensionManager> guardY(DimensionManager dimensionmanager) {
        return dimensionmanager.height() < 16 ? DataResult.error("height has to be at least 16") : (dimensionmanager.minY() + dimensionmanager.height() > DimensionManager.MAX_Y + 1 ? DataResult.error("min_y + height cannot be higher than: " + (DimensionManager.MAX_Y + 1)) : (dimensionmanager.logicalHeight() > dimensionmanager.height() ? DataResult.error("logical_height cannot be higher than height") : (dimensionmanager.height() % 16 != 0 ? DataResult.error("height has to be multiple of 16") : (dimensionmanager.minY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(dimensionmanager)))));
    }

    private DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, int i, int j, int k, TagKey<Block> tagkey, MinecraftKey minecraftkey, float f) {
        this(optionallong, flag, flag1, flag2, flag3, d0, false, flag4, flag5, flag6, flag7, i, j, k, tagkey, minecraftkey, f);
    }

    public static DimensionManager create(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8, int i, int j, int k, TagKey<Block> tagkey, MinecraftKey minecraftkey, float f) {
        DimensionManager dimensionmanager = new DimensionManager(optionallong, flag, flag1, flag2, flag3, d0, flag4, flag5, flag6, flag7, flag8, i, j, k, tagkey, minecraftkey, f);

        guardY(dimensionmanager).error().ifPresent((partialresult) -> {
            throw new IllegalStateException(partialresult.message());
        });
        return dimensionmanager;
    }

    /** @deprecated */
    @Deprecated
    private DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8, int i, int j, int k, TagKey<Block> tagkey, MinecraftKey minecraftkey, float f) {
        this.fixedTime = optionallong;
        this.hasSkylight = flag;
        this.hasCeiling = flag1;
        this.ultraWarm = flag2;
        this.natural = flag3;
        this.coordinateScale = d0;
        this.createDragonFight = flag4;
        this.piglinSafe = flag5;
        this.bedWorks = flag6;
        this.respawnAnchorWorks = flag7;
        this.hasRaids = flag8;
        this.minY = i;
        this.height = j;
        this.logicalHeight = k;
        this.infiniburn = tagkey;
        this.effectsLocation = minecraftkey;
        this.ambientLight = f;
        this.brightnessRamp = fillBrightnessRamp(f);
    }

    private static float[] fillBrightnessRamp(float f) {
        float[] afloat = new float[16];

        for (int i = 0; i <= 15; ++i) {
            float f1 = (float) i / 15.0F;
            float f2 = f1 / (4.0F - 3.0F * f1);

            afloat[i] = MathHelper.lerp(f, f2, 1.0F);
        }

        return afloat;
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

    public static IRegistryCustom.e registerBuiltin(IRegistryCustom.e iregistrycustom_e) {
        IRegistryWritable<DimensionManager> iregistrywritable = iregistrycustom_e.ownedWritableRegistryOrThrow(IRegistry.DIMENSION_TYPE_REGISTRY);

        iregistrywritable.register(DimensionManager.OVERWORLD_LOCATION, (Object) DimensionManager.DEFAULT_OVERWORLD, Lifecycle.stable());
        iregistrywritable.register(DimensionManager.OVERWORLD_CAVES_LOCATION, (Object) DimensionManager.DEFAULT_OVERWORLD_CAVES, Lifecycle.stable());
        iregistrywritable.register(DimensionManager.NETHER_LOCATION, (Object) DimensionManager.DEFAULT_NETHER, Lifecycle.stable());
        iregistrywritable.register(DimensionManager.END_LOCATION, (Object) DimensionManager.DEFAULT_END, Lifecycle.stable());
        return iregistrycustom_e;
    }

    public static IRegistry<WorldDimension> defaultDimensions(IRegistryCustom iregistrycustom, long i) {
        return defaultDimensions(iregistrycustom, i, true);
    }

    public static IRegistry<WorldDimension> defaultDimensions(IRegistryCustom iregistrycustom, long i, boolean flag) {
        IRegistryWritable<WorldDimension> iregistrywritable = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function) null);
        IRegistry<DimensionManager> iregistry = iregistrycustom.registryOrThrow(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<BiomeBase> iregistry1 = iregistrycustom.registryOrThrow(IRegistry.BIOME_REGISTRY);
        IRegistry<StructureSet> iregistry2 = iregistrycustom.registryOrThrow(IRegistry.STRUCTURE_SET_REGISTRY);
        IRegistry<GeneratorSettingBase> iregistry3 = iregistrycustom.registryOrThrow(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        IRegistry<NoiseGeneratorNormal.a> iregistry4 = iregistrycustom.registryOrThrow(IRegistry.NOISE_REGISTRY);

        iregistrywritable.register(WorldDimension.NETHER, (Object) (new WorldDimension(iregistry.getOrCreateHolder(DimensionManager.NETHER_LOCATION), new ChunkGeneratorAbstract(iregistry2, iregistry4, WorldChunkManagerMultiNoise.a.NETHER.biomeSource(iregistry1, flag), i, iregistry3.getOrCreateHolder(GeneratorSettingBase.NETHER)))), Lifecycle.stable());
        iregistrywritable.register(WorldDimension.END, (Object) (new WorldDimension(iregistry.getOrCreateHolder(DimensionManager.END_LOCATION), new ChunkGeneratorAbstract(iregistry2, iregistry4, new WorldChunkManagerTheEnd(iregistry1, i), i, iregistry3.getOrCreateHolder(GeneratorSettingBase.END)))), Lifecycle.stable());
        return iregistrywritable;
    }

    public static double getTeleportationScale(DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
        double d0 = dimensionmanager.coordinateScale();
        double d1 = dimensionmanager1.coordinateScale();

        return d0 / d1;
    }

    public static Path getStorageFolder(ResourceKey<World> resourcekey, Path path) {
        return resourcekey == World.OVERWORLD ? path : (resourcekey == World.END ? path.resolve("DIM1") : (resourcekey == World.NETHER ? path.resolve("DIM-1") : path.resolve("dimensions").resolve(resourcekey.location().getNamespace()).resolve(resourcekey.location().getPath())));
    }

    public boolean hasSkyLight() {
        return this.hasSkylight;
    }

    public boolean hasCeiling() {
        return this.hasCeiling;
    }

    public boolean ultraWarm() {
        return this.ultraWarm;
    }

    public boolean natural() {
        return this.natural;
    }

    public double coordinateScale() {
        return this.coordinateScale;
    }

    public boolean piglinSafe() {
        return this.piglinSafe;
    }

    public boolean bedWorks() {
        return this.bedWorks;
    }

    public boolean respawnAnchorWorks() {
        return this.respawnAnchorWorks;
    }

    public boolean hasRaids() {
        return this.hasRaids;
    }

    public int minY() {
        return this.minY;
    }

    public int height() {
        return this.height;
    }

    public int logicalHeight() {
        return this.logicalHeight;
    }

    public boolean createDragonFight() {
        return this.createDragonFight;
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

    public float brightness(int i) {
        return this.brightnessRamp[i];
    }

    public TagKey<Block> infiniburn() {
        return this.infiniburn;
    }

    public MinecraftKey effectsLocation() {
        return this.effectsLocation;
    }
}
