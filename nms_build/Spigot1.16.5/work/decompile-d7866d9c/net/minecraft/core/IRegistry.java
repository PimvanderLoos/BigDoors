package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.Particles;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.decoration.Paintings;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.GeneratorSettingBase;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverAbstract;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.blockplacers.WorldGenBlockPlacers;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacers;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviders;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePools;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTrees;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacers;
import net.minecraft.world.level.levelgen.placement.WorldGenDecorator;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurface;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.storage.loot.entries.LootEntries;
import net.minecraft.world.level.storage.loot.entries.LootEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IRegistry<T> implements Codec<T>, Keyable, Registry<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<MinecraftKey, Supplier<?>> a = Maps.newLinkedHashMap();
    public static final MinecraftKey d = new MinecraftKey("root");
    protected static final IRegistryWritable<IRegistryWritable<?>> e = new RegistryMaterials<>(a("root"), Lifecycle.experimental());
    public static final IRegistry<? extends IRegistry<?>> f = IRegistry.e;
    public static final ResourceKey<IRegistry<SoundEffect>> g = a("sound_event");
    public static final ResourceKey<IRegistry<FluidType>> h = a("fluid");
    public static final ResourceKey<IRegistry<MobEffectList>> i = a("mob_effect");
    public static final ResourceKey<IRegistry<Block>> j = a("block");
    public static final ResourceKey<IRegistry<Enchantment>> k = a("enchantment");
    public static final ResourceKey<IRegistry<EntityTypes<?>>> l = a("entity_type");
    public static final ResourceKey<IRegistry<Item>> m = a("item");
    public static final ResourceKey<IRegistry<PotionRegistry>> n = a("potion");
    public static final ResourceKey<IRegistry<Particle<?>>> o = a("particle_type");
    public static final ResourceKey<IRegistry<TileEntityTypes<?>>> p = a("block_entity_type");
    public static final ResourceKey<IRegistry<Paintings>> q = a("motive");
    public static final ResourceKey<IRegistry<MinecraftKey>> r = a("custom_stat");
    public static final ResourceKey<IRegistry<ChunkStatus>> s = a("chunk_status");
    public static final ResourceKey<IRegistry<DefinedStructureRuleTestType<?>>> t = a("rule_test");
    public static final ResourceKey<IRegistry<PosRuleTestType<?>>> u = a("pos_rule_test");
    public static final ResourceKey<IRegistry<Containers<?>>> v = a("menu");
    public static final ResourceKey<IRegistry<Recipes<?>>> w = a("recipe_type");
    public static final ResourceKey<IRegistry<RecipeSerializer<?>>> x = a("recipe_serializer");
    public static final ResourceKey<IRegistry<AttributeBase>> y = a("attribute");
    public static final ResourceKey<IRegistry<StatisticWrapper<?>>> z = a("stat_type");
    public static final ResourceKey<IRegistry<VillagerType>> A = a("villager_type");
    public static final ResourceKey<IRegistry<VillagerProfession>> B = a("villager_profession");
    public static final ResourceKey<IRegistry<VillagePlaceType>> C = a("point_of_interest_type");
    public static final ResourceKey<IRegistry<MemoryModuleType<?>>> D = a("memory_module_type");
    public static final ResourceKey<IRegistry<SensorType<?>>> E = a("sensor_type");
    public static final ResourceKey<IRegistry<Schedule>> F = a("schedule");
    public static final ResourceKey<IRegistry<Activity>> G = a("activity");
    public static final ResourceKey<IRegistry<LootEntryType>> H = a("loot_pool_entry_type");
    public static final ResourceKey<IRegistry<LootItemFunctionType>> I = a("loot_function_type");
    public static final ResourceKey<IRegistry<LootItemConditionType>> J = a("loot_condition_type");
    public static final ResourceKey<IRegistry<DimensionManager>> K = a("dimension_type");
    public static final ResourceKey<IRegistry<World>> L = a("dimension");
    public static final ResourceKey<IRegistry<WorldDimension>> M = a("dimension");
    public static final IRegistry<SoundEffect> SOUND_EVENT = a(IRegistry.g, () -> {
        return SoundEffects.ENTITY_ITEM_PICKUP;
    });
    public static final RegistryBlocks<FluidType> FLUID = a(IRegistry.h, "empty", () -> {
        return FluidTypes.EMPTY;
    });
    public static final IRegistry<MobEffectList> MOB_EFFECT = a(IRegistry.i, () -> {
        return MobEffects.LUCK;
    });
    public static final RegistryBlocks<Block> BLOCK = a(IRegistry.j, "air", () -> {
        return Blocks.AIR;
    });
    public static final IRegistry<Enchantment> ENCHANTMENT = a(IRegistry.k, () -> {
        return Enchantments.LOOT_BONUS_BLOCKS;
    });
    public static final RegistryBlocks<EntityTypes<?>> ENTITY_TYPE = a(IRegistry.l, "pig", () -> {
        return EntityTypes.PIG;
    });
    public static final RegistryBlocks<Item> ITEM = a(IRegistry.m, "air", () -> {
        return Items.AIR;
    });
    public static final RegistryBlocks<PotionRegistry> POTION = a(IRegistry.n, "empty", () -> {
        return Potions.EMPTY;
    });
    public static final IRegistry<Particle<?>> PARTICLE_TYPE = a(IRegistry.o, () -> {
        return Particles.BLOCK;
    });
    public static final IRegistry<TileEntityTypes<?>> BLOCK_ENTITY_TYPE = a(IRegistry.p, () -> {
        return TileEntityTypes.FURNACE;
    });
    public static final RegistryBlocks<Paintings> MOTIVE = a(IRegistry.q, "kebab", () -> {
        return Paintings.a;
    });
    public static final IRegistry<MinecraftKey> CUSTOM_STAT = a(IRegistry.r, () -> {
        return StatisticList.JUMP;
    });
    public static final RegistryBlocks<ChunkStatus> CHUNK_STATUS = a(IRegistry.s, "empty", () -> {
        return ChunkStatus.EMPTY;
    });
    public static final IRegistry<DefinedStructureRuleTestType<?>> RULE_TEST = a(IRegistry.t, () -> {
        return DefinedStructureRuleTestType.a;
    });
    public static final IRegistry<PosRuleTestType<?>> POS_RULE_TEST = a(IRegistry.u, () -> {
        return PosRuleTestType.a;
    });
    public static final IRegistry<Containers<?>> MENU = a(IRegistry.v, () -> {
        return Containers.ANVIL;
    });
    public static final IRegistry<Recipes<?>> RECIPE_TYPE = a(IRegistry.w, () -> {
        return Recipes.CRAFTING;
    });
    public static final IRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = a(IRegistry.x, () -> {
        return RecipeSerializer.b;
    });
    public static final IRegistry<AttributeBase> ATTRIBUTE = a(IRegistry.y, () -> {
        return GenericAttributes.LUCK;
    });
    public static final IRegistry<StatisticWrapper<?>> STATS = a(IRegistry.z, () -> {
        return StatisticList.ITEM_USED;
    });
    public static final RegistryBlocks<VillagerType> VILLAGER_TYPE = a(IRegistry.A, "plains", () -> {
        return VillagerType.PLAINS;
    });
    public static final RegistryBlocks<VillagerProfession> VILLAGER_PROFESSION = a(IRegistry.B, "none", () -> {
        return VillagerProfession.NONE;
    });
    public static final RegistryBlocks<VillagePlaceType> POINT_OF_INTEREST_TYPE = a(IRegistry.C, "unemployed", () -> {
        return VillagePlaceType.c;
    });
    public static final RegistryBlocks<MemoryModuleType<?>> MEMORY_MODULE_TYPE = a(IRegistry.D, "dummy", () -> {
        return MemoryModuleType.DUMMY;
    });
    public static final RegistryBlocks<SensorType<?>> SENSOR_TYPE = a(IRegistry.E, "dummy", () -> {
        return SensorType.a;
    });
    public static final IRegistry<Schedule> SCHEDULE = a(IRegistry.F, () -> {
        return Schedule.EMPTY;
    });
    public static final IRegistry<Activity> ACTIVITY = a(IRegistry.G, () -> {
        return Activity.IDLE;
    });
    public static final IRegistry<LootEntryType> ao = a(IRegistry.H, () -> {
        return LootEntries.a;
    });
    public static final IRegistry<LootItemFunctionType> ap = a(IRegistry.I, () -> {
        return LootItemFunctions.b;
    });
    public static final IRegistry<LootItemConditionType> aq = a(IRegistry.J, () -> {
        return LootItemConditions.a;
    });
    public static final ResourceKey<IRegistry<GeneratorSettingBase>> ar = a("worldgen/noise_settings");
    public static final ResourceKey<IRegistry<WorldGenSurfaceComposite<?>>> as = a("worldgen/configured_surface_builder");
    public static final ResourceKey<IRegistry<WorldGenCarverWrapper<?>>> at = a("worldgen/configured_carver");
    public static final ResourceKey<IRegistry<WorldGenFeatureConfigured<?, ?>>> au = a("worldgen/configured_feature");
    public static final ResourceKey<IRegistry<StructureFeature<?, ?>>> av = a("worldgen/configured_structure_feature");
    public static final ResourceKey<IRegistry<ProcessorList>> aw = a("worldgen/processor_list");
    public static final ResourceKey<IRegistry<WorldGenFeatureDefinedStructurePoolTemplate>> ax = a("worldgen/template_pool");
    public static final ResourceKey<IRegistry<BiomeBase>> ay = a("worldgen/biome");
    public static final ResourceKey<IRegistry<WorldGenSurface<?>>> az = a("worldgen/surface_builder");
    public static final IRegistry<WorldGenSurface<?>> SURFACE_BUILDER = a(IRegistry.az, () -> {
        return WorldGenSurface.v;
    });
    public static final ResourceKey<IRegistry<WorldGenCarverAbstract<?>>> aB = a("worldgen/carver");
    public static final IRegistry<WorldGenCarverAbstract<?>> CARVER = a(IRegistry.aB, () -> {
        return WorldGenCarverAbstract.a;
    });
    public static final ResourceKey<IRegistry<WorldGenerator<?>>> aD = a("worldgen/feature");
    public static final IRegistry<WorldGenerator<?>> FEATURE = a(IRegistry.aD, () -> {
        return WorldGenerator.ORE;
    });
    public static final ResourceKey<IRegistry<StructureGenerator<?>>> aF = a("worldgen/structure_feature");
    public static final IRegistry<StructureGenerator<?>> STRUCTURE_FEATURE = a(IRegistry.aF, () -> {
        return StructureGenerator.MINESHAFT;
    });
    public static final ResourceKey<IRegistry<WorldGenFeatureStructurePieceType>> aH = a("worldgen/structure_piece");
    public static final IRegistry<WorldGenFeatureStructurePieceType> STRUCTURE_PIECE = a(IRegistry.aH, () -> {
        return WorldGenFeatureStructurePieceType.c;
    });
    public static final ResourceKey<IRegistry<WorldGenDecorator<?>>> aJ = a("worldgen/decorator");
    public static final IRegistry<WorldGenDecorator<?>> DECORATOR = a(IRegistry.aJ, () -> {
        return WorldGenDecorator.a;
    });
    public static final ResourceKey<IRegistry<WorldGenFeatureStateProviders<?>>> aL = a("worldgen/block_state_provider_type");
    public static final ResourceKey<IRegistry<WorldGenBlockPlacers<?>>> aM = a("worldgen/block_placer_type");
    public static final ResourceKey<IRegistry<WorldGenFoilagePlacers<?>>> aN = a("worldgen/foliage_placer_type");
    public static final ResourceKey<IRegistry<TrunkPlacers<?>>> aO = a("worldgen/trunk_placer_type");
    public static final ResourceKey<IRegistry<WorldGenFeatureTrees<?>>> aP = a("worldgen/tree_decorator_type");
    public static final ResourceKey<IRegistry<FeatureSizeType<?>>> aQ = a("worldgen/feature_size_type");
    public static final ResourceKey<IRegistry<Codec<? extends WorldChunkManager>>> aR = a("worldgen/biome_source");
    public static final ResourceKey<IRegistry<Codec<? extends ChunkGenerator>>> aS = a("worldgen/chunk_generator");
    public static final ResourceKey<IRegistry<DefinedStructureStructureProcessorType<?>>> aT = a("worldgen/structure_processor");
    public static final ResourceKey<IRegistry<WorldGenFeatureDefinedStructurePools<?>>> aU = a("worldgen/structure_pool_element");
    public static final IRegistry<WorldGenFeatureStateProviders<?>> BLOCK_STATE_PROVIDER_TYPE = a(IRegistry.aL, () -> {
        return WorldGenFeatureStateProviders.a;
    });
    public static final IRegistry<WorldGenBlockPlacers<?>> BLOCK_PLACER_TYPE = a(IRegistry.aM, () -> {
        return WorldGenBlockPlacers.a;
    });
    public static final IRegistry<WorldGenFoilagePlacers<?>> FOLIAGE_PLACER_TYPE = a(IRegistry.aN, () -> {
        return WorldGenFoilagePlacers.a;
    });
    public static final IRegistry<TrunkPlacers<?>> TRUNK_PLACER_TYPE = a(IRegistry.aO, () -> {
        return TrunkPlacers.a;
    });
    public static final IRegistry<WorldGenFeatureTrees<?>> TREE_DECORATOR_TYPE = a(IRegistry.aP, () -> {
        return WorldGenFeatureTrees.b;
    });
    public static final IRegistry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = a(IRegistry.aQ, () -> {
        return FeatureSizeType.a;
    });
    public static final IRegistry<Codec<? extends WorldChunkManager>> BIOME_SOURCE = a(IRegistry.aR, Lifecycle.stable(), () -> {
        return WorldChunkManager.a;
    });
    public static final IRegistry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = a(IRegistry.aS, Lifecycle.stable(), () -> {
        return ChunkGenerator.a;
    });
    public static final IRegistry<DefinedStructureStructureProcessorType<?>> STRUCTURE_PROCESSOR = a(IRegistry.aT, () -> {
        return DefinedStructureStructureProcessorType.a;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePools<?>> STRUCTURE_POOL_ELEMENT = a(IRegistry.aU, () -> {
        return WorldGenFeatureDefinedStructurePools.d;
    });
    private final ResourceKey<? extends IRegistry<T>> b;
    private final Lifecycle bf;

    private static <T> ResourceKey<IRegistry<T>> a(String s) {
        return ResourceKey.a(new MinecraftKey(s));
    }

    public static <T extends IRegistryWritable<?>> void a(IRegistryWritable<T> iregistrywritable) {
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

    private static <T> IRegistry<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, Supplier<T> supplier) {
        return a(resourcekey, Lifecycle.experimental(), supplier);
    }

    private static <T> RegistryBlocks<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, String s, Supplier<T> supplier) {
        return a(resourcekey, s, Lifecycle.experimental(), supplier);
    }

    private static <T> IRegistry<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Supplier<T> supplier) {
        return a(resourcekey, (IRegistryWritable) (new RegistryMaterials<>(resourcekey, lifecycle)), supplier, lifecycle);
    }

    private static <T> RegistryBlocks<T> a(ResourceKey<? extends IRegistry<T>> resourcekey, String s, Lifecycle lifecycle, Supplier<T> supplier) {
        return (RegistryBlocks) a(resourcekey, (IRegistryWritable) (new RegistryBlocks<>(s, resourcekey, lifecycle)), supplier, lifecycle);
    }

    private static <T, R extends IRegistryWritable<T>> R a(ResourceKey<? extends IRegistry<T>> resourcekey, R r0, Supplier<T> supplier, Lifecycle lifecycle) {
        MinecraftKey minecraftkey = resourcekey.a();

        IRegistry.a.put(minecraftkey, supplier);
        IRegistryWritable<R> iregistrywritable = IRegistry.e;

        return (IRegistryWritable) iregistrywritable.a(resourcekey, (Object) r0, lifecycle);
    }

    protected IRegistry(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        this.b = resourcekey;
        this.bf = lifecycle;
    }

    public ResourceKey<? extends IRegistry<T>> f() {
        return this.b;
    }

    public String toString() {
        return "Registry[" + this.b + " (" + this.bf + ")]";
    }

    public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> dynamicops, U u0) {
        return dynamicops.compressMaps() ? dynamicops.getNumberValue(u0).flatMap((number) -> {
            T t0 = this.fromId(number.intValue());

            return t0 == null ? DataResult.error("Unknown registry id: " + number) : DataResult.success(t0, this.d(t0));
        }).map((object) -> {
            return Pair.of(object, dynamicops.empty());
        }) : MinecraftKey.a.decode(dynamicops, u0).flatMap((pair) -> {
            T t0 = this.get((MinecraftKey) pair.getFirst());

            return t0 == null ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(t0, pair.getSecond()), this.d(t0));
        });
    }

    public <U> DataResult<U> encode(T t0, DynamicOps<U> dynamicops, U u0) {
        MinecraftKey minecraftkey = this.getKey(t0);

        return minecraftkey == null ? DataResult.error("Unknown registry element " + t0) : (dynamicops.compressMaps() ? dynamicops.mergeToPrimitive(u0, dynamicops.createInt(this.a(t0))).setLifecycle(this.bf) : dynamicops.mergeToPrimitive(u0, dynamicops.createString(minecraftkey.toString())).setLifecycle(this.bf));
    }

    public <U> Stream<U> keys(DynamicOps<U> dynamicops) {
        return this.keySet().stream().map((minecraftkey) -> {
            return dynamicops.createString(minecraftkey.toString());
        });
    }

    @Nullable
    public abstract MinecraftKey getKey(T t0);

    public abstract Optional<ResourceKey<T>> c(T t0);

    @Override
    public abstract int a(@Nullable T t0);

    @Nullable
    public abstract T a(@Nullable ResourceKey<T> resourcekey);

    @Nullable
    public abstract T get(@Nullable MinecraftKey minecraftkey);

    protected abstract Lifecycle d(T t0);

    public abstract Lifecycle b();

    public Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(this.get(minecraftkey));
    }

    public T d(ResourceKey<T> resourcekey) {
        T t0 = this.a(resourcekey);

        if (t0 == null) {
            throw new IllegalStateException("Missing: " + resourcekey);
        } else {
            return t0;
        }
    }

    public abstract Set<MinecraftKey> keySet();

    public abstract Set<Entry<ResourceKey<T>, T>> d();

    public Stream<T> g() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public static <T> T a(IRegistry<? super T> iregistry, String s, T t0) {
        return a(iregistry, new MinecraftKey(s), t0);
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, MinecraftKey minecraftkey, T t0) {
        return ((IRegistryWritable) iregistry).a(ResourceKey.a(iregistry.b, minecraftkey), t0, Lifecycle.stable());
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, int i, String s, T t0) {
        return ((IRegistryWritable) iregistry).a(i, ResourceKey.a(iregistry.b, new MinecraftKey(s)), t0, Lifecycle.stable());
    }

    static {
        RegistryGeneration.a();
        IRegistry.a.forEach((minecraftkey, supplier) -> {
            if (supplier.get() == null) {
                IRegistry.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
        a(IRegistry.e);
    }
}
