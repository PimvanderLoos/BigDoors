package net.minecraft.core.registries;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.SystemUtils;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.core.particles.Particle;
import net.minecraft.core.particles.Particles;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.biome.WorldChunkManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerators;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldGenCarverAbstract;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacers;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProviders;
import net.minecraft.world.level.levelgen.feature.treedecorators.WorldGenFeatureTrees;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacers;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.WorldGenFeatureStructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePools;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureStructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
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
import org.slf4j.Logger;

public class BuiltInRegistries {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<MinecraftKey, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    public static final MinecraftKey ROOT_REGISTRY_NAME = new MinecraftKey("root");
    private static final IRegistryWritable<IRegistryWritable<?>> WRITABLE_REGISTRY = new RegistryMaterials<>(ResourceKey.createRegistryKey(BuiltInRegistries.ROOT_REGISTRY_NAME), Lifecycle.stable());
    public static final RegistryBlocks<GameEvent> GAME_EVENT = registerDefaultedWithIntrusiveHolders(Registries.GAME_EVENT, "step", (iregistry) -> {
        return GameEvent.STEP;
    });
    public static final IRegistry<SoundEffect> SOUND_EVENT = registerSimple(Registries.SOUND_EVENT, (iregistry) -> {
        return SoundEffects.ITEM_PICKUP;
    });
    public static final RegistryBlocks<FluidType> FLUID = registerDefaultedWithIntrusiveHolders(Registries.FLUID, "empty", (iregistry) -> {
        return FluidTypes.EMPTY;
    });
    public static final IRegistry<MobEffectList> MOB_EFFECT = registerSimple(Registries.MOB_EFFECT, (iregistry) -> {
        return MobEffects.LUCK;
    });
    public static final RegistryBlocks<Block> BLOCK = registerDefaultedWithIntrusiveHolders(Registries.BLOCK, "air", (iregistry) -> {
        return Blocks.AIR;
    });
    public static final IRegistry<Enchantment> ENCHANTMENT = registerSimple(Registries.ENCHANTMENT, (iregistry) -> {
        return Enchantments.BLOCK_FORTUNE;
    });
    public static final RegistryBlocks<EntityTypes<?>> ENTITY_TYPE = registerDefaultedWithIntrusiveHolders(Registries.ENTITY_TYPE, "pig", (iregistry) -> {
        return EntityTypes.PIG;
    });
    public static final RegistryBlocks<Item> ITEM = registerDefaultedWithIntrusiveHolders(Registries.ITEM, "air", (iregistry) -> {
        return Items.AIR;
    });
    public static final RegistryBlocks<PotionRegistry> POTION = registerDefaulted(Registries.POTION, "empty", (iregistry) -> {
        return Potions.EMPTY;
    });
    public static final IRegistry<Particle<?>> PARTICLE_TYPE = registerSimple(Registries.PARTICLE_TYPE, (iregistry) -> {
        return Particles.BLOCK;
    });
    public static final IRegistry<TileEntityTypes<?>> BLOCK_ENTITY_TYPE = registerSimple(Registries.BLOCK_ENTITY_TYPE, (iregistry) -> {
        return TileEntityTypes.FURNACE;
    });
    public static final RegistryBlocks<PaintingVariant> PAINTING_VARIANT = registerDefaulted(Registries.PAINTING_VARIANT, "kebab", PaintingVariants::bootstrap);
    public static final IRegistry<MinecraftKey> CUSTOM_STAT = registerSimple(Registries.CUSTOM_STAT, (iregistry) -> {
        return StatisticList.JUMP;
    });
    public static final RegistryBlocks<ChunkStatus> CHUNK_STATUS = registerDefaulted(Registries.CHUNK_STATUS, "empty", (iregistry) -> {
        return ChunkStatus.EMPTY;
    });
    public static final IRegistry<DefinedStructureRuleTestType<?>> RULE_TEST = registerSimple(Registries.RULE_TEST, (iregistry) -> {
        return DefinedStructureRuleTestType.ALWAYS_TRUE_TEST;
    });
    public static final IRegistry<PosRuleTestType<?>> POS_RULE_TEST = registerSimple(Registries.POS_RULE_TEST, (iregistry) -> {
        return PosRuleTestType.ALWAYS_TRUE_TEST;
    });
    public static final IRegistry<Containers<?>> MENU = registerSimple(Registries.MENU, (iregistry) -> {
        return Containers.ANVIL;
    });
    public static final IRegistry<Recipes<?>> RECIPE_TYPE = registerSimple(Registries.RECIPE_TYPE, (iregistry) -> {
        return Recipes.CRAFTING;
    });
    public static final IRegistry<RecipeSerializer<?>> RECIPE_SERIALIZER = registerSimple(Registries.RECIPE_SERIALIZER, (iregistry) -> {
        return RecipeSerializer.SHAPELESS_RECIPE;
    });
    public static final IRegistry<AttributeBase> ATTRIBUTE = registerSimple(Registries.ATTRIBUTE, (iregistry) -> {
        return GenericAttributes.LUCK;
    });
    public static final IRegistry<PositionSourceType<?>> POSITION_SOURCE_TYPE = registerSimple(Registries.POSITION_SOURCE_TYPE, (iregistry) -> {
        return PositionSourceType.BLOCK;
    });
    public static final IRegistry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = registerSimple(Registries.COMMAND_ARGUMENT_TYPE, ArgumentTypeInfos::bootstrap);
    public static final IRegistry<StatisticWrapper<?>> STAT_TYPE = registerSimple(Registries.STAT_TYPE, (iregistry) -> {
        return StatisticList.ITEM_USED;
    });
    public static final RegistryBlocks<VillagerType> VILLAGER_TYPE = registerDefaulted(Registries.VILLAGER_TYPE, "plains", (iregistry) -> {
        return VillagerType.PLAINS;
    });
    public static final RegistryBlocks<VillagerProfession> VILLAGER_PROFESSION = registerDefaulted(Registries.VILLAGER_PROFESSION, "none", (iregistry) -> {
        return VillagerProfession.NONE;
    });
    public static final IRegistry<VillagePlaceType> POINT_OF_INTEREST_TYPE = registerSimple(Registries.POINT_OF_INTEREST_TYPE, PoiTypes::bootstrap);
    public static final RegistryBlocks<MemoryModuleType<?>> MEMORY_MODULE_TYPE = registerDefaulted(Registries.MEMORY_MODULE_TYPE, "dummy", (iregistry) -> {
        return MemoryModuleType.DUMMY;
    });
    public static final RegistryBlocks<SensorType<?>> SENSOR_TYPE = registerDefaulted(Registries.SENSOR_TYPE, "dummy", (iregistry) -> {
        return SensorType.DUMMY;
    });
    public static final IRegistry<Schedule> SCHEDULE = registerSimple(Registries.SCHEDULE, (iregistry) -> {
        return Schedule.EMPTY;
    });
    public static final IRegistry<Activity> ACTIVITY = registerSimple(Registries.ACTIVITY, (iregistry) -> {
        return Activity.IDLE;
    });
    public static final IRegistry<LootEntryType> LOOT_POOL_ENTRY_TYPE = registerSimple(Registries.LOOT_POOL_ENTRY_TYPE, (iregistry) -> {
        return LootEntries.EMPTY;
    });
    public static final IRegistry<LootItemFunctionType> LOOT_FUNCTION_TYPE = registerSimple(Registries.LOOT_FUNCTION_TYPE, (iregistry) -> {
        return LootItemFunctions.SET_COUNT;
    });
    public static final IRegistry<LootItemConditionType> LOOT_CONDITION_TYPE = registerSimple(Registries.LOOT_CONDITION_TYPE, (iregistry) -> {
        return LootItemConditions.INVERTED;
    });
    public static final IRegistry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = registerSimple(Registries.LOOT_NUMBER_PROVIDER_TYPE, (iregistry) -> {
        return NumberProviders.CONSTANT;
    });
    public static final IRegistry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = registerSimple(Registries.LOOT_NBT_PROVIDER_TYPE, (iregistry) -> {
        return NbtProviders.CONTEXT;
    });
    public static final IRegistry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = registerSimple(Registries.LOOT_SCORE_PROVIDER_TYPE, (iregistry) -> {
        return ScoreboardNameProviders.CONTEXT;
    });
    public static final IRegistry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = registerSimple(Registries.FLOAT_PROVIDER_TYPE, (iregistry) -> {
        return FloatProviderType.CONSTANT;
    });
    public static final IRegistry<IntProviderType<?>> INT_PROVIDER_TYPE = registerSimple(Registries.INT_PROVIDER_TYPE, (iregistry) -> {
        return IntProviderType.CONSTANT;
    });
    public static final IRegistry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = registerSimple(Registries.HEIGHT_PROVIDER_TYPE, (iregistry) -> {
        return HeightProviderType.CONSTANT;
    });
    public static final IRegistry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = registerSimple(Registries.BLOCK_PREDICATE_TYPE, (iregistry) -> {
        return BlockPredicateType.NOT;
    });
    public static final IRegistry<WorldGenCarverAbstract<?>> CARVER = registerSimple(Registries.CARVER, (iregistry) -> {
        return WorldGenCarverAbstract.CAVE;
    });
    public static final IRegistry<WorldGenerator<?>> FEATURE = registerSimple(Registries.FEATURE, (iregistry) -> {
        return WorldGenerator.ORE;
    });
    public static final IRegistry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = registerSimple(Registries.STRUCTURE_PLACEMENT, (iregistry) -> {
        return StructurePlacementType.RANDOM_SPREAD;
    });
    public static final IRegistry<WorldGenFeatureStructurePieceType> STRUCTURE_PIECE = registerSimple(Registries.STRUCTURE_PIECE, (iregistry) -> {
        return WorldGenFeatureStructurePieceType.MINE_SHAFT_ROOM;
    });
    public static final IRegistry<StructureType<?>> STRUCTURE_TYPE = registerSimple(Registries.STRUCTURE_TYPE, (iregistry) -> {
        return StructureType.JIGSAW;
    });
    public static final IRegistry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = registerSimple(Registries.PLACEMENT_MODIFIER_TYPE, (iregistry) -> {
        return PlacementModifierType.COUNT;
    });
    public static final IRegistry<WorldGenFeatureStateProviders<?>> BLOCKSTATE_PROVIDER_TYPE = registerSimple(Registries.BLOCK_STATE_PROVIDER_TYPE, (iregistry) -> {
        return WorldGenFeatureStateProviders.SIMPLE_STATE_PROVIDER;
    });
    public static final IRegistry<WorldGenFoilagePlacers<?>> FOLIAGE_PLACER_TYPE = registerSimple(Registries.FOLIAGE_PLACER_TYPE, (iregistry) -> {
        return WorldGenFoilagePlacers.BLOB_FOLIAGE_PLACER;
    });
    public static final IRegistry<TrunkPlacers<?>> TRUNK_PLACER_TYPE = registerSimple(Registries.TRUNK_PLACER_TYPE, (iregistry) -> {
        return TrunkPlacers.STRAIGHT_TRUNK_PLACER;
    });
    public static final IRegistry<RootPlacerType<?>> ROOT_PLACER_TYPE = registerSimple(Registries.ROOT_PLACER_TYPE, (iregistry) -> {
        return RootPlacerType.MANGROVE_ROOT_PLACER;
    });
    public static final IRegistry<WorldGenFeatureTrees<?>> TREE_DECORATOR_TYPE = registerSimple(Registries.TREE_DECORATOR_TYPE, (iregistry) -> {
        return WorldGenFeatureTrees.LEAVE_VINE;
    });
    public static final IRegistry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = registerSimple(Registries.FEATURE_SIZE_TYPE, (iregistry) -> {
        return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
    });
    public static final IRegistry<Codec<? extends WorldChunkManager>> BIOME_SOURCE = registerSimple(Registries.BIOME_SOURCE, Lifecycle.stable(), BiomeSources::bootstrap);
    public static final IRegistry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = registerSimple(Registries.CHUNK_GENERATOR, Lifecycle.stable(), ChunkGenerators::bootstrap);
    public static final IRegistry<Codec<? extends SurfaceRules.f>> MATERIAL_CONDITION = registerSimple(Registries.MATERIAL_CONDITION, SurfaceRules.f::bootstrap);
    public static final IRegistry<Codec<? extends SurfaceRules.o>> MATERIAL_RULE = registerSimple(Registries.MATERIAL_RULE, SurfaceRules.o::bootstrap);
    public static final IRegistry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = registerSimple(Registries.DENSITY_FUNCTION_TYPE, DensityFunctions::bootstrap);
    public static final IRegistry<DefinedStructureStructureProcessorType<?>> STRUCTURE_PROCESSOR = registerSimple(Registries.STRUCTURE_PROCESSOR, (iregistry) -> {
        return DefinedStructureStructureProcessorType.BLOCK_IGNORE;
    });
    public static final IRegistry<WorldGenFeatureDefinedStructurePools<?>> STRUCTURE_POOL_ELEMENT = registerSimple(Registries.STRUCTURE_POOL_ELEMENT, (iregistry) -> {
        return WorldGenFeatureDefinedStructurePools.EMPTY;
    });
    public static final IRegistry<CatVariant> CAT_VARIANT = registerSimple(Registries.CAT_VARIANT, CatVariant::bootstrap);
    public static final IRegistry<FrogVariant> FROG_VARIANT = registerSimple(Registries.FROG_VARIANT, (iregistry) -> {
        return FrogVariant.TEMPERATE;
    });
    public static final IRegistry<EnumBannerPatternType> BANNER_PATTERN = registerSimple(Registries.BANNER_PATTERN, BannerPatterns::bootstrap);
    public static final IRegistry<Instrument> INSTRUMENT = registerSimple(Registries.INSTRUMENT, Instruments::bootstrap);
    public static final IRegistry<? extends IRegistry<?>> REGISTRY = BuiltInRegistries.WRITABLE_REGISTRY;

