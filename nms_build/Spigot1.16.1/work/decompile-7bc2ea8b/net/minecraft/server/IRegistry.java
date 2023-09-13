package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IRegistry<T> implements Codec<T>, Keyable, Registry<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<MinecraftKey, Supplier<?>> a = Maps.newLinkedHashMap();
    public static final MinecraftKey f = new MinecraftKey("root");
    protected static final IRegistryWritable<IRegistryWritable<?>> g = new RegistryMaterials<>(a("root"), Lifecycle.experimental());
    public static final IRegistry<? extends IRegistry<?>> h = IRegistry.g;
    public static final ResourceKey<IRegistry<SoundEffect>> i = a("sound_event");
    public static final ResourceKey<IRegistry<FluidType>> j = a("fluid");
    public static final ResourceKey<IRegistry<MobEffectList>> k = a("mob_effect");
    public static final ResourceKey<IRegistry<Block>> l = a("block");
    public static final ResourceKey<IRegistry<Enchantment>> m = a("enchantment");
    public static final ResourceKey<IRegistry<EntityTypes<?>>> n = a("entity_type");
    public static final ResourceKey<IRegistry<Item>> o = a("item");
    public static final ResourceKey<IRegistry<PotionRegistry>> p = a("potion");
    public static final ResourceKey<IRegistry<WorldGenCarverAbstract<?>>> q = a("carver");
    public static final ResourceKey<IRegistry<WorldGenSurface<?>>> r = a("surface_builder");
    public static final ResourceKey<IRegistry<WorldGenerator<?>>> s = a("feature");
    public static final ResourceKey<IRegistry<WorldGenDecorator<?>>> t = a("decorator");
    public static final ResourceKey<IRegistry<BiomeBase>> u = a("biome");
    public static final ResourceKey<IRegistry<WorldGenFeatureStateProviders<?>>> v = a("block_state_provider_type");
    public static final ResourceKey<IRegistry<WorldGenBlockPlacers<?>>> w = a("block_placer_type");
    public static final ResourceKey<IRegistry<WorldGenFoilagePlacers<?>>> x = a("foliage_placer_type");
    public static final ResourceKey<IRegistry<TrunkPlacers<?>>> y = a("trunk_placer_type");
    public static final ResourceKey<IRegistry<WorldGenFeatureTrees<?>>> z = a("tree_decorator_type");
    public static final ResourceKey<IRegistry<FeatureSizeType<?>>> A = a("feature_size_type");
    public static final ResourceKey<IRegistry<Particle<?>>> B = a("particle_type");
    public static final ResourceKey<IRegistry<Codec<? extends WorldChunkManager>>> C = a("biome_source");
    public static final ResourceKey<IRegistry<Codec<? extends ChunkGenerator>>> D = a("chunk_generator");
    public static final ResourceKey<IRegistry<TileEntityTypes<?>>> E = a("block_entity_type");
    public static final ResourceKey<IRegistry<Paintings>> F = a("motive");
    public static final ResourceKey<IRegistry<MinecraftKey>> G = a("custom_stat");
    public static final ResourceKey<IRegistry<ChunkStatus>> H = a("chunk_status");
    public static final ResourceKey<IRegistry<StructureGenerator<?>>> I = a("structure_feature");
    public static final ResourceKey<IRegistry<WorldGenFeatureStructurePieceType>> J = a("structure_piece");
    public static final ResourceKey<IRegistry<DefinedStructureRuleTestType<?>>> K = a("rule_test");
    public static final ResourceKey<IRegistry<PosRuleTestType<?>>> L = a("pos_rule_test");
    public static final ResourceKey<IRegistry<DefinedStructureStructureProcessorType<?>>> M = a("structure_processor");
    public static final ResourceKey<IRegistry<WorldGenFeatureDefinedStructurePools<?>>> N = a("structure_pool_element");
    public static final ResourceKey<IRegistry<Containers<?>>> O = a("menu");
    public static final ResourceKey<IRegistry<Recipes<?>>> P = a("recipe_type");
    public static final ResourceKey<IRegistry<RecipeSerializer<?>>> Q = a("recipe_serializer");
    public static final ResourceKey<IRegistry<AttributeBase>> R = a("attribute");
    public static final ResourceKey<IRegistry<StatisticWrapper<?>>> S = a("stat_type");
    public static final ResourceKey<IRegistry<VillagerType>> T = a("villager_type");
    public static final ResourceKey<IRegistry<VillagerProfession>> U = a("villager_profession");
    public static final ResourceKey<IRegistry<VillagePlaceType>> V = a("point_of_interest_type");
    public static final ResourceKey<IRegistry<MemoryModuleType<?>>> W = a("memory_module_type");
    public static final ResourceKey<IRegistry<SensorType<?>>> X = a("sensor_type");
    public static final ResourceKey<IRegistry<Schedule>> Y = a("schedule");
    public static final ResourceKey<IRegistry<Activity>> Z = a("activity");
    public static final ResourceKey<IRegistry<LootEntryType>> aa = a("loot_pool_entry_type");
    public static final ResourceKey<IRegistry<LootItemFunctionType>> ab = a("loot_function_type");
    public static final ResourceKey<IRegistry<LootItemConditionType>> ac = a("loot_condition_type");
    public static final ResourceKey<IRegistry<DimensionManager>> ad = a("dimension_type");
    public static final ResourceKey<IRegistry<World>> ae = a("dimension");
    public static final ResourceKey<IRegistry<WorldDimension>> af = a("dimension");
    public static final IRegistry<SoundEffect> SOUND_EVENT = a(IRegistry.i, () -> {
        return SoundEffects.ENTITY_ITEM_PICKUP;
    });
    public static final RegistryBlocks<FluidType> FLUID = a(IRegistry.j, "empty", () -> {
        return FluidTypes.EMPTY;
    });
    public static final IRegistry<MobEffectList> MOB_EFFECT = a(IRegistry.k, () -> {
        return MobEffects.LUCK;
    });
    public static final RegistryBlocks<Block> BLOCK = a(IRegistry.l, "air", () -> {
        return Blocks.AIR;
    });
    public static final IRegistry<Enchantment> ENCHANTMENT = a(IRegistry.m, () -> {
        return Enchantments.LOOT_BONUS_BLOCKS;
    });
    public static final RegistryBlocks<EntityTypes<?>> ENTITY_TYPE = a(IRegistry.n, "pig", () -> {
        return EntityTypes.PIG;
    });
    public static final RegistryBlocks<Item> ITEM = a(IRegistry.o, "air", () -> {
        return Items.AIR;
    });
    public static final RegistryBlocks<PotionRegistry> POTION = a(IRegistry.p, "empty", () -> {
        return Potions.EMPTY;
    });
    public static final IRegistry<WorldGenCarverAbstract<?>> CARVER = a(IRegistry.q, () -> {
        return WorldGenCarverAbstract.a;
    });
    public static final IRegistry<WorldGenSurface<?>> SURFACE_BUILDER = a(IRegistry.r, () -> {
        return WorldGenSurface.S;
    });
    public static final IRegistry<WorldGenerator<?>> FEATURE = a(IRegistry.s, () -> {
        return WorldGenerator.ORE;
    });
    public static final IRegistry<WorldGenDecorator<?>> DECORATOR = a(IRegistry.t, () -> {
        return WorldGenDecorator.a;
    });
    public static final IRegistry<BiomeBase> BIOME = a(IRegistry.u, () -> {
        return Biomes.b;
    });
    public static final IRegistry<WorldGenFeatureStateProviders<?>> BLOCK_STATE_PROVIDER_TYPE = a(IRegistry.v, () -> {
        return WorldGenFeatureStateProviders.a;
    });
    public static final IRegistry<WorldGenBlockPlacers<?>> BLOCK_PLACER_TYPE = a(IRegistry.w, () -> {
        return WorldGenBlockPlacers.a;
    });
    public static final IRegistry<WorldGenFoilagePlacers<?>> FOLIAGE_PLACER_TYPE = a(IRegistry.x, () -> {
        return WorldGenFoilagePlacers.a;
    });
    public static final IRegistry<TrunkPlacers<?>> TRUNK_PLACER_TYPE = a(IRegistry.y, () -> {
        return TrunkPlacers.a;
    });
    public static final IRegistry<WorldGenFeatureTrees<?>> TREE_DECORATOR_TYPE = a(IRegistry.z, () -> {
        return WorldGenFeatureTrees.b;
    });
    public static final IRegistry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = a(IRegistry.A, () -> {
        return FeatureSizeType.a;
    });
    public static final IRegistry<Particle<?>> PARTICLE_TYPE = a(IRegistry.B, () -> {
        return Particles.BLOCK;
    });
    public static final IRegistry<Codec<? extends WorldChunkManager>> BIOME_SOURCE = a(IRegistry.C, Lifecycle.stable(), () -> {
        return WorldChunkManager.a;
    });
    public static final IRegistry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = a(IRegistry.D, Lifecycle.stable(), () -> {
        return ChunkGenerator.a;
    });
    public static final IRegistry<TileEntityTypes<?>> BLOCK_ENTITY_TYPE = a(IRegistry.E, () -> {
        return TileEntityTypes.FURNACE;
    });
    public static final RegistryBlocks<Paintings> MOTIVE = a(IRegistry.F, "kebab", () -> {
        return Paintings.a;
    });
    public static final IRegistry<MinecraftKey> CUSTOM_STAT = a(IRegistry.G, () -> {
        return StatisticList.JUMP;
    });
    public static final RegistryBlocks<ChunkStatus> CHUNK_STATUS = a(IRegistry.H, "empty", () -> {
        return ChunkStatus.EMPTY;
    });
    public static final IRegistry<StructureGenerator<?>> STRUCTURE_FEATURE = a(IRegistry.I, () -> {
        return StructureGenerator.MINESHAFT;
    });
    public static final IRegistry<WorldGenFeatureStructurePieceType> STRUCTURE_PIECE = a(IRegistry.J, () -> {
        return WorldGenFeatureStructurePieceType.c;
    });
    public static final IRegistry<DefinedStructureRuleTestType<?>> RULE_TEST = a(IRegistry.K, () -> {
        return DefinedStructureRuleTestType.a;
    });
    public static final IRegistry<PosRuleTestType<?>> POS_RULE_TEST = a(IRegistry.L, () -> {
        return PosRuleTestType.a;
    });
    public static final IRegistry<DefinedStructureStructureProcessorType<?>> STRUCTURE_PROCESSOR = a(IRegistry.M, () -> {
        return DefinedStructureStructureProcessorType.a;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePools<?>> STRUCTURE_POOL_ELEMENT = a(IRegistry.N, () -> {
        return WorldGenFeatureDefinedStructurePools.d;
    });
    public static final IRegistry<Containers<?>> MENU = a(IRegistry.O, () -> {
        return Containers.ANVIL;
    });
    public static final IRegistry<Recipes<?>> RECIPE_TYPE = a(IRegistry.P, () -> {
        return Recipes.CRAFTING;
    });
    public static final IRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = a(IRegistry.Q, () -> {
        return RecipeSerializer.b;
    });
    public static final IRegistry<AttributeBase> ATTRIBUTE = a(IRegistry.R, () -> {
        return GenericAttributes.LUCK;
    });
    public static final IRegistry<StatisticWrapper<?>> STATS = a(IRegistry.S, () -> {
        return StatisticList.ITEM_USED;
    });
    public static final RegistryBlocks<VillagerType> VILLAGER_TYPE = a(IRegistry.T, "plains", () -> {
        return VillagerType.PLAINS;
    });
    public static final RegistryBlocks<VillagerProfession> VILLAGER_PROFESSION = a(IRegistry.U, "none", () -> {
        return VillagerProfession.NONE;
    });
    public static final RegistryBlocks<VillagePlaceType> POINT_OF_INTEREST_TYPE = a(IRegistry.V, "unemployed", () -> {
        return VillagePlaceType.c;
    });
    public static final RegistryBlocks<MemoryModuleType<?>> MEMORY_MODULE_TYPE = a(IRegistry.W, "dummy", () -> {
        return MemoryModuleType.DUMMY;
    });
    public static final RegistryBlocks<SensorType<?>> SENSOR_TYPE = a(IRegistry.X, "dummy", () -> {
        return SensorType.a;
    });
    public static final IRegistry<Schedule> SCHEDULE = a(IRegistry.Y, () -> {
        return Schedule.EMPTY;
    });
    public static final IRegistry<Activity> ACTIVITY = a(IRegistry.Z, () -> {
        return Activity.IDLE;
    });
    public static final IRegistry<LootEntryType> aY = a(IRegistry.aa, () -> {
        return LootEntries.a;
    });
    public static final IRegistry<LootItemFunctionType> aZ = a(IRegistry.ab, () -> {
        return LootItemFunctions.b;
    });
    public static final IRegistry<LootItemConditionType> ba = a(IRegistry.ac, () -> {
        return LootItemConditions.a;
    });
    private final ResourceKey<IRegistry<T>> b;
    private final Lifecycle c;

    private static <T> ResourceKey<IRegistry<T>> a(String s) {
        return ResourceKey.a(new MinecraftKey(s));
    }

    private static <T extends IRegistryWritable<?>> void a(IRegistryWritable<T> iregistrywritable) {
        iregistrywritable.forEach((iregistrywritable1) -> {
            if (iregistrywritable1.keySet().isEmpty()) {
                IRegistry.LOGGER.error("Registry '{}' was empty after loading", iregistrywritable.getKey(iregistrywritable1));
                if (SharedConstants.d) {
                    throw new IllegalStateException("Registry: '" + iregistrywritable.getKey(iregistrywritable1) + "' is empty, not allowed, fix me!");
                }
            }

            if (iregistrywritable1 instanceof RegistryBlocks) {
                MinecraftKey minecraftkey = ((RegistryBlocks) iregistrywritable1).a();

                Validate.notNull(iregistrywritable1.get(minecraftkey), "Missing default of DefaultedMappedRegistry: " + minecraftkey, new Object[0]);
            }

        });
    }

    private static <T> IRegistry<T> a(ResourceKey<IRegistry<T>> resourcekey, Supplier<T> supplier) {
        return a(resourcekey, Lifecycle.experimental(), supplier);
    }

    private static <T> RegistryBlocks<T> a(ResourceKey<IRegistry<T>> resourcekey, String s, Supplier<T> supplier) {
        return a(resourcekey, s, Lifecycle.experimental(), supplier);
    }

    private static <T> IRegistry<T> a(ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle, Supplier<T> supplier) {
        return a(resourcekey, (IRegistryWritable) (new RegistryMaterials<>(resourcekey, lifecycle)), supplier);
    }

    private static <T> RegistryBlocks<T> a(ResourceKey<IRegistry<T>> resourcekey, String s, Lifecycle lifecycle, Supplier<T> supplier) {
        return (RegistryBlocks) a(resourcekey, (IRegistryWritable) (new RegistryBlocks<>(s, resourcekey, lifecycle)), supplier);
    }

    private static <T, R extends IRegistryWritable<T>> R a(ResourceKey<IRegistry<T>> resourcekey, R r0, Supplier<T> supplier) {
        MinecraftKey minecraftkey = resourcekey.a();

        IRegistry.a.put(minecraftkey, supplier);
        IRegistryWritable<R> iregistrywritable = IRegistry.g;

        return (IRegistryWritable) iregistrywritable.a(resourcekey, (Object) r0);
    }

    protected IRegistry(ResourceKey<IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        this.b = resourcekey;
        this.c = lifecycle;
    }

    public String toString() {
        return "Registry[" + this.b + " (" + this.c + ")]";
    }

    public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> dynamicops, U u0) {
        return dynamicops.compressMaps() ? dynamicops.getNumberValue(u0).flatMap((number) -> {
            int i = number.intValue();

            if (!this.b(i)) {
                return DataResult.error("Unknown registry id: " + number);
            } else {
                T t0 = this.fromId(i);

                return DataResult.success(t0, this.c);
            }
        }).map((object) -> {
            return Pair.of(object, dynamicops.empty());
        }) : MinecraftKey.a.decode(dynamicops, u0).addLifecycle(this.c).flatMap((pair) -> {
            return !this.c((MinecraftKey) pair.getFirst()) ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(pair.mapFirst(this::get), this.c);
        });
    }

    public <U> DataResult<U> encode(T t0, DynamicOps<U> dynamicops, U u0) {
        MinecraftKey minecraftkey = this.getKey(t0);

        return minecraftkey == null ? DataResult.error("Unknown registry element " + t0) : (dynamicops.compressMaps() ? dynamicops.mergeToPrimitive(u0, dynamicops.createInt(this.a(t0))).setLifecycle(this.c) : dynamicops.mergeToPrimitive(u0, dynamicops.createString(minecraftkey.toString())).setLifecycle(this.c));
    }

    public <U> Stream<U> keys(DynamicOps<U> dynamicops) {
        return this.keySet().stream().map((minecraftkey) -> {
            return dynamicops.createString(minecraftkey.toString());
        });
    }

    @Nullable
    public abstract MinecraftKey getKey(T t0);

    public abstract Optional<ResourceKey<T>> c(T t0);

    public abstract int a(@Nullable T t0);

    @Nullable
    public abstract T a(@Nullable ResourceKey<T> resourcekey);

    @Nullable
    public abstract T get(@Nullable MinecraftKey minecraftkey);

    public abstract Optional<T> getOptional(@Nullable MinecraftKey minecraftkey);

    public abstract Set<MinecraftKey> keySet();

    public Stream<T> e() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public abstract boolean c(MinecraftKey minecraftkey);

    public abstract boolean c(ResourceKey<T> resourcekey);

    public abstract boolean b(int i);

    public static <T> T a(IRegistry<? super T> iregistry, String s, T t0) {
        return a(iregistry, new MinecraftKey(s), t0);
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, MinecraftKey minecraftkey, T t0) {
        return ((IRegistryWritable) iregistry).a(ResourceKey.a(iregistry.b, minecraftkey), t0);
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, int i, String s, T t0) {
        return ((IRegistryWritable) iregistry).a(i, ResourceKey.a(iregistry.b, new MinecraftKey(s)), t0);
    }

    static {
        IRegistry.a.forEach((minecraftkey, supplier) -> {
            if (supplier.get() == null) {
                IRegistry.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
        a(IRegistry.g);
    }
}
