package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;
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

    public static final MinecraftKey OVERWORLD_KEY = new MinecraftKey("overworld");
    public static final MinecraftKey THE_NETHER_KEY = new MinecraftKey("the_nether");
    public static final MinecraftKey THE_END_KEY = new MinecraftKey("the_end");
    public static final Codec<DimensionManager> d = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((optional) -> {
            return (OptionalLong) optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
        }, (optionallong) -> {
            return optionallong.isPresent() ? Optional.of(optionallong.getAsLong()) : Optional.empty();
        }).forGetter((dimensionmanager) -> {
            return dimensionmanager.fixedTime;
        }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionManager::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionManager::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionManager::isNether), Codec.BOOL.fieldOf("natural").forGetter(DimensionManager::isNatural), Codec.doubleRange(9.999999747378752E-6D, 3.0E7D).fieldOf("coordinate_scale").forGetter(DimensionManager::getCoordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionManager::isPiglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionManager::isBedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionManager::isRespawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionManager::hasRaids), Codec.intRange(0, 256).fieldOf("logical_height").forGetter(DimensionManager::getLogicalHeight), MinecraftKey.a.fieldOf("infiniburn").forGetter((dimensionmanager) -> {
            return dimensionmanager.infiniburn;
        }), MinecraftKey.a.fieldOf("effects").orElse(DimensionManager.OVERWORLD_KEY).forGetter((dimensionmanager) -> {
            return dimensionmanager.effects;
        }), Codec.FLOAT.fieldOf("ambient_light").forGetter((dimensionmanager) -> {
            return dimensionmanager.ambientLight;
        })).apply(instance, DimensionManager::new);
    });
    public static final float[] e = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public static final ResourceKey<DimensionManager> OVERWORLD = ResourceKey.a(IRegistry.K, new MinecraftKey("overworld"));
    public static final ResourceKey<DimensionManager> THE_NETHER = ResourceKey.a(IRegistry.K, new MinecraftKey("the_nether"));
    public static final ResourceKey<DimensionManager> THE_END = ResourceKey.a(IRegistry.K, new MinecraftKey("the_end"));
    protected static final DimensionManager OVERWORLD_IMPL = new DimensionManager(OptionalLong.empty(), true, false, false, true, 1.0D, false, false, true, false, true, 256, GenLayerZoomVoronoiFixed.INSTANCE, TagsBlock.aE.a(), DimensionManager.OVERWORLD_KEY, 0.0F);
    protected static final DimensionManager THE_NETHER_IMPL = new DimensionManager(OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, false, true, false, 128, GenLayerZoomVoronoi.INSTANCE, TagsBlock.aF.a(), DimensionManager.THE_NETHER_KEY, 0.1F);
    protected static final DimensionManager THE_END_IMPL = new DimensionManager(OptionalLong.of(6000L), false, false, false, false, 1.0D, true, false, false, false, true, 256, GenLayerZoomVoronoi.INSTANCE, TagsBlock.aG.a(), DimensionManager.THE_END_KEY, 0.0F);
    public static final ResourceKey<DimensionManager> l = ResourceKey.a(IRegistry.K, new MinecraftKey("overworld_caves"));
    protected static final DimensionManager m = new DimensionManager(OptionalLong.empty(), true, true, false, true, 1.0D, false, false, true, false, true, 256, GenLayerZoomVoronoiFixed.INSTANCE, TagsBlock.aE.a(), DimensionManager.OVERWORLD_KEY, 0.0F);
    public static final Codec<Supplier<DimensionManager>> n = RegistryFileCodec.a(IRegistry.K, DimensionManager.d);
    private final OptionalLong fixedTime;
    private final boolean hasSkylight;
    private final boolean hasCeiling;
    private final boolean ultraWarm;
    private final boolean natural;
    private final double coordinateScale;
    private final boolean createDragonBattle;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int logicalHeight;
    private final GenLayerZoomer genLayerZoomer;
    private final MinecraftKey infiniburn;
    private final MinecraftKey effects;
    private final float ambientLight;
    private final transient float[] E;

    protected DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, int i, MinecraftKey minecraftkey, MinecraftKey minecraftkey1, float f) {
        this(optionallong, flag, flag1, flag2, flag3, d0, false, flag4, flag5, flag6, flag7, i, GenLayerZoomVoronoi.INSTANCE, minecraftkey, minecraftkey1, f);
    }

    protected DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, double d0, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8, int i, GenLayerZoomer genlayerzoomer, MinecraftKey minecraftkey, MinecraftKey minecraftkey1, float f) {
        this.fixedTime = optionallong;
        this.hasSkylight = flag;
        this.hasCeiling = flag1;
        this.ultraWarm = flag2;
        this.natural = flag3;
        this.coordinateScale = d0;
        this.createDragonBattle = flag4;
        this.piglinSafe = flag5;
        this.bedWorks = flag6;
        this.respawnAnchorWorks = flag7;
        this.hasRaids = flag8;
        this.logicalHeight = i;
        this.genLayerZoomer = genlayerzoomer;
        this.infiniburn = minecraftkey;
        this.effects = minecraftkey1;
        this.ambientLight = f;
        this.E = a(f);
    }

    private static float[] a(float f) {
        float[] afloat = new float[16];

        for (int i = 0; i <= 15; ++i) {
            float f1 = (float) i / 15.0F;
            float f2 = f1 / (4.0F - 3.0F * f1);

            afloat[i] = MathHelper.g(f, f2, 1.0F);
        }

        return afloat;
    }

    @Deprecated
    public static DataResult<ResourceKey<World>> a(Dynamic<?> dynamic) {
        Optional<Number> optional = dynamic.asNumber().result();

        if (optional.isPresent()) {
            int i = ((Number) optional.get()).intValue();

            if (i == -1) {
                return DataResult.success(World.THE_NETHER);
            }

            if (i == 0) {
                return DataResult.success(World.OVERWORLD);
            }

            if (i == 1) {
                return DataResult.success(World.THE_END);
            }
        }

        return World.f.parse(dynamic);
    }

    public static IRegistryCustom.Dimension a(IRegistryCustom.Dimension iregistrycustom_dimension) {
        IRegistryWritable<DimensionManager> iregistrywritable = iregistrycustom_dimension.b(IRegistry.K);

        iregistrywritable.a(DimensionManager.OVERWORLD, (Object) DimensionManager.OVERWORLD_IMPL, Lifecycle.stable());
        iregistrywritable.a(DimensionManager.l, (Object) DimensionManager.m, Lifecycle.stable());
        iregistrywritable.a(DimensionManager.THE_NETHER, (Object) DimensionManager.THE_NETHER_IMPL, Lifecycle.stable());
        iregistrywritable.a(DimensionManager.THE_END, (Object) DimensionManager.THE_END_IMPL, Lifecycle.stable());
        return iregistrycustom_dimension;
    }

    private static ChunkGenerator a(IRegistry<BiomeBase> iregistry, IRegistry<GeneratorSettingBase> iregistry1, long i) {
        return new ChunkGeneratorAbstract(new WorldChunkManagerTheEnd(iregistry, i), i, () -> {
            return (GeneratorSettingBase) iregistry1.d(GeneratorSettingBase.f);
        });
    }

    private static ChunkGenerator b(IRegistry<BiomeBase> iregistry, IRegistry<GeneratorSettingBase> iregistry1, long i) {
        return new ChunkGeneratorAbstract(WorldChunkManagerMultiNoise.b.a.a(iregistry, i), i, () -> {
            return (GeneratorSettingBase) iregistry1.d(GeneratorSettingBase.e);
        });
    }

    public static RegistryMaterials<WorldDimension> a(IRegistry<DimensionManager> iregistry, IRegistry<BiomeBase> iregistry1, IRegistry<GeneratorSettingBase> iregistry2, long i) {
        RegistryMaterials<WorldDimension> registrymaterials = new RegistryMaterials<>(IRegistry.M, Lifecycle.experimental());

        registrymaterials.a(WorldDimension.THE_NETHER, (Object) (new WorldDimension(() -> {
            return (DimensionManager) iregistry.d(DimensionManager.THE_NETHER);
        }, b(iregistry1, iregistry2, i))), Lifecycle.stable());
        registrymaterials.a(WorldDimension.THE_END, (Object) (new WorldDimension(() -> {
            return (DimensionManager) iregistry.d(DimensionManager.THE_END);
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
        return this.a(DimensionManager.THE_END_IMPL) ? "_end" : "";
    }

    public static File a(ResourceKey<World> resourcekey, File file) {
        return resourcekey == World.OVERWORLD ? file : (resourcekey == World.THE_END ? new File(file, "DIM1") : (resourcekey == World.THE_NETHER ? new File(file, "DIM-1") : new File(file, "dimensions/" + resourcekey.a().getNamespace() + "/" + resourcekey.a().getKey())));
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

    public int getLogicalHeight() {
        return this.logicalHeight;
    }

    public boolean isCreateDragonBattle() {
        return this.createDragonBattle;
    }

    public GenLayerZoomer getGenLayerZoomer() {
        return this.genLayerZoomer;
    }

    public boolean isFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float a(long i) {
        double d0 = MathHelper.h((double) this.fixedTime.orElse(i) / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;

        return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    public int b(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    public float a(int i) {
        return this.E[i];
    }

    public Tag<Block> o() {
        Tag<Block> tag = TagsBlock.a().a(this.infiniburn);

        return (Tag) (tag != null ? tag : TagsBlock.aE);
    }

    public boolean a(DimensionManager dimensionmanager) {
        return this == dimensionmanager ? true : this.hasSkylight == dimensionmanager.hasSkylight && this.hasCeiling == dimensionmanager.hasCeiling && this.ultraWarm == dimensionmanager.ultraWarm && this.natural == dimensionmanager.natural && this.coordinateScale == dimensionmanager.coordinateScale && this.createDragonBattle == dimensionmanager.createDragonBattle && this.piglinSafe == dimensionmanager.piglinSafe && this.bedWorks == dimensionmanager.bedWorks && this.respawnAnchorWorks == dimensionmanager.respawnAnchorWorks && this.hasRaids == dimensionmanager.hasRaids && this.logicalHeight == dimensionmanager.logicalHeight && Float.compare(dimensionmanager.ambientLight, this.ambientLight) == 0 && this.fixedTime.equals(dimensionmanager.fixedTime) && this.genLayerZoomer.equals(dimensionmanager.genLayerZoomer) && this.infiniburn.equals(dimensionmanager.infiniburn) && this.effects.equals(dimensionmanager.effects);
    }
}
