package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Supplier;

public class DimensionManager {

    public static final MapCodec<DimensionManager> a = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((optional) -> {
            return (OptionalLong) optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
        }, (optionallong) -> {
            return optionallong.isPresent() ? Optional.of(optionallong.getAsLong()) : Optional.empty();
        }).forGetter((dimensionmanager) -> {
            return dimensionmanager.fixedTime;
        }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionManager::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionManager::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionManager::isNether), Codec.BOOL.fieldOf("natural").forGetter(DimensionManager::isNatural), Codec.BOOL.fieldOf("shrunk").forGetter(DimensionManager::h), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionManager::isPiglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionManager::isBedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionManager::isRespawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionManager::hasRaids), Codecs.a(0, 256).fieldOf("logical_height").forGetter(DimensionManager::getLogicalHeight), MinecraftKey.a.fieldOf("infiniburn").forGetter((dimensionmanager) -> {
            return dimensionmanager.infiniburn;
        }), Codec.FLOAT.fieldOf("ambient_light").forGetter((dimensionmanager) -> {
            return dimensionmanager.ambientLight;
        })).apply(instance, DimensionManager::new);
    });
    public static final float[] b = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public static final ResourceKey<DimensionManager> OVERWORLD = ResourceKey.a(IRegistry.ad, new MinecraftKey("overworld"));
    public static final ResourceKey<DimensionManager> THE_NETHER = ResourceKey.a(IRegistry.ad, new MinecraftKey("the_nether"));
    public static final ResourceKey<DimensionManager> THE_END = ResourceKey.a(IRegistry.ad, new MinecraftKey("the_end"));
    protected static final DimensionManager OVERWORLD_IMPL = new DimensionManager(OptionalLong.empty(), true, false, false, true, false, false, false, true, false, true, 256, GenLayerZoomVoronoiFixed.INSTANCE, TagsBlock.aC.a(), 0.0F);
    protected static final DimensionManager THE_NETHER_IMPL = new DimensionManager(OptionalLong.of(18000L), false, true, true, false, true, false, true, false, true, false, 128, GenLayerZoomVoronoi.INSTANCE, TagsBlock.aD.a(), 0.1F);
    protected static final DimensionManager THE_END_IMPL = new DimensionManager(OptionalLong.of(6000L), false, false, false, false, false, true, false, false, false, true, 256, GenLayerZoomVoronoi.INSTANCE, TagsBlock.aE.a(), 0.0F);
    public static final ResourceKey<DimensionManager> i = ResourceKey.a(IRegistry.ad, new MinecraftKey("overworld_caves"));
    protected static final DimensionManager j = new DimensionManager(OptionalLong.empty(), true, true, false, true, false, false, false, true, false, true, 256, GenLayerZoomVoronoiFixed.INSTANCE, TagsBlock.aC.a(), 0.0F);
    public static final Codec<Supplier<DimensionManager>> k = RegistryFileCodec.a(IRegistry.ad, DimensionManager.a);
    private final OptionalLong fixedTime;
    private final boolean hasSkylight;
    private final boolean hasCeiling;
    private final boolean ultraWarm;
    private final boolean natural;
    private final boolean shrunk;
    private final boolean createDragonBattle;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int logicalHeight;
    private final GenLayerZoomer genLayerZoomer;
    private final MinecraftKey infiniburn;
    private final float ambientLight;
    private final transient float[] A;

    public static DimensionManager a() {
        return DimensionManager.OVERWORLD_IMPL;
    }

    protected DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8, int i, MinecraftKey minecraftkey, float f) {
        this(optionallong, flag, flag1, flag2, flag3, flag4, false, flag5, flag6, flag7, flag8, i, GenLayerZoomVoronoi.INSTANCE, minecraftkey, f);
    }

    protected DimensionManager(OptionalLong optionallong, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, boolean flag5, boolean flag6, boolean flag7, boolean flag8, boolean flag9, int i, GenLayerZoomer genlayerzoomer, MinecraftKey minecraftkey, float f) {
        this.fixedTime = optionallong;
        this.hasSkylight = flag;
        this.hasCeiling = flag1;
        this.ultraWarm = flag2;
        this.natural = flag3;
        this.shrunk = flag4;
        this.createDragonBattle = flag5;
        this.piglinSafe = flag6;
        this.bedWorks = flag7;
        this.respawnAnchorWorks = flag8;
        this.hasRaids = flag9;
        this.logicalHeight = i;
        this.genLayerZoomer = genlayerzoomer;
        this.infiniburn = minecraftkey;
        this.ambientLight = f;
        this.A = a(f);
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
        iregistrycustom_dimension.a(DimensionManager.OVERWORLD, DimensionManager.OVERWORLD_IMPL);
        iregistrycustom_dimension.a(DimensionManager.i, DimensionManager.j);
        iregistrycustom_dimension.a(DimensionManager.THE_NETHER, DimensionManager.THE_NETHER_IMPL);
        iregistrycustom_dimension.a(DimensionManager.THE_END, DimensionManager.THE_END_IMPL);
        return iregistrycustom_dimension;
    }

    private static ChunkGenerator d(long i) {
        return new ChunkGeneratorAbstract(new WorldChunkManagerTheEnd(i), i, GeneratorSettingBase.a.e.b());
    }

    private static ChunkGenerator e(long i) {
        return new ChunkGeneratorAbstract(WorldChunkManagerMultiNoise.a.b.a(i), i, GeneratorSettingBase.a.d.b());
    }

    public static RegistryMaterials<WorldDimension> a(long i) {
        RegistryMaterials<WorldDimension> registrymaterials = new RegistryMaterials<>(IRegistry.af, Lifecycle.experimental());

        registrymaterials.a(WorldDimension.THE_NETHER, (Object) (new WorldDimension(() -> {
            return DimensionManager.THE_NETHER_IMPL;
        }, e(i))));
        registrymaterials.a(WorldDimension.THE_END, (Object) (new WorldDimension(() -> {
            return DimensionManager.THE_END_IMPL;
        }, d(i))));
        registrymaterials.d(WorldDimension.THE_NETHER);
        registrymaterials.d(WorldDimension.THE_END);
        return registrymaterials;
    }

    @Deprecated
    public String getSuffix() {
        return this == DimensionManager.THE_END_IMPL ? "_end" : "";
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

    public boolean h() {
        return this.shrunk;
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

    public float b(long i) {
        double d0 = MathHelper.h((double) this.fixedTime.orElse(i) / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;

        return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    public int c(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    public float a(int i) {
        return this.A[i];
    }

    public Tag<Block> q() {
        Tag<Block> tag = TagsBlock.b().a(this.infiniburn);

        return (Tag) (tag != null ? tag : TagsBlock.aC);
    }
}
