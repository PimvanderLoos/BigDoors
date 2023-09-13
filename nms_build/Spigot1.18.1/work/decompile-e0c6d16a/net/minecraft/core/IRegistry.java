package net.minecraft.core;

import com.google.common.collect.Maps;
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
import net.minecraft.util.ExtraCodecs;
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
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverAbstract;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverWrapper;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacers;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviders;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePools;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTrees;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacers;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorList;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
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

public abstract class IRegistry<T> implements Keyable, Registry<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<MinecraftKey, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    public static final MinecraftKey ROOT_REGISTRY_NAME = new MinecraftKey("root");
    protected static final IRegistryWritable<IRegistryWritable<?>> WRITABLE_REGISTRY = new RegistryMaterials<>(createRegistryKey("root"), Lifecycle.experimental());
    public static final IRegistry<? extends IRegistry<?>> REGISTRY = IRegistry.WRITABLE_REGISTRY;
    public static final ResourceKey<IRegistry<SoundEffect>> SOUND_EVENT_REGISTRY = createRegistryKey("sound_event");
    public static final ResourceKey<IRegistry<FluidType>> FLUID_REGISTRY = createRegistryKey("fluid");
    public static final ResourceKey<IRegistry<MobEffectList>> MOB_EFFECT_REGISTRY = createRegistryKey("mob_effect");
    public static final ResourceKey<IRegistry<Block>> BLOCK_REGISTRY = createRegistryKey("block");
    public static final ResourceKey<IRegistry<Enchantment>> ENCHANTMENT_REGISTRY = createRegistryKey("enchantment");
    public static final ResourceKey<IRegistry<EntityTypes<?>>> ENTITY_TYPE_REGISTRY = createRegistryKey("entity_type");
    public static final ResourceKey<IRegistry<Item>> ITEM_REGISTRY = createRegistryKey("item");
    public static final ResourceKey<IRegistry<PotionRegistry>> POTION_REGISTRY = createRegistryKey("potion");
    public static final ResourceKey<IRegistry<Particle<?>>> PARTICLE_TYPE_REGISTRY = createRegistryKey("particle_type");
    public static final ResourceKey<IRegistry<TileEntityTypes<?>>> BLOCK_ENTITY_TYPE_REGISTRY = createRegistryKey("block_entity_type");
    public static final ResourceKey<IRegistry<Paintings>> MOTIVE_REGISTRY = createRegistryKey("motive");
    public static final ResourceKey<IRegistry<MinecraftKey>> CUSTOM_STAT_REGISTRY = createRegistryKey("custom_stat");
    public static final ResourceKey<IRegistry<ChunkStatus>> CHUNK_STATUS_REGISTRY = createRegistryKey("chunk_status");
    public static final ResourceKey<IRegistry<DefinedStructureRuleTestType<?>>> RULE_TEST_REGISTRY = createRegistryKey("rule_test");
    public static final ResourceKey<IRegistry<PosRuleTestType<?>>> POS_RULE_TEST_REGISTRY = createRegistryKey("pos_rule_test");
    public static final ResourceKey<IRegistry<Containers<?>>> MENU_REGISTRY = createRegistryKey("menu");
    public static final ResourceKey<IRegistry<Recipes<?>>> RECIPE_TYPE_REGISTRY = createRegistryKey("recipe_type");
    public static final ResourceKey<IRegistry<RecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRY = createRegistryKey("recipe_serializer");
    public static final ResourceKey<IRegistry<AttributeBase>> ATTRIBUTE_REGISTRY = createRegistryKey("attribute");
    public static final ResourceKey<IRegistry<GameEvent>> GAME_EVENT_REGISTRY = createRegistryKey("game_event");
    public static final ResourceKey<IRegistry<PositionSourceType<?>>> POSITION_SOURCE_TYPE_REGISTRY = createRegistryKey("position_source_type");
    public static final ResourceKey<IRegistry<StatisticWrapper<?>>> STAT_TYPE_REGISTRY = createRegistryKey("stat_type");
    public static final ResourceKey<IRegistry<VillagerType>> VILLAGER_TYPE_REGISTRY = createRegistryKey("villager_type");
    public static final ResourceKey<IRegistry<VillagerProfession>> VILLAGER_PROFESSION_REGISTRY = createRegistryKey("villager_profession");
    public static final ResourceKey<IRegistry<VillagePlaceType>> POINT_OF_INTEREST_TYPE_REGISTRY = createRegistryKey("point_of_interest_type");
    public static final ResourceKey<IRegistry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_REGISTRY = createRegistryKey("memory_module_type");
    public static final ResourceKey<IRegistry<SensorType<?>>> SENSOR_TYPE_REGISTRY = createRegistryKey("sensor_type");
    public static final ResourceKey<IRegistry<Schedule>> SCHEDULE_REGISTRY = createRegistryKey("schedule");
    public static final ResourceKey<IRegistry<Activity>> ACTIVITY_REGISTRY = createRegistryKey("activity");
    public static final ResourceKey<IRegistry<LootEntryType>> LOOT_ENTRY_REGISTRY = createRegistryKey("loot_pool_entry_type");
    public static final ResourceKey<IRegistry<LootItemFunctionType>> LOOT_FUNCTION_REGISTRY = createRegistryKey("loot_function_type");
    public static final ResourceKey<IRegistry<LootItemConditionType>> LOOT_ITEM_REGISTRY = createRegistryKey("loot_condition_type");
    public static final ResourceKey<IRegistry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_REGISTRY = createRegistryKey("loot_number_provider_type");
    public static final ResourceKey<IRegistry<LootNbtProviderType>> LOOT_NBT_PROVIDER_REGISTRY = createRegistryKey("loot_nbt_provider_type");
    public static final ResourceKey<IRegistry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_REGISTRY = createRegistryKey("loot_score_provider_type");
    public static final ResourceKey<IRegistry<DimensionManager>> DIMENSION_TYPE_REGISTRY = createRegistryKey("dimension_type");
    public static final ResourceKey<IRegistry<World>> DIMENSION_REGISTRY = createRegistryKey("dimension");
    public static final ResourceKey<IRegistry<WorldDimension>> LEVEL_STEM_REGISTRY = createRegistryKey("dimension");
    public static final RegistryBlocks<GameEvent> GAME_EVENT = registerDefaulted(IRegistry.GAME_EVENT_REGISTRY, "step", () -> {
        return GameEvent.STEP;
    });
    public static final IRegistry<SoundEffect> SOUND_EVENT = registerSimple(IRegistry.SOUND_EVENT_REGISTRY, () -> {
        return SoundEffects.ITEM_PICKUP;
    });
    public static final RegistryBlocks<FluidType> FLUID = registerDefaulted(IRegistry.FLUID_REGISTRY, "empty", () -> {
        return FluidTypes.EMPTY;
    });
    public static final IRegistry<MobEffectList> MOB_EFFECT = registerSimple(IRegistry.MOB_EFFECT_REGISTRY, () -> {
        return MobEffects.LUCK;
    });
    public static final RegistryBlocks<Block> BLOCK = registerDefaulted(IRegistry.BLOCK_REGISTRY, "air", () -> {
        return Blocks.AIR;
    });
    public static final IRegistry<Enchantment> ENCHANTMENT = registerSimple(IRegistry.ENCHANTMENT_REGISTRY, () -> {
        return Enchantments.BLOCK_FORTUNE;
    });
    public static final RegistryBlocks<EntityTypes<?>> ENTITY_TYPE = registerDefaulted(IRegistry.ENTITY_TYPE_REGISTRY, "pig", () -> {
        return EntityTypes.PIG;
    });
    public static final RegistryBlocks<Item> ITEM = registerDefaulted(IRegistry.ITEM_REGISTRY, "air", () -> {
        return Items.AIR;
    });
    public static final RegistryBlocks<PotionRegistry> POTION = registerDefaulted(IRegistry.POTION_REGISTRY, "empty", () -> {
        return Potions.EMPTY;
    });
    public static final IRegistry<Particle<?>> PARTICLE_TYPE = registerSimple(IRegistry.PARTICLE_TYPE_REGISTRY, () -> {
        return Particles.BLOCK;
    });
    public static final IRegistry<TileEntityTypes<?>> BLOCK_ENTITY_TYPE = registerSimple(IRegistry.BLOCK_ENTITY_TYPE_REGISTRY, () -> {
        return TileEntityTypes.FURNACE;
    });
    public static final RegistryBlocks<Paintings> MOTIVE = registerDefaulted(IRegistry.MOTIVE_REGISTRY, "kebab", () -> {
        return Paintings.KEBAB;
    });
    public static final IRegistry<MinecraftKey> CUSTOM_STAT = registerSimple(IRegistry.CUSTOM_STAT_REGISTRY, () -> {
        return StatisticList.JUMP;
    });
    public static final RegistryBlocks<ChunkStatus> CHUNK_STATUS = registerDefaulted(IRegistry.CHUNK_STATUS_REGISTRY, "empty", () -> {
        return ChunkStatus.EMPTY;
    });
    public static final IRegistry<DefinedStructureRuleTestType<?>> RULE_TEST = registerSimple(IRegistry.RULE_TEST_REGISTRY, () -> {
        return DefinedStructureRuleTestType.ALWAYS_TRUE_TEST;
    });
    public static final IRegistry<PosRuleTestType<?>> POS_RULE_TEST = registerSimple(IRegistry.POS_RULE_TEST_REGISTRY, () -> {
        return PosRuleTestType.ALWAYS_TRUE_TEST;
    });
    public static final IRegistry<Containers<?>> MENU = registerSimple(IRegistry.MENU_REGISTRY, () -> {
        return Containers.ANVIL;
    });
    public static final IRegistry<Recipes<?>> RECIPE_TYPE = registerSimple(IRegistry.RECIPE_TYPE_REGISTRY, () -> {
        return Recipes.CRAFTING;
    });
    public static final IRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = registerSimple(IRegistry.RECIPE_SERIALIZER_REGISTRY, () -> {
        return RecipeSerializer.SHAPELESS_RECIPE;
    });
    public static final IRegistry<AttributeBase> ATTRIBUTE = registerSimple(IRegistry.ATTRIBUTE_REGISTRY, () -> {
        return GenericAttributes.LUCK;
    });
    public static final IRegistry<PositionSourceType<?>> POSITION_SOURCE_TYPE = registerSimple(IRegistry.POSITION_SOURCE_TYPE_REGISTRY, () -> {
        return PositionSourceType.BLOCK;
    });
    public static final IRegistry<StatisticWrapper<?>> STAT_TYPE = registerSimple(IRegistry.STAT_TYPE_REGISTRY, () -> {
        return StatisticList.ITEM_USED;
    });
    public static final RegistryBlocks<VillagerType> VILLAGER_TYPE = registerDefaulted(IRegistry.VILLAGER_TYPE_REGISTRY, "plains", () -> {
        return VillagerType.PLAINS;
    });
    public static final RegistryBlocks<VillagerProfession> VILLAGER_PROFESSION = registerDefaulted(IRegistry.VILLAGER_PROFESSION_REGISTRY, "none", () -> {
        return VillagerProfession.NONE;
    });
    public static final RegistryBlocks<VillagePlaceType> POINT_OF_INTEREST_TYPE = registerDefaulted(IRegistry.POINT_OF_INTEREST_TYPE_REGISTRY, "unemployed", () -> {
        return VillagePlaceType.UNEMPLOYED;
    });
    public static final RegistryBlocks<MemoryModuleType<?>> MEMORY_MODULE_TYPE = registerDefaulted(IRegistry.MEMORY_MODULE_TYPE_REGISTRY, "dummy", () -> {
        return MemoryModuleType.DUMMY;
    });
    public static final RegistryBlocks<SensorType<?>> SENSOR_TYPE = registerDefaulted(IRegistry.SENSOR_TYPE_REGISTRY, "dummy", () -> {
        return SensorType.DUMMY;
    });
    public static final IRegistry<Schedule> SCHEDULE = registerSimple(IRegistry.SCHEDULE_REGISTRY, () -> {
        return Schedule.EMPTY;
    });
    public static final IRegistry<Activity> ACTIVITY = registerSimple(IRegistry.ACTIVITY_REGISTRY, () -> {
        return Activity.IDLE;
    });
    public static final IRegistry<LootEntryType> LOOT_POOL_ENTRY_TYPE = registerSimple(IRegistry.LOOT_ENTRY_REGISTRY, () -> {
        return LootEntries.EMPTY;
    });
    public static final IRegistry<LootItemFunctionType> LOOT_FUNCTION_TYPE = registerSimple(IRegistry.LOOT_FUNCTION_REGISTRY, () -> {
        return LootItemFunctions.SET_COUNT;
    });
    public static final IRegistry<LootItemConditionType> LOOT_CONDITION_TYPE = registerSimple(IRegistry.LOOT_ITEM_REGISTRY, () -> {
        return LootItemConditions.INVERTED;
    });
    public static final IRegistry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = registerSimple(IRegistry.LOOT_NUMBER_PROVIDER_REGISTRY, () -> {
        return NumberProviders.CONSTANT;
    });
    public static final IRegistry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = registerSimple(IRegistry.LOOT_NBT_PROVIDER_REGISTRY, () -> {
        return NbtProviders.CONTEXT;
    });
    public static final IRegistry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = registerSimple(IRegistry.LOOT_SCORE_PROVIDER_REGISTRY, () -> {
        return ScoreboardNameProviders.CONTEXT;
    });
    public static final ResourceKey<IRegistry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE_REGISTRY = createRegistryKey("float_provider_type");
    public static final IRegistry<FloatProviderType<?>> FLOAT_PROVIDER_TYPES = registerSimple(IRegistry.FLOAT_PROVIDER_TYPE_REGISTRY, () -> {
        return FloatProviderType.CONSTANT;
    });
    public static final ResourceKey<IRegistry<IntProviderType<?>>> INT_PROVIDER_TYPE_REGISTRY = createRegistryKey("int_provider_type");
    public static final IRegistry<IntProviderType<?>> INT_PROVIDER_TYPES = registerSimple(IRegistry.INT_PROVIDER_TYPE_REGISTRY, () -> {
        return IntProviderType.CONSTANT;
    });
    public static final ResourceKey<IRegistry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE_REGISTRY = createRegistryKey("height_provider_type");
    public static final IRegistry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPES = registerSimple(IRegistry.HEIGHT_PROVIDER_TYPE_REGISTRY, () -> {
        return HeightProviderType.CONSTANT;
    });
    public static final ResourceKey<IRegistry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE_REGISTRY = createRegistryKey("block_predicate_type");
    public static final IRegistry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPES = registerSimple(IRegistry.BLOCK_PREDICATE_TYPE_REGISTRY, () -> {
        return BlockPredicateType.NOT;
    });
    public static final ResourceKey<IRegistry<GeneratorSettingBase>> NOISE_GENERATOR_SETTINGS_REGISTRY = createRegistryKey("worldgen/noise_settings");
    public static final ResourceKey<IRegistry<WorldGenCarverWrapper<?>>> CONFIGURED_CARVER_REGISTRY = createRegistryKey("worldgen/configured_carver");
    public static final ResourceKey<IRegistry<WorldGenFeatureConfigured<?, ?>>> CONFIGURED_FEATURE_REGISTRY = createRegistryKey("worldgen/configured_feature");
    public static final ResourceKey<IRegistry<PlacedFeature>> PLACED_FEATURE_REGISTRY = createRegistryKey("worldgen/placed_feature");
    public static final ResourceKey<IRegistry<StructureFeature<?, ?>>> CONFIGURED_STRUCTURE_FEATURE_REGISTRY = createRegistryKey("worldgen/configured_structure_feature");
    public static final ResourceKey<IRegistry<ProcessorList>> PROCESSOR_LIST_REGISTRY = createRegistryKey("worldgen/processor_list");
    public static final ResourceKey<IRegistry<WorldGenFeatureDefinedStructurePoolTemplate>> TEMPLATE_POOL_REGISTRY = createRegistryKey("worldgen/template_pool");
    public static final ResourceKey<IRegistry<BiomeBase>> BIOME_REGISTRY = createRegistryKey("worldgen/biome");
    public static final ResourceKey<IRegistry<NoiseGeneratorNormal.a>> NOISE_REGISTRY = createRegistryKey("worldgen/noise");
    public static final ResourceKey<IRegistry<WorldGenCarverAbstract<?>>> CARVER_REGISTRY = createRegistryKey("worldgen/carver");
    public static final IRegistry<WorldGenCarverAbstract<?>> CARVER = registerSimple(IRegistry.CARVER_REGISTRY, () -> {
        return WorldGenCarverAbstract.CAVE;
    });
    public static final ResourceKey<IRegistry<WorldGenerator<?>>> FEATURE_REGISTRY = createRegistryKey("worldgen/feature");
    public static final IRegistry<WorldGenerator<?>> FEATURE = registerSimple(IRegistry.FEATURE_REGISTRY, () -> {
        return WorldGenerator.ORE;
    });
    public static final ResourceKey<IRegistry<StructureGenerator<?>>> STRUCTURE_FEATURE_REGISTRY = createRegistryKey("worldgen/structure_feature");
    public static final IRegistry<StructureGenerator<?>> STRUCTURE_FEATURE = registerSimple(IRegistry.STRUCTURE_FEATURE_REGISTRY, () -> {
        return StructureGenerator.MINESHAFT;
    });
    public static final ResourceKey<IRegistry<WorldGenFeatureStructurePieceType>> STRUCTURE_PIECE_REGISTRY = createRegistryKey("worldgen/structure_piece");
    public static final IRegistry<WorldGenFeatureStructurePieceType> STRUCTURE_PIECE = registerSimple(IRegistry.STRUCTURE_PIECE_REGISTRY, () -> {
        return WorldGenFeatureStructurePieceType.MINE_SHAFT_ROOM;
    });
    public static final ResourceKey<IRegistry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_REGISTRY = createRegistryKey("worldgen/placement_modifier_type");
    public static final IRegistry<PlacementModifierType<?>> PLACEMENT_MODIFIERS = registerSimple(IRegistry.PLACEMENT_MODIFIER_REGISTRY, () -> {
        return PlacementModifierType.COUNT;
    });
    public static final ResourceKey<IRegistry<WorldGenFeatureStateProviders<?>>> BLOCK_STATE_PROVIDER_TYPE_REGISTRY = createRegistryKey("worldgen/block_state_provider_type");
    public static final ResourceKey<IRegistry<WorldGenFoilagePlacers<?>>> FOLIAGE_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/foliage_placer_type");
    public static final ResourceKey<IRegistry<TrunkPlacers<?>>> TRUNK_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/trunk_placer_type");
    public static final ResourceKey<IRegistry<WorldGenFeatureTrees<?>>> TREE_DECORATOR_TYPE_REGISTRY = createRegistryKey("worldgen/tree_decorator_type");
    public static final ResourceKey<IRegistry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_REGISTRY = createRegistryKey("worldgen/feature_size_type");
    public static final ResourceKey<IRegistry<Codec<? extends WorldChunkManager>>> BIOME_SOURCE_REGISTRY = createRegistryKey("worldgen/biome_source");
    public static final ResourceKey<IRegistry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_REGISTRY = createRegistryKey("worldgen/chunk_generator");
    public static final ResourceKey<IRegistry<Codec<? extends SurfaceRules.f>>> CONDITION_REGISTRY = createRegistryKey("worldgen/material_condition");
    public static final ResourceKey<IRegistry<Codec<? extends SurfaceRules.o>>> RULE_REGISTRY = createRegistryKey("worldgen/material_rule");
    public static final ResourceKey<IRegistry<DefinedStructureStructureProcessorType<?>>> STRUCTURE_PROCESSOR_REGISTRY = createRegistryKey("worldgen/structure_processor");
    public static final ResourceKey<IRegistry<WorldGenFeatureDefinedStructurePools<?>>> STRUCTURE_POOL_ELEMENT_REGISTRY = createRegistryKey("worldgen/structure_pool_element");
    public static final IRegistry<WorldGenFeatureStateProviders<?>> BLOCKSTATE_PROVIDER_TYPES = registerSimple(IRegistry.BLOCK_STATE_PROVIDER_TYPE_REGISTRY, () -> {
        return WorldGenFeatureStateProviders.SIMPLE_STATE_PROVIDER;
    });
    public static final IRegistry<WorldGenFoilagePlacers<?>> FOLIAGE_PLACER_TYPES = registerSimple(IRegistry.FOLIAGE_PLACER_TYPE_REGISTRY, () -> {
        return WorldGenFoilagePlacers.BLOB_FOLIAGE_PLACER;
    });
    public static final IRegistry<TrunkPlacers<?>> TRUNK_PLACER_TYPES = registerSimple(IRegistry.TRUNK_PLACER_TYPE_REGISTRY, () -> {
        return TrunkPlacers.STRAIGHT_TRUNK_PLACER;
    });
    public static final IRegistry<WorldGenFeatureTrees<?>> TREE_DECORATOR_TYPES = registerSimple(IRegistry.TREE_DECORATOR_TYPE_REGISTRY, () -> {
        return WorldGenFeatureTrees.LEAVE_VINE;
    });
    public static final IRegistry<FeatureSizeType<?>> FEATURE_SIZE_TYPES = registerSimple(IRegistry.FEATURE_SIZE_TYPE_REGISTRY, () -> {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    });
    public static final IRegistry<Codec<? extends WorldChunkManager>> BIOME_SOURCE = registerSimple(IRegistry.BIOME_SOURCE_REGISTRY, Lifecycle.stable(), () -> {
        return WorldChunkManager.CODEC;
    });
    public static final IRegistry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = registerSimple(IRegistry.CHUNK_GENERATOR_REGISTRY, Lifecycle.stable(), () -> {
        return ChunkGenerator.CODEC;
    });
    public static final IRegistry<Codec<? extends SurfaceRules.f>> CONDITION = registerSimple(IRegistry.CONDITION_REGISTRY, SurfaceRules.f::bootstrap);
    public static final IRegistry<Codec<? extends SurfaceRules.o>> RULE = registerSimple(IRegistry.RULE_REGISTRY, SurfaceRules.o::bootstrap);
    public static final IRegistry<DefinedStructureStructureProcessorType<?>> STRUCTURE_PROCESSOR = registerSimple(IRegistry.STRUCTURE_PROCESSOR_REGISTRY, () -> {
        return DefinedStructureStructureProcessorType.BLOCK_IGNORE;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePools<?>> STRUCTURE_POOL_ELEMENT = registerSimple(IRegistry.STRUCTURE_POOL_ELEMENT_REGISTRY, () -> {
        return WorldGenFeatureDefinedStructurePools.EMPTY;
    });
    private final ResourceKey<? extends IRegistry<T>> key;
    private final Lifecycle lifecycle;

    private static <T> ResourceKey<IRegistry<T>> createRegistryKey(String s) {
        return ResourceKey.createRegistryKey(new MinecraftKey(s));
    }

    public static <T extends IRegistryWritable<?>> void checkRegistry(IRegistryWritable<T> iregistrywritable) {
        iregistrywritable.forEach((iregistrywritable1) -> {
            if (iregistrywritable1.keySet().isEmpty()) {
                MinecraftKey minecraftkey = iregistrywritable.getKey(iregistrywritable1);

                SystemUtils.logAndPauseIfInIde("Registry '" + minecraftkey + "' was empty after loading");
            }

            if (iregistrywritable1 instanceof RegistryBlocks) {
                MinecraftKey minecraftkey1 = ((RegistryBlocks) iregistrywritable1).getDefaultKey();

                Validate.notNull(iregistrywritable1.get(minecraftkey1), "Missing default of DefaultedMappedRegistry: " + minecraftkey1, new Object[0]);
            }

        });
    }

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, Supplier<T> supplier) {
        return registerSimple(resourcekey, Lifecycle.experimental(), supplier);
    }

    private static <T> RegistryBlocks<T> registerDefaulted(ResourceKey<? extends IRegistry<T>> resourcekey, String s, Supplier<T> supplier) {
        return registerDefaulted(resourcekey, s, Lifecycle.experimental(), supplier);
    }

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, Supplier<T> supplier) {
        return internalRegister(resourcekey, new RegistryMaterials<>(resourcekey, lifecycle), supplier, lifecycle);
    }

    private static <T> RegistryBlocks<T> registerDefaulted(ResourceKey<? extends IRegistry<T>> resourcekey, String s, Lifecycle lifecycle, Supplier<T> supplier) {
        return (RegistryBlocks) internalRegister(resourcekey, new RegistryBlocks<>(s, resourcekey, lifecycle), supplier, lifecycle);
    }

    private static <T, R extends IRegistryWritable<T>> R internalRegister(ResourceKey<? extends IRegistry<T>> resourcekey, R r0, Supplier<T> supplier, Lifecycle lifecycle) {
        MinecraftKey minecraftkey = resourcekey.location();

        IRegistry.LOADERS.put(minecraftkey, supplier);
        IRegistryWritable<R> iregistrywritable = IRegistry.WRITABLE_REGISTRY;

        return (IRegistryWritable) iregistrywritable.register(resourcekey, (Object) r0, lifecycle);
    }

    protected IRegistry(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle) {
        DispenserRegistry.checkBootstrapCalled(() -> {
            return "registry " + resourcekey;
        });
        this.key = resourcekey;
        this.lifecycle = lifecycle;
    }

    public ResourceKey<? extends IRegistry<T>> key() {
        return this.key;
    }

    public Lifecycle lifecycle() {
        return this.lifecycle;
    }

    public String toString() {
        return "Registry[" + this.key + " (" + this.lifecycle + ")]";
    }

    public Codec<T> byNameCodec() {
        Codec<T> codec = MinecraftKey.CODEC.flatXmap((minecraftkey) -> {
            return (DataResult) Optional.ofNullable(this.get(minecraftkey)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown registry key in " + this.key + ": " + minecraftkey);
            });
        }, (object) -> {
            return (DataResult) this.getResourceKey(object).map(ResourceKey::location).map(DataResult::success).orElseGet(() -> {
                return DataResult.error("Unknown registry element in " + this.key + ":" + object);
            });
        });
        Codec<T> codec1 = ExtraCodecs.idResolverCodec((object) -> {
            return this.getResourceKey(object).isPresent() ? this.getId(object) : -1;
        }, this::byId, -1);

        return ExtraCodecs.overrideLifecycle(ExtraCodecs.orCompressed(codec, codec1), this::lifecycle, (object) -> {
            return this.lifecycle;
        });
    }

    public <U> Stream<U> keys(DynamicOps<U> dynamicops) {
        return this.keySet().stream().map((minecraftkey) -> {
            return dynamicops.createString(minecraftkey.toString());
        });
    }

    @Nullable
    public abstract MinecraftKey getKey(T t0);

    public abstract Optional<ResourceKey<T>> getResourceKey(T t0);

    @Override
    public abstract int getId(@Nullable T t0);

    @Nullable
    public abstract T get(@Nullable ResourceKey<T> resourcekey);

    @Nullable
    public abstract T get(@Nullable MinecraftKey minecraftkey);

    public abstract Lifecycle lifecycle(T t0);

    public abstract Lifecycle elementsLifecycle();

    public Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(this.get(minecraftkey));
    }

    public Optional<T> getOptional(@Nullable ResourceKey<T> resourcekey) {
        return Optional.ofNullable(this.get(resourcekey));
    }

    public T getOrThrow(ResourceKey<T> resourcekey) {
        T t0 = this.get(resourcekey);

        if (t0 == null) {
            throw new IllegalStateException("Missing key in " + this.key + ": " + resourcekey);
        } else {
            return t0;
        }
    }

    public abstract Set<MinecraftKey> keySet();

    public abstract Set<Entry<ResourceKey<T>, T>> entrySet();

    @Nullable
    public abstract T getRandom(Random random);

    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public abstract boolean containsKey(MinecraftKey minecraftkey);

    public abstract boolean containsKey(ResourceKey<T> resourcekey);

    public static <T> T register(IRegistry<? super T> iregistry, String s, T t0) {
        return register(iregistry, new MinecraftKey(s), t0);
    }

    public static <V, T extends V> T register(IRegistry<V> iregistry, MinecraftKey minecraftkey, T t0) {
        return register(iregistry, ResourceKey.create(iregistry.key, minecraftkey), t0);
    }

    public static <V, T extends V> T register(IRegistry<V> iregistry, ResourceKey<V> resourcekey, T t0) {
        return ((IRegistryWritable) iregistry).register(resourcekey, t0, Lifecycle.stable());
    }

    public static <V, T extends V> T registerMapping(IRegistry<V> iregistry, int i, String s, T t0) {
        return ((IRegistryWritable) iregistry).registerMapping(i, ResourceKey.create(iregistry.key, new MinecraftKey(s)), t0, Lifecycle.stable());
    }

    static {
        RegistryGeneration.bootstrap();
        IRegistry.LOADERS.forEach((minecraftkey, supplier) -> {
            if (supplier.get() == null) {
                IRegistry.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
        checkRegistry(IRegistry.WRITABLE_REGISTRY);
    }
}