    public BuiltInRegistries() {}

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, BuiltInRegistries.a<T> builtinregistries_a) {
        return registerSimple(resourcekey, Lifecycle.stable(), builtinregistries_a);
    }

    private static <T> RegistryBlocks<T> registerDefaulted(ResourceKey<? extends IRegistry<T>> resourcekey, String s, BuiltInRegistries.a<T> builtinregistries_a) {
        return registerDefaulted(resourcekey, s, Lifecycle.stable(), builtinregistries_a);
    }

    private static <T> RegistryBlocks<T> registerDefaultedWithIntrusiveHolders(ResourceKey<? extends IRegistry<T>> resourcekey, String s, BuiltInRegistries.a<T> builtinregistries_a) {
        return registerDefaultedWithIntrusiveHolders(resourcekey, s, Lifecycle.stable(), builtinregistries_a);
    }

    private static <T> IRegistry<T> registerSimple(ResourceKey<? extends IRegistry<T>> resourcekey, Lifecycle lifecycle, BuiltInRegistries.a<T> builtinregistries_a) {
        return internalRegister(resourcekey, new RegistryMaterials<>(resourcekey, lifecycle, false), builtinregistries_a, lifecycle);
    }

    private static <T> RegistryBlocks<T> registerDefaulted(ResourceKey<? extends IRegistry<T>> resourcekey, String s, Lifecycle lifecycle, BuiltInRegistries.a<T> builtinregistries_a) {
        return (RegistryBlocks) internalRegister(resourcekey, new DefaultedMappedRegistry<>(s, resourcekey, lifecycle, false), builtinregistries_a, lifecycle);
    }

    private static <T> RegistryBlocks<T> registerDefaultedWithIntrusiveHolders(ResourceKey<? extends IRegistry<T>> resourcekey, String s, Lifecycle lifecycle, BuiltInRegistries.a<T> builtinregistries_a) {
        return (RegistryBlocks) internalRegister(resourcekey, new DefaultedMappedRegistry<>(s, resourcekey, lifecycle, true), builtinregistries_a, lifecycle);
    }

    private static <T, R extends IRegistryWritable<T>> R internalRegister(ResourceKey<? extends IRegistry<T>> resourcekey, R r0, BuiltInRegistries.a<T> builtinregistries_a, Lifecycle lifecycle) {
        MinecraftKey minecraftkey = resourcekey.location();

        BuiltInRegistries.LOADERS.put(minecraftkey, () -> {
            return builtinregistries_a.run(r0);
        });
        BuiltInRegistries.WRITABLE_REGISTRY.register(resourcekey, (Object) r0, lifecycle);
        return r0;
    }

    public static void bootStrap() {
        createContents();
        freeze();
        validate(BuiltInRegistries.REGISTRY);
    }

    private static void createContents() {
        BuiltInRegistries.LOADERS.forEach((minecraftkey, supplier) -> {
            if (supplier.get() == null) {
                BuiltInRegistries.LOGGER.error("Unable to bootstrap registry '{}'", minecraftkey);
            }

        });
    }

    private static void freeze() {
        BuiltInRegistries.REGISTRY.freeze();
        Iterator iterator = BuiltInRegistries.REGISTRY.iterator();

        while (iterator.hasNext()) {
            IRegistry<?> iregistry = (IRegistry) iterator.next();

            iregistry.freeze();
        }

    }

    private static <T extends IRegistry<?>> void validate(IRegistry<T> iregistry) {
        iregistry.forEach((iregistry1) -> {
            if (iregistry1.keySet().isEmpty()) {
                MinecraftKey minecraftkey = iregistry.getKey(iregistry1);

                SystemUtils.logAndPauseIfInIde("Registry '" + minecraftkey + "' was empty after loading");
            }

            if (iregistry1 instanceof RegistryBlocks) {
                MinecraftKey minecraftkey1 = ((RegistryBlocks) iregistry1).getDefaultKey();

                Validate.notNull(iregistry1.get(minecraftkey1), "Missing default of DefaultedMappedRegistry: " + minecraftkey1, new Object[0]);
            }

        });
    }

    @FunctionalInterface
    private interface a<T> {

        T run(IRegistry<T> iregistry);
    }
}
