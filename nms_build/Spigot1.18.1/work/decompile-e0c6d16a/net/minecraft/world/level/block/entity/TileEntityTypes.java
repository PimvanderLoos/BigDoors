package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.TileEntityPiston;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityTypes<T extends TileEntity> {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final TileEntityTypes<TileEntityFurnaceFurnace> FURNACE = register("furnace", TileEntityTypes.b.of(TileEntityFurnaceFurnace::new, Blocks.FURNACE));
    public static final TileEntityTypes<TileEntityChest> CHEST = register("chest", TileEntityTypes.b.of(TileEntityChest::new, Blocks.CHEST));
    public static final TileEntityTypes<TileEntityChestTrapped> TRAPPED_CHEST = register("trapped_chest", TileEntityTypes.b.of(TileEntityChestTrapped::new, Blocks.TRAPPED_CHEST));
    public static final TileEntityTypes<TileEntityEnderChest> ENDER_CHEST = register("ender_chest", TileEntityTypes.b.of(TileEntityEnderChest::new, Blocks.ENDER_CHEST));
    public static final TileEntityTypes<TileEntityJukeBox> JUKEBOX = register("jukebox", TileEntityTypes.b.of(TileEntityJukeBox::new, Blocks.JUKEBOX));
    public static final TileEntityTypes<TileEntityDispenser> DISPENSER = register("dispenser", TileEntityTypes.b.of(TileEntityDispenser::new, Blocks.DISPENSER));
    public static final TileEntityTypes<TileEntityDropper> DROPPER = register("dropper", TileEntityTypes.b.of(TileEntityDropper::new, Blocks.DROPPER));
    public static final TileEntityTypes<TileEntitySign> SIGN = register("sign", TileEntityTypes.b.of(TileEntitySign::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN));
    public static final TileEntityTypes<TileEntityMobSpawner> MOB_SPAWNER = register("mob_spawner", TileEntityTypes.b.of(TileEntityMobSpawner::new, Blocks.SPAWNER));
    public static final TileEntityTypes<TileEntityPiston> PISTON = register("piston", TileEntityTypes.b.of(TileEntityPiston::new, Blocks.MOVING_PISTON));
    public static final TileEntityTypes<TileEntityBrewingStand> BREWING_STAND = register("brewing_stand", TileEntityTypes.b.of(TileEntityBrewingStand::new, Blocks.BREWING_STAND));
    public static final TileEntityTypes<TileEntityEnchantTable> ENCHANTING_TABLE = register("enchanting_table", TileEntityTypes.b.of(TileEntityEnchantTable::new, Blocks.ENCHANTING_TABLE));
    public static final TileEntityTypes<TileEntityEnderPortal> END_PORTAL = register("end_portal", TileEntityTypes.b.of(TileEntityEnderPortal::new, Blocks.END_PORTAL));
    public static final TileEntityTypes<TileEntityBeacon> BEACON = register("beacon", TileEntityTypes.b.of(TileEntityBeacon::new, Blocks.BEACON));
    public static final TileEntityTypes<TileEntitySkull> SKULL = register("skull", TileEntityTypes.b.of(TileEntitySkull::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
    public static final TileEntityTypes<TileEntityLightDetector> DAYLIGHT_DETECTOR = register("daylight_detector", TileEntityTypes.b.of(TileEntityLightDetector::new, Blocks.DAYLIGHT_DETECTOR));
    public static final TileEntityTypes<TileEntityHopper> HOPPER = register("hopper", TileEntityTypes.b.of(TileEntityHopper::new, Blocks.HOPPER));
    public static final TileEntityTypes<TileEntityComparator> COMPARATOR = register("comparator", TileEntityTypes.b.of(TileEntityComparator::new, Blocks.COMPARATOR));
    public static final TileEntityTypes<TileEntityBanner> BANNER = register("banner", TileEntityTypes.b.of(TileEntityBanner::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
    public static final TileEntityTypes<TileEntityStructure> STRUCTURE_BLOCK = register("structure_block", TileEntityTypes.b.of(TileEntityStructure::new, Blocks.STRUCTURE_BLOCK));
    public static final TileEntityTypes<TileEntityEndGateway> END_GATEWAY = register("end_gateway", TileEntityTypes.b.of(TileEntityEndGateway::new, Blocks.END_GATEWAY));
    public static final TileEntityTypes<TileEntityCommand> COMMAND_BLOCK = register("command_block", TileEntityTypes.b.of(TileEntityCommand::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK));
    public static final TileEntityTypes<TileEntityShulkerBox> SHULKER_BOX = register("shulker_box", TileEntityTypes.b.of(TileEntityShulkerBox::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX));
    public static final TileEntityTypes<TileEntityBed> BED = register("bed", TileEntityTypes.b.of(TileEntityBed::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED));
    public static final TileEntityTypes<TileEntityConduit> CONDUIT = register("conduit", TileEntityTypes.b.of(TileEntityConduit::new, Blocks.CONDUIT));
    public static final TileEntityTypes<TileEntityBarrel> BARREL = register("barrel", TileEntityTypes.b.of(TileEntityBarrel::new, Blocks.BARREL));
    public static final TileEntityTypes<TileEntitySmoker> SMOKER = register("smoker", TileEntityTypes.b.of(TileEntitySmoker::new, Blocks.SMOKER));
    public static final TileEntityTypes<TileEntityBlastFurnace> BLAST_FURNACE = register("blast_furnace", TileEntityTypes.b.of(TileEntityBlastFurnace::new, Blocks.BLAST_FURNACE));
    public static final TileEntityTypes<TileEntityLectern> LECTERN = register("lectern", TileEntityTypes.b.of(TileEntityLectern::new, Blocks.LECTERN));
    public static final TileEntityTypes<TileEntityBell> BELL = register("bell", TileEntityTypes.b.of(TileEntityBell::new, Blocks.BELL));
    public static final TileEntityTypes<TileEntityJigsaw> JIGSAW = register("jigsaw", TileEntityTypes.b.of(TileEntityJigsaw::new, Blocks.JIGSAW));
    public static final TileEntityTypes<TileEntityCampfire> CAMPFIRE = register("campfire", TileEntityTypes.b.of(TileEntityCampfire::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));
    public static final TileEntityTypes<TileEntityBeehive> BEEHIVE = register("beehive", TileEntityTypes.b.of(TileEntityBeehive::new, Blocks.BEE_NEST, Blocks.BEEHIVE));
    public static final TileEntityTypes<SculkSensorBlockEntity> SCULK_SENSOR = register("sculk_sensor", TileEntityTypes.b.of(SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR));
    private final TileEntityTypes.a<? extends T> factory;
    private final Set<Block> validBlocks;
    private final Type<?> dataType;

    @Nullable
    public static MinecraftKey getKey(TileEntityTypes<?> tileentitytypes) {
        return IRegistry.BLOCK_ENTITY_TYPE.getKey(tileentitytypes);
    }

    private static <T extends TileEntity> TileEntityTypes<T> register(String s, TileEntityTypes.b<T> tileentitytypes_b) {
        if (tileentitytypes_b.validBlocks.isEmpty()) {
            TileEntityTypes.LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", s);
        }

        Type<?> type = SystemUtils.fetchChoiceType(DataConverterTypes.BLOCK_ENTITY, s);

        return (TileEntityTypes) IRegistry.register(IRegistry.BLOCK_ENTITY_TYPE, s, tileentitytypes_b.build(type));
    }

    public TileEntityTypes(TileEntityTypes.a<? extends T> tileentitytypes_a, Set<Block> set, Type<?> type) {
        this.factory = tileentitytypes_a;
        this.validBlocks = set;
        this.dataType = type;
    }

    @Nullable
    public T create(BlockPosition blockposition, IBlockData iblockdata) {
        return this.factory.create(blockposition, iblockdata);
    }

    public boolean isValid(IBlockData iblockdata) {
        return this.validBlocks.contains(iblockdata.getBlock());
    }

    @Nullable
    public T getBlockEntity(IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getBlockEntity(blockposition);

        return tileentity != null && tileentity.getType() == this ? tileentity : null;
    }

    public static final class b<T extends TileEntity> {

        private final TileEntityTypes.a<? extends T> factory;
        final Set<Block> validBlocks;

        private b(TileEntityTypes.a<? extends T> tileentitytypes_a, Set<Block> set) {
            this.factory = tileentitytypes_a;
            this.validBlocks = set;
        }

        public static <T extends TileEntity> TileEntityTypes.b<T> of(TileEntityTypes.a<? extends T> tileentitytypes_a, Block... ablock) {
            return new TileEntityTypes.b<>(tileentitytypes_a, ImmutableSet.copyOf(ablock));
        }

        public TileEntityTypes<T> build(Type<?> type) {
            return new TileEntityTypes<>(this.factory, this.validBlocks, type);
        }
    }

    @FunctionalInterface
    private interface a<T extends TileEntity> {

        T create(BlockPosition blockposition, IBlockData iblockdata);
    }
}
