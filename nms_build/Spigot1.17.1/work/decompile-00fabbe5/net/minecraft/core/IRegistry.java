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
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.Particles;
import net.minecraft.data.RegistryGeneration;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.DispenserRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.stats.StatisticWrapper;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
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
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
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
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IRegistry<T> implements Codec<T>, Keyable, Registry<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<MinecraftKey, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    public static final MinecraftKey ROOT_REGISTRY_NAME = new MinecraftKey("root");
    protected static final IRegistryWritable<IRegistryWritable<?>> WRITABLE_REGISTRY = new RegistryMaterials<>(a("root"), Lifecycle.experimental());
    public static final IRegistry<? extends IRegistry<?>> REGISTRY = IRegistry.WRITABLE_REGISTRY;
    public static final ResourceKey<IRegistry<SoundEffect>> SOUND_EVENT_REGISTRY = a("sound_event");
    public static final ResourceKey<IRegistry<FluidType>> FLUID_REGISTRY = a("fluid");
    public static final ResourceKey<IRegistry<MobEffectList>> MOB_EFFECT_REGISTRY = a("mob_effect");
    public static final ResourceKey<IRegistry<Block>> BLOCK_REGISTRY = a("block");
    public static final ResourceKey<IRegistry<Enchantment>> ENCHANTMENT_REGISTRY = a("enchantment");
    public static final ResourceKey<IRegistry<EntityTypes<?>>> ENTITY_TYPE_REGISTRY = a("entity_type");
    public static final ResourceKey<IRegistry<Item>> ITEM_REGISTRY = a("item");
    public static final ResourceKey<IRegistry<PotionRegistry>> POTION_REGISTRY = a("potion");
    public static final ResourceKey<IRegistry<Particle<?>>> PARTICLE_TYPE_REGISTRY = a("particle_type");
    public static final ResourceKey<IRegistry<TileEntityTypes<?>>> BLOCK_ENTITY_TYPE_REGISTRY = a("block_entity_type");
    public static final ResourceKey<IRegistry<Paintings>> MOTIVE_REGISTRY = a("motive");
    public static final ResourceKey<IRegistry<MinecraftKey>> CUSTOM_STAT_REGISTRY = a("custom_stat");
    public static final ResourceKey<IRegistry<ChunkStatus>> CHUNK_STATUS_REGISTRY = a("chunk_status");
    public static final ResourceKey<IRegistry<DefinedStructureRuleTestType<?>>> RULE_TEST_REGISTRY = a("rule_test");
    public static final ResourceKey<IRegistry<PosRuleTestType<?>>> POS_RULE_TEST_REGISTRY = a("pos_rule_test");
    public static final ResourceKey<IRegistry<Containers<?>>> MENU_REGISTRY = a("menu");
    public static final ResourceKey<IRegistry<Recipes<?>>> RECIPE_TYPE_REGISTRY = a("recipe_type");
    public static final ResourceKey<IRegistry<RecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRY = a("recipe_serializer");
    public static final ResourceKey<IRegistry<AttributeBase>> ATTRIBUTE_REGISTRY = a("attribute");
    public static final ResourceKey<IRegistry<GameEvent>> GAME_EVENT_REGISTRY = a("game_event");
    public static final ResourceKey<IRegistry<PositionSourceType<?>>> POSITION_SOURCE_TYPE_REGISTRY = a("position_source_type");
    public static final ResourceKey<IRegistry<StatisticWrapper<?>>> STAT_TYPE_REGISTRY = a("stat_type");
    public static final ResourceKey<IRegistry<VillagerType>> VILLAGER_TYPE_REGISTRY = a("villager_type");
    public static final ResourceKey<IRegistry<VillagerProfession>> VILLAGER_PROFESSION_REGISTRY = a("villager_profession");
    public static final ResourceKey<IRegistry<VillagePlaceType>> POINT_OF_INTEREST_TYPE_REGISTRY = a("point_of_interest_type");
    public static final ResourceKey<IRegistry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_REGISTRY = a("memory_module_type");
    public static final ResourceKey<IRegistry<SensorType<?>>> SENSOR_TYPE_REGISTRY = a("sensor_type");
    public static final ResourceKey<IRegistry<Schedule>> SCHEDULE_REGISTRY = a("schedule");
    public static final ResourceKey<IRegistry<Activity>> ACTIVITY_REGISTRY = a("activity");
    public static final ResourceKey<IRegistry<LootEntryType>> LOOT_ENTRY_REGISTRY = a("loot_pool_entry_type");
    public static final ResourceKey<IRegistry<LootItemFunctionType>> LOOT_FUNCTION_REGISTRY = a("loot_function_type");
    public static final ResourceKey<IRegistry<LootItemConditionType>> LOOT_ITEM_REGISTRY = a("loot_condition_type");
    public static final ResourceKey<IRegistry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_REGISTRY = a("loot_number_provider_type");
    public static final ResourceKey<IRegistry<LootNbtProviderType>> LOOT_NBT_PROVIDER_REGISTRY = a("loot_nbt_provider_type");
    public static final ResourceKey<IRegistry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_REGISTRY = a("loot_score_provider_type");
    public static final ResourceKey<IRegistry<DimensionManager>> DIMENSION_TYPE_REGISTRY = a("dimension_type");
    public static final ResourceKey<IRegistry<World>> DIMENSION_REGISTRY = a("dimension");
    public static final ResourceKey<IRegistry<WorldDimension>> LEVEL_STEM_REGISTRY = a("dimension");
    public static final RegistryBlocks<GameEvent> GAME_EVENT = a(IRegistry.GAME_EVENT_REGISTRY, "step", () -> {
        return GameEvent.STEP;
    });
    public static final IRegistry<SoundEffect> SOUND_EVENT = a(IRegistry.SOUND_EVENT_REGISTRY, () -> {
        return SoundEffects.ITEM_PICKUP;
    });
    public static final RegistryBlocks<FluidType> FLUID = a(IRegistry.FLUID_REGISTRY, "empty", () -> {
        return FluidTypes.EMPTY;
    });
    public static final IRegistry<MobEffectList> MOB_EFFECT = a(IRegistry.MOB_EFFECT_REGISTRY, () -> {
        return MobEffects.LUCK;
    });
    public static final RegistryBlocks<Block> BLOCK = a(IRegistry.BLOCK_REGISTRY, "air", () -> {
        return Blocks.AIR;
    });
    public static final IRegistry<Enchantment> ENCHANTMENT = a(IRegistry.ENCHANTMENT_REGISTRY, () -> {
        return Enchantments.BLOCK_FORTUNE;
    });
    public static final RegistryBlocks<EntityTypes<?>> ENTITY_TYPE = a(IRegistry.ENTITY_TYPE_REGISTRY, "pig", () -> {
        return EntityTypes.PIG;
    });
    public static final RegistryBlocks<Item> ITEM = a(IRegistry.ITEM_REGISTRY, "air", () -> {
        return Items.AIR;
    });
    public static final RegistryBlocks<PotionRegistry> POTION = a(IRegistry.POTION_REGISTRY, "empty", () -> {
        return Potions.EMPTY;
    });
    public static final IRegistry<Particle<?>> PARTICLE_TYPE = a(IRegistry.PARTICLE_TYPE_REGISTRY, () -> {
        return Particles.BLOCK;
    });
    public static final IRegistry<TileEntityTypes<?>> BLOCK_ENTITY_TYPE = a(IRegistry.BLOCK_ENTITY_TYPE_REGISTRY, () -> {
        return TileEntityTypes.FURNACE;
    });
    public static final RegistryBlocks<Paintings> MOTIVE = a(IRegistry.MOTIVE_REGISTRY, "kebab", () -> {
        return Paintings.KEBAB;
    });
    public static final IRegistry<MinecraftKey> CUSTOM_STAT = a(IRegistry.CUSTOM_STAT_REGISTRY, () -> {
        return StatisticList.JUMP;
    });
    public static final RegistryBlocks<ChunkStatus> CHUNK_STATUS = a(IRegistry.CHUNK_STATUS_REGISTRY, "empty", () -> {
        return ChunkStatus.EMPTY;
    });
    public static final IRegistry<DefinedStructureRuleTestType<?>> RULE_TEST = a(IRegistry.RULE_TEST_REGISTRY, () -> {
        return DefinedStructureRuleTestType.ALWAYS_TRUE_TEST;
    });
    public static final IRegistry<PosRuleTestType<?>> POS_RULE_TEST = a(IRegistry.POS_RULE_TEST_REGISTRY, () -> {
        return PosRuleTestType.ALWAYS_TRUE_TEST;
    });
    public static final IRegistry<Containers<?>> MENU = a(IRegistry.MENU_REGISTRY, () -> {
        return Containers.ANVIL;
    });
    public static final IRegistry<Recipes<?>> RECIPE_TYPE = a(IRegistry.RECIPE_TYPE_REGISTRY, () -> {
        return Recipes.CRAFTING;
    });
    public static final IRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = a(IRegistry.RECIPE_SERIALIZER_REGISTRY, () -> {
        return RecipeSerializer.SHAPELESS_RECIPE;
    });
    public static final IRegistry<AttributeBase> ATTRIBUTE = a(IRegistry.ATTRIBUTE_REGISTRY, () -> {
        return GenericAttributes.LUCK;
    });
    public static final IRegistry<PositionSourceType<?>> POSITION_SOURCE_TYPE = a(IRegistry.POSITION_SOURCE_TYPE_REGISTRY, () -> {
        return PositionSourceType.BLOCK;
    });
    public static final IRegistry<StatisticWrapper<?>> STAT_TYPE = a(IRegistry.STAT_TYPE_REGISTRY, () -> {
        return StatisticList.ITEM_USED;
    });
    public static final RegistryBlocks<VillagerType> VILLAGER_TYPE = a(IRegistry.VILLAGER_TYPE_REGISTRY, "plains", () -> {
        return VillagerType.PLAINS;
    });
    public static final RegistryBlocks<VillagerProfession> VILLAGER_PROFESSION = a(IRegistry.VILLAGER_PROFESSION_REGISTRY, "none", () -> {
        return VillagerProfession.NONE;
    });
    public static final RegistryBlocks<VillagePlaceType> POINT_OF_INTEREST_TYPE = a(IRegistry.POINT_OF_INTEREST_TYPE_REGISTRY, "unemployed", () -> {
        return VillagePlaceType.UNEMPLOYED;
    });
    public static final RegistryBlocks<MemoryModuleType<?>> MEMORY_MODULE_TYPE = a(IRegistry.MEMORY_MODULE_TYPE_REGISTRY, "dummy", () -> {
        return MemoryModuleType.DUMMY;
    });
    public static final RegistryBlocks<SensorType<?>> SENSOR_TYPE = a(IRegistry.SENSOR_TYPE_REGISTRY, "dummy", () -> {
        return SensorType.DUMMY;
    });
    public static final IRegistry<Schedule> SCHEDULE = a(IRegistry.SCHEDULE_REGISTRY, () -> {
        return Schedule.EMPTY;
    });
    public static final IRegistry<Activity> ACTIVITY = a(IRegistry.ACTIVITY_REGISTRY, () -> {
        return Activity.IDLE;
    });
    public static final IRegistry<LootEntryType> LOOT_POOL_ENTRY_TYPE = a(IRegistry.LOOT_ENTRY_REGISTRY, () -> {
        return LootEntries.EMPTY;
    });
    public static final IRegistry<LootItemFunctionType> LOOT_FUNCTION_TYPE = a(IRegistry.LOOT_FUNCTION_REGISTRY, () -> {
        return LootItemFunctions.SET_COUNT;
    });
    public static final IRegistry<LootItemConditionType> LOOT_CONDITION_TYPE = a(IRegistry.LOOT_ITEM_REGISTRY, () -> {
        return LootItemConditions.INVERTED;
    });
    public static final IRegistry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = a(IRegistry.LOOT_NUMBER_PROVIDER_REGISTRY, () -> {
        return NumberProviders.CONSTANT;
    });
    public static final IRegistry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = a(IRegistry.LOOT_NBT_PROVIDER_REGISTRY, () -> {
        return NbtProviders.CONTEXT;
    });
    public static final IRegistry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = a(IRegistry.LOOT_SCORE_PROVIDER_REGISTRY, () -> {
        return ScoreboardNameProviders.CONTEXT;
    });
    public static final ResourceKey<IRegistry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE_REGISTRY = a("float_provider_type");
    public static final IRegistry<FloatProviderType<?>> FLOAT_PROVIDER_TYPES = a(IRegistry.FLOAT_PROVIDER_TYPE_REGISTRY, () -> {
        return FloatProviderType.CONSTANT;
    });
    public static final ResourceKey<IRegistry<IntProviderType<?>>> INT_PROVIDER_TYPE_REGISTRY = a("int_provider_type");
    public static final IRegistry<IntProviderType<?>> INT_PROVIDER_TYPES = a(IRegistry.INT_PROVIDER_TYPE_REGISTRY, () -> {
        return IntProviderType.CONSTANT;
    });
    public static final ResourceKey<IRegistry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE_REGISTRY = a("height_provider_type");
    public static final IRegistry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPES = a(IRegistry.HEIGHT_PROVIDER_TYPE_REGISTRY, () -> {
        return HeightProviderType.CONSTANT;
    });
    public static final ResourceKey<IRegistry<GeneratorSettingBase>> NOISE_GENERATOR_SETTINGS_REGISTRY = a("worldgen/noise_settings");
    public static final ResourceKey<IRegistry<WorldGenSurfaceComposite<?>>> CONFIGURED_SURFACE_BUILDER_REGISTRY = a("worldgen/configured_surface_builder");
    public static final ResourceKey<IRegistry<WorldGenCarverWrapper<?>>> CONFIGURED_CARVER_REGISTRY = a("worldgen/configured_carver");
    public static final ResourceKey<IRegistry<WorldGenFeatureConfigured<?, ?>>> CONFIGURED_FEATURE_REGISTRY = a("worldgen/configured_feature");
    public static final ResourceKey<IRegistry<StructureFeature<?, ?>>> CONFIGURED_STRUCTURE_FEATURE_REGISTRY = a("worldgen/configured_structure_feature");
    public static final ResourceKey<IRegistry<ProcessorList>> PROCESSOR_LIST_REGISTRY = a("worldgen/processor_list");
    public static final ResourceKey<IRegistry<WorldGenFeatureDefinedStructurePoolTemplate>> TEMPLATE_POOL_REGISTRY = a("worldgen/template_pool");
    public static final ResourceKey<IRegistry<BiomeBase>> BIOME_REGISTRY = a("worldgen/biome");
    public static final ResourceKey<IRegistry<WorldGenSurface<?>>> SURFACE_BUILDER_REGISTRY = a("worldgen/surface_builder");
    public static final IRegistry<WorldGenSurface<?>> SURFACE_BUILDER = a(IRegistry.SURFACE_BUILDER_REGISTRY, () -> {
        return WorldGenSurface.DEFAULT;
    });
    public static final ResourceKey<IRegistry<WorldGenCarverAbstract<?>>> CARVER_REGISTRY = a("worldgen/carver");
    public static final IRegistry<WorldGenCarverAbstract<?>> CARVER = a(IRegistry.CARVER_REGISTRY, () -> {
        return WorldGenCarverAbstract.CAVE;
    });
    public static final ResourceKey<IRegistry<WorldGenerator<?>>> FEATURE_REGISTRY = a("worldgen/feature");
    public static final IRegistry<WorldGenerator<?>> FEATURE = a(IRegistry.FEATURE_REGISTRY, () -> {
        return WorldGenerator.ORE;
    });
    public static final ResourceKey<IRegistry<StructureGenerator<?>>> STRUCTURE_FEATURE_REGISTRY = a("worldgen/structure_feature");
    public static final IRegistry<StructureGenerator<?>> STRUCTURE_FEATURE = a(IRegistry.STRUCTURE_FEATURE_REGISTRY, () -> {
        return StructureGenerator.MINESHAFT;
    });
    public static final ResourceKey<IRegistry<WorldGenFeatureStructurePieceType>> STRUCTURE_PIECE_REGISTRY = a("worldgen/structure_piece");
    public static final IRegistry<WorldGenFeatureStructurePieceType> STRUCTURE_PIECE = a(IRegistry.STRUCTURE_PIECE_REGISTRY, () -> {
        return WorldGenFeatureStructurePieceType.MINE_SHAFT_ROOM;
    });
    public static final ResourceKey<IRegistry<WorldGenDecorator<?>>> DECORATOR_REGISTRY = a("worldgen/decorator");
    public static final IRegistry<WorldGenDecorator<?>> DECORATOR = a(IRegistry.DECORATOR_REGISTRY, () -> {
        return WorldGenDecorator.NOPE;
    });
    public static final ResourceKey<IRegistry<WorldGenFeatureStateProviders<?>>> BLOCK_STATE_PROVIDER_TYPE_REGISTRY = a("worldgen/block_state_provider_type");
    public static final ResourceKey<IRegistry<WorldGenBlockPlacers<?>>> BLOCK_PLACER_TYPE_REGISTRY = a("worldgen/block_placer_type");
    public static final ResourceKey<IRegistry<WorldGenFoilagePlacers<?>>> FOLIAGE_PLACER_TYPE_REGISTRY = a("worldgen/foliage_placer_type");
    public static final ResourceKey<IRegistry<TrunkPlacers<?>>> TRUNK_PLACER_TYPE_REGISTRY = a("worldgen/trunk_placer_type");
    public static final ResourceKey<IRegistry<WorldGenFeatureTrees<?>>> TREE_DECORATOR_TYPE_REGISTRY = a("worldgen/tree_decorator_type");
    public static final ResourceKey<IRegistry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_REGISTRY = a("worldgen/feature_size_type");
    public static final ResourceKey<IRegistry<Codec<? extends WorldChunkManager>>> BIOME_SOURCE_REGISTRY = a("worldgen/biome_source");
    public static final ResourceKey<IRegistry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_REGISTRY = a("worldgen/chunk_generator");
    public static final ResourceKey<IRegistry<DefinedStructureStructureProcessorType<?>>> STRUCTURE_PROCESSOR_REGISTRY = a("worldgen/structure_processor");
    public static final ResourceKey<IRegistry<WorldGenFeatureDefinedStructurePools<?>>> STRUCTURE_POOL_ELEMENT_REGISTRY = a("worldgen/structure_pool_element");
    public static final IRegistry<WorldGenFeatureStateProviders<?>> BLOCKSTATE_PROVIDER_TYPES = a(IRegistry.BLOCK_STATE_PROVIDER_TYPE_REGISTRY, () -> {
        return WorldGenFeatureStateProviders.SIMPLE_STATE_PROVIDER;
    });
    public static final IRegistry<WorldGenBlockPlacers<?>> BLOCK_PLACER_TYPES = a(IRegistry.BLOCK_PLACER_TYPE_REGISTRY, () -> {
        return WorldGenBlockPlacers.SIMPLE_BLOCK_PLACER;
    });
    public static final IRegistry<WorldGenFoilagePlacers<?>> FOLIAGE_PLACER_TYPES = a(IRegistry.FOLIAGE_PLACER_TYPE_REGISTRY, () -> {
        return WorldGenFoilagePlacers.BLOB_FOLIAGE_PLACER;
    });
    public static final IRegistry<TrunkPlacers<?>> TRUNK_PLACER_TYPES = a(IRegistry.TRUNK_PLACER_TYPE_REGISTRY, () -> {
        return TrunkPlacers.STRAIGHT_TRUNK_PLACER;
    });
    public static final IRegistry<WorldGenFeatureTrees<?>> TREE_DECORATOR_TYPES = a(IRegistry.TREE_DECORATOR_TYPE_REGISTRY, () -> {
        return WorldGenFeatureTrees.LEAVE_VINE;
    });
    public static final IRegistry<FeatureSizeType<?>> FEATURE_SIZE_TYPES = a(IRegistry.FEATURE_SIZE_TYPE_REGISTRY, () -> {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    });
    public static final IRegistry<Codec<? extends WorldChunkManager>> BIOME_SOURCE = a(IRegistry.BIOME_SOURCE_REGISTRY, Lifecycle.stable(), () -> {
        return WorldChunkManager.CODEC;
    });
    public static final IRegistry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = a(IRegistry.CHUNK_GENERATOR_REGISTRY, Lifecycle.stable(), () -> {
        return ChunkGenerator.CODEC;
    });
    public static final IRegistry<DefinedStructureStructureProcessorType<?>> STRUCTURE_PROCESSOR = a(IRegistry.STRUCTURE_PROCESSOR_REGISTRY, () -> {
        return DefinedStructureStructureProcessorType.BLOCK_IGNORE;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePools<?>> STRUCTURE_POOL_ELEMENT = a(IRegistry.STRUCTURE_POOL_ELEMENT_REGISTRY, () -> {
        return WorldGenFeatureDefinedStructurePools.EMPTY;
    });
    private final ResourceKey<? extends IRegistry<T>> key;
    private final Lifecycle lifecycle;

    private static <T> ResourceKey<IRegistry<T>> a(String s) {
        return ResourceKey.a(new MinecraftKey(s));
    }

    public static <T extends IRegistryWritable<?>> void a(IRegistryWritable<T> iregistrywritable) {
        iregistrywritable.forEach((iregistrywritable1) -> {
            if (iregistrywritable1.keySet().isEmpty()) {
                MinecraftKey minecraftkey = iregistrywritable.getKey(iregistrywritable1);

                SystemUtils.a("Registry '" + minecraftkey + "' was empty after loading");
            }

            if (iregistrywritable1 instanceof RegistryBlocks) {
                MinecraftKey minecraftkey1 = ((RegistryBlocks) iregistrywritable1).a();

                Validate.notNull(iregistrywritable1.get(minecraftkey1), "Missing default of DefaultedMappedRegistry: " + minecraftkey1, new Object[0]);
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

        IRegistry.LOADERS.put(minecraftkey, supplier);
        IRegistryWritable<R> iregistrywritable = IRegistry.WRITABLE_REGISTRY;

        return (IRegistryWritable) iregistrywritable.a(resourcekey, (Object) r0, lifecycle);
    }

    protected IRegistry(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        DispenserRegistry.a(() -> {
            return "registry " + resourcekey;
        });
        this.key = resourcekey;
        this.lifecycle = lifecycle;
    }

    public ResourceKey<? extends IRegistry<T>> f() {
        return this.key;
    }

    public String toString() {
        return "Registry[" + this.key + " (" + this.lifecycle + ")]";
    }

    public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> dynamicops, U u0) {
        return dynamicops.compressMaps() ? dynamicops.getNumberValue(u0).flatMap((number) -> {
            T t0 = this.fromId(number.intValue());

            return t0 == null ? DataResult.error("Unknown registry id: " + number) : DataResult.success(t0, this.d(t0));
        }).map((object) -> {
            return Pair.of(object, dynamicops.empty());
        }) : MinecraftKey.CODEC.decode(dynamicops, u0).flatMap((pair) -> {
            T t0 = this.get((MinecraftKey) pair.getFirst());

            return t0 == null ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(t0, pair.getSecond()), this.d(t0));
        });
    }

    public <U> DataResult<U> encode(T t0, DynamicOps<U> dynamicops, U u0) {
        MinecraftKey minecraftkey = this.getKey(t0);

        return minecraftkey == null ? DataResult.error("Unknown registry element " + t0) : (dynamicops.compressMaps() ? dynamicops.mergeToPrimitive(u0, dynamicops.createInt(this.getId(t0))).setLifecycle(this.lifecycle) : dynamicops.mergeToPrimitive(u0, dynamicops.createString(minecraftkey.toString())).setLifecycle(this.lifecycle));
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
    public abstract int getId(@Nullable T t0);

    @Nullable
    public abstract T a(@Nullable ResourceKey<T> resourcekey);

    @Nullable
    public abstract T get(@Nullable MinecraftKey minecraftkey);

    protected abstract Lifecycle d(T t0);

    public abstract Lifecycle b();

    public Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(this.get(minecraftkey));
    }

    public Optional<T> c(@Nullable ResourceKey<T> resourcekey) {
        return Optional.ofNullable(this.a(resourcekey));
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

    @Nullable
    public abstract T a(Random random);

    public Stream<T> g() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public abstract boolean c(MinecraftKey minecraftkey);

    public abstract boolean b(ResourceKey<T> resourcekey);

    public static <T> T a(IRegistry<? super T> iregistry, String s, T t0) {
        return a(iregistry, new MinecraftKey(s), t0);
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, MinecraftKey minecraftkey, T t0) {
        return ((IRegistryWritable) iregistry).a(ResourceKey.a(iregistry.key, minecraftkey), t0, Lifecycle.stable());
    }

    public static <V, T extends V> T a(IRegistry<V> iregistry, int i, String s, T t0) {
        return ((IRegistryWritable) iregistry).a(i, ResourceKey.a(iregistry.key, new MinecraftKey(s)), t0, Lifecycle.stable());
    }

    static {
        RegistryGeneration.a();
        IRegistry.LOADERS.forEach((minecraftkey, supplier) -> {
            if (supplier.get() == null) {
                IRegistry.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
        a(IRegistry.WRITABLE_REGISTRY);
    }
}
