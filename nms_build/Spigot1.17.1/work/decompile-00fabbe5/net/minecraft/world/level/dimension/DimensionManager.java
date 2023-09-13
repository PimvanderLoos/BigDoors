package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.GenLayerZoomVoronoi;
import net.minecraft.world.level.biome.GenLayerZoomVoronoiFixed;
import net.minecraft.world.level.biome.GenLayerZoomer;
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.biome.WorldChunkManagerTheEnd;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;

public class DimensionManager {

    public static final int BITS_FOR_Y = BlockPosition.PACKED_Y_LENGTH;
    public static final int MIN_HEIGHT = 16;
    public static final int Y_SIZE = (1 << DimensionManager.BITS_FOR_Y) - 32;
    public static final int MAX_Y = (DimensionManager.Y_SIZE >> 1) - 1;
    public static final int MIN_Y = DimensionManager.MAX_Y - DimensionManager.Y_SIZE + 1;
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
        }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionManager::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionManager::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionManager::isNether), Codec.BOOL.fieldOf("natural").forGetter(DimensionManager::isNatural), Codec.doubleRange(9.999999747378752E-6D, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionManager::getCoordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionManager::isPiglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionManager::isBedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionManager::isRespawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionManager::hasRaids), Codec.intRange(DimensionManager.MIN_Y, DimensionManager.MAX_Y).fieldOf("min_y").forGetter(DimensionManager::getMinY), Codec.intRange(16, DimensionManager.Y_SIZE).fieldOf("height").forGetter(DimensionManager::getHeight), Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("logical_height").forGetter(DimensionManager::getLogicalHeight), MinecraftKey.CODEC.fieldOf("infiniburn").forGetter((dimensionmanager) -> {
            return dimensionmanager.infiniburn;
        }), MinecraftKey.CODEC.fieldOf("effects").orElse(DimensionManager.OVERWORLD_EFFECTS).forGetter((dimensionmanager) -> {
            return dimensionmanager.effectsLocation;
        }), Codec.FLOAT.fieldOf("ambient_light").forGetter((dimensionmanager) -> {
            return dimensionmanager.ambientLight;
        })).apply(instance, DimensionManager::new);
    }).comapFlatMap(DimensionManager::b, Function.identity());
    private static final int MOON_PHASES = 8;
    public static final float[] MOON_BRIGHTNESS_PER_PHASE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public static final ResourceKey<DimensionManager> OVERWORLD_LOCATION = ResourceKey.a(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<DimensionManager> NETHER_LOCATION = ResourceKey.a(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("the_nether"));
    public static final ResourceKey<DimensionManager> END_LOCATION = ResourceKey.a(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("the_end"));
    protected static final DimensionManager DEFAULT_OVERWORLD = a(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, 0, 256, 256, GenLayerZoomVoronoiFixed.INSTANCE, TagsBlock.INFINIBURN_OVERWORLD.a(), DimensionManager.OVERWORLD_EFFECTS, 0.0F);
    protected static final DimensionManager DEFAULT_NETHER = a(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, false, true, false, 0, 256, 128, GenLayerZoomVoronoi.INSTANCE, TagsBlock.INFINIBURN_NETHER.a(), DimensionManager.NETHER_EFFECTS, 0.1F);
    protected static final DimensionManager DEFAULT_END = a(OptionalLong.of(6000L), false, false, false, false, 1.0D, true, false, false, false, true, 0, 256, 256, GenLayerZoomVoronoi.INSTANCE, TagsBlock.INFINIBURN_END.a(), DimensionManager.END_EFFECTS, 0.0F);
    public static final ResourceKey<DimensionManager> OVERWORLD_CAVES_LOCATION = ResourceKey.a(IRegistry.DIMENSION_TYPE_REGISTRY, new MinecraftKey("overworld_caves"));
    protected static final DimensionManager DEFAULT_OVERWORLD_CAVES = a(OptionalLong.empty(), true, true, false, true, 1.0D, false, false, true, false, true, 0, 256, 256, GenLayerZoomVoronoiFixed.INSTANCE, TagsBlock.INFINIBURN_OVERWORLD.a(), DimensionManager.OVERWORLD_EFFECTS, 0.0F);
    public static final Codec<Supplier<DimensionManager>> CODEC = RegistryFileCodec.a(IRegistry.DIMENSION_TYPE_REGISTRY, DimensionManager.DIRECT_CODEC);
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
    private final GenLayerZoomer biomeZoomer;
    private final MinecraftKey infiniburn;
    private final MinecraftKey effectsLocation;
    private final float ambientLight;
    private final transient float[] brightnessRamp;

    private static DataResult<DimensionManager> b(DimensionManager dimensionmanager) {
        return dimensionmanager.getHeight() < 16 ? DataResult.error("height has to be at least 16") : (dimensionmanager.getMinY() + dimensionmanager.getHeight() > DimensionManager.MAX_Y + 1 ? DataResult.error("min_y + height cannot be higher than: " + (DimensionManager.MAX_Y + 1)) : (dimensionmanager.getLogicalHeight() > dimensionmanager.getHeight() ? DataResult.error("logical_height cannot be higher than height") : (dimensionmanager.getHeight() % 16 != 0 ? DataResult.error("height has to be multiple of 16") : (dimensionmanager.getMinY() % 16 != 0 ? DataResult.error("min_y has to be a multiple of 16") : DataResult.success(dimensionmanager)))));
    }

    private DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, int i, int j, int k, MinecraftKey minecraftkey, MinecraftKey minecraftkey1, float f) {
        this(optionallong, flag, flag1, flag2, flag3, d0, false, flag4, flag5, flag6, flag7, i, j, k, GenLayerZoomVoronoi.INSTANCE, minecraftkey, minecraftkey1, f);
    }

    public static DimensionManager a(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8, int i, int j, int k, GenLayerZoomer genlayerzoomer, MinecraftKey minecraftkey, MinecraftKey minecraftkey1, float f) {
        DimensionManager dimensionmanager = new DimensionManager(optionallong, flag, flag1, flag2, flag3, d0, flag4, flag5, flag6, flag7, flag8, i, j, k, genlayerzoomer, minecraftkey, minecraftkey1, f);

        b(dimensionmanager).error().ifPresent((partialresult) -> {
            throw new IllegalStateException(partialresult.message());
        });
        return dimensionmanager;
    }

    @Deprecated
    private DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8, int i, int j, int k, GenLayerZoomer genlayerzoomer, MinecraftKey minecraftkey, MinecraftKey minecraftkey1, float f) {
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
        this.biomeZoomer = genlayerzoomer;
        this.infiniburn = minecraftkey;
        this.effectsLocation = minecraftkey1;
        this.ambientLight = f;
        this.brightnessRamp = a(f);
    }

    private static float[] a(float f) {
        float[] afloat = new float[16];

        for (int i = 0; i <= 15; ++i) {
            float f1 = (float) i / 15.0F;
            float f2 = f1 / (4.0F - 3.0F * f1);

            afloat[i] = MathHelper.h(f, f2, 1.0F);
        }

        return afloat;
    }

    @Deprecated
    public static DataResult<ResourceKey<World>> a(Dynamic<?> dynamic) {
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

    public static IRegistryCustom.Dimension a(IRegistryCustom.Dimension iregistrycustom_dimension) {
        IRegistryWritable<DimensionManager> iregistrywritable = iregistrycustom_dimension.b(IRegistry.DIMENSION_TYPE_REGISTRY);

        iregistrywritable.a(DimensionManager.OVERWORLD_LOCATION, (Object) DimensionManager.DEFAULT_OVERWORLD, Lifecycle.stable());
        iregistrywritable.a(DimensionManager.OVERWORLD_CAVES_LOCATION, (Object) DimensionManager.DEFAULT_OVERWORLD_CAVES, Lifecycle.stable());
        iregistrywritable.a(DimensionManager.NETHER_LOCATION, (Object) DimensionManager.DEFAULT_NETHER, Lifecycle.stable());
        iregistrywritable.a(DimensionManager.END_LOCATION, (Object) DimensionManager.DEFAULT_END, Lifecycle.stable());
        return iregistrycustom_dimension;
    }

    private static ChunkGenerator a(IRegistry<BiomeBase> iregistry, IRegistry<GeneratorSettingBase> iregistry1, long i) {
        return new ChunkGeneratorAbstract(new WorldChunkManagerTheEnd(iregistry, i), i, () -> {
            return (GeneratorSettingBase) iregistry1.d(GeneratorSettingBase.END);
        });
    }

    private static ChunkGenerator b(IRegistry<BiomeBase> iregistry, IRegistry<GeneratorSettingBase> iregistry1, long i) {
        return new ChunkGeneratorAbstract(WorldChunkManagerMultiNoise.b.NETHER.a(iregistry, i), i, () -> {
            return (GeneratorSettingBase) iregistry1.d(GeneratorSettingBase.NETHER);
        });
    }

    public static RegistryMaterials<WorldDimension> a(IRegistry<DimensionManager> iregistry, IRegistry<BiomeBase> iregistry1, IRegistry<GeneratorSettingBase> iregistry2, long i) {
        RegistryMaterials<WorldDimension> registrymaterials = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());

        registrymaterials.a(WorldDimension.NETHER, (Object) (new WorldDimension(() -> {
            return (DimensionManager) iregistry.d(DimensionManager.NETHER_LOCATION);
        }, b(iregistry1, iregistry2, i))), Lifecycle.stable());
        registrymaterials.a(WorldDimension.END, (Object) (new WorldDimension(() -> {
            return (DimensionManager) iregistry.d(DimensionManager.END_LOCATION);
        }, a(iregistry1, iregistry2, i))), Lifecycle.stable());
        return registrymaterials;
    }

    public static double a(DimensionManager dimensionmanager, DimensionManager dimensionmanager1) {
        double d0 = dimensionmanager.getCoordinateScale();
        double d1 = dimensionmanager1.getCoordinateScale();

        return d0 / d1;
    }

    @Deprecated
    public String getSuffix() {
        return this.a(DimensionManager.DEFAULT_END) ? "_end" : "";
    }

    public static File a(ResourceKey<World> resourcekey, File file) {
        if (resourcekey == World.OVERWORLD) {
            return file;
        } else if (resourcekey == World.END) {
            return new File(file, "DIM1");
        } else if (resourcekey == World.NETHER) {
            return new File(file, "DIM-1");
        } else {
            String s = resourcekey.a().getNamespace();

            return new File(file, "dimensions/" + s + "/" + resourcekey.a().getKey());
        }
    }

    public boolean hasSkyLight() {
        return this.hasSkylight;
    }

    public boolean hasCeiling() {
        return this.hasCeiling;
    }

    public boolean isNether() {
        return this.ultraWarm;
    }

    public boolean isNatural() {
        return this.natural;
    }

    public double getCoordinateScale() {
        return this.coordinateScale;
    }

    public boolean isPiglinSafe() {
        return this.piglinSafe;
    }

    public boolean isBedWorks() {
        return this.bedWorks;
    }

    public boolean isRespawnAnchorWorks() {
        return this.respawnAnchorWorks;
    }

    public boolean hasRaids() {
        return this.hasRaids;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLogicalHeight() {
        return this.logicalHeight;
    }

    public boolean isCreateDragonBattle() {
        return this.createDragonFight;
    }

    public GenLayerZoomer getGenLayerZoomer() {
        return this.biomeZoomer;
    }

    public boolean isFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float a(long i) {
        double d0 = MathHelper.g((double) this.fixedTime.orElse(i) / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;

        return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    public int b(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    public float a(int i) {
        return this.brightnessRamp[i];
    }

    public Tag<Block> q() {
        Tag<Block> tag = TagsBlock.a().a(this.infiniburn);

        return (Tag) (tag != null ? tag : TagsBlock.INFINIBURN_OVERWORLD);
    }

    public MinecraftKey r() {
        return this.effectsLocation;
    }

    public boolean a(DimensionManager dimensionmanager) {
        return this == dimensionmanager ? true : this.hasSkylight == dimensionmanager.hasSkylight && this.hasCeiling == dimensionmanager.hasCeiling && this.ultraWarm == dimensionmanager.ultraWarm && this.natural == dimensionmanager.natural && this.coordinateScale == dimensionmanager.coordinateScale && this.createDragonFight == dimensionmanager.createDragonFight && this.piglinSafe == dimensionmanager.piglinSafe && this.bedWorks == dimensionmanager.bedWorks && this.respawnAnchorWorks == dimensionmanager.respawnAnchorWorks && this.hasRaids == dimensionmanager.hasRaids && this.minY == dimensionmanager.minY && this.height == dimensionmanager.height && this.logicalHeight == dimensionmanager.logicalHeight && Float.compare(dimensionmanager.ambientLight, this.ambientLight) == 0 && this.fixedTime.equals(dimensionmanager.fixedTime) && this.biomeZoomer.equals(dimensionmanager.biomeZoomer) && this.infiniburn.equals(dimensionmanager.infiniburn) && this.effectsLocation.equals(dimensionmanager.effectsLocation);
    }
}
