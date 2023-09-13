package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.level.block.SoundEffectType;

public record BlockSetType(String name, SoundEffectType soundType, SoundEffect doorClose, SoundEffect doorOpen, SoundEffect trapdoorClose, SoundEffect trapdoorOpen, SoundEffect pressurePlateClickOff, SoundEffect pressurePlateClickOn, SoundEffect buttonClickOff, SoundEffect buttonClickOn) {

    private static final Set<BlockSetType> VALUES = new ObjectArraySet();
    public static final BlockSetType IRON = register(new BlockSetType("iron", SoundEffectType.METAL, SoundEffects.IRON_DOOR_CLOSE, SoundEffects.IRON_DOOR_OPEN, SoundEffects.IRON_TRAPDOOR_CLOSE, SoundEffects.IRON_TRAPDOOR_OPEN, SoundEffects.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEffects.METAL_PRESSURE_PLATE_CLICK_ON, SoundEffects.STONE_BUTTON_CLICK_OFF, SoundEffects.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType GOLD = register(new BlockSetType("gold", SoundEffectType.METAL, SoundEffects.IRON_DOOR_CLOSE, SoundEffects.IRON_DOOR_OPEN, SoundEffects.IRON_TRAPDOOR_CLOSE, SoundEffects.IRON_TRAPDOOR_OPEN, SoundEffects.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEffects.METAL_PRESSURE_PLATE_CLICK_ON, SoundEffects.STONE_BUTTON_CLICK_OFF, SoundEffects.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType STONE = register(new BlockSetType("stone", SoundEffectType.STONE, SoundEffects.IRON_DOOR_CLOSE, SoundEffects.IRON_DOOR_OPEN, SoundEffects.IRON_TRAPDOOR_CLOSE, SoundEffects.IRON_TRAPDOOR_OPEN, SoundEffects.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEffects.STONE_PRESSURE_PLATE_CLICK_ON, SoundEffects.STONE_BUTTON_CLICK_OFF, SoundEffects.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType POLISHED_BLACKSTONE = register(new BlockSetType("polished_blackstone", SoundEffectType.STONE, SoundEffects.IRON_DOOR_CLOSE, SoundEffects.IRON_DOOR_OPEN, SoundEffects.IRON_TRAPDOOR_CLOSE, SoundEffects.IRON_TRAPDOOR_OPEN, SoundEffects.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEffects.STONE_PRESSURE_PLATE_CLICK_ON, SoundEffects.STONE_BUTTON_CLICK_OFF, SoundEffects.STONE_BUTTON_CLICK_ON));
    public static final BlockSetType OAK = register(new BlockSetType("oak"));
    public static final BlockSetType SPRUCE = register(new BlockSetType("spruce"));
    public static final BlockSetType BIRCH = register(new BlockSetType("birch"));
    public static final BlockSetType ACACIA = register(new BlockSetType("acacia"));
    public static final BlockSetType CHERRY = register(new BlockSetType("cherry", SoundEffectType.CHERRY_WOOD, SoundEffects.CHERRY_WOOD_DOOR_CLOSE, SoundEffects.CHERRY_WOOD_DOOR_OPEN, SoundEffects.CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEffects.CHERRY_WOOD_TRAPDOOR_OPEN, SoundEffects.CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEffects.CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEffects.CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEffects.CHERRY_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType JUNGLE = register(new BlockSetType("jungle"));
    public static final BlockSetType DARK_OAK = register(new BlockSetType("dark_oak"));
    public static final BlockSetType CRIMSON = register(new BlockSetType("crimson", SoundEffectType.NETHER_WOOD, SoundEffects.NETHER_WOOD_DOOR_CLOSE, SoundEffects.NETHER_WOOD_DOOR_OPEN, SoundEffects.NETHER_WOOD_TRAPDOOR_CLOSE, SoundEffects.NETHER_WOOD_TRAPDOOR_OPEN, SoundEffects.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEffects.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEffects.NETHER_WOOD_BUTTON_CLICK_OFF, SoundEffects.NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType WARPED = register(new BlockSetType("warped", SoundEffectType.NETHER_WOOD, SoundEffects.NETHER_WOOD_DOOR_CLOSE, SoundEffects.NETHER_WOOD_DOOR_OPEN, SoundEffects.NETHER_WOOD_TRAPDOOR_CLOSE, SoundEffects.NETHER_WOOD_TRAPDOOR_OPEN, SoundEffects.NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEffects.NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEffects.NETHER_WOOD_BUTTON_CLICK_OFF, SoundEffects.NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType MANGROVE = register(new BlockSetType("mangrove"));
    public static final BlockSetType BAMBOO = register(new BlockSetType("bamboo", SoundEffectType.BAMBOO_WOOD, SoundEffects.BAMBOO_WOOD_DOOR_CLOSE, SoundEffects.BAMBOO_WOOD_DOOR_OPEN, SoundEffects.BAMBOO_WOOD_TRAPDOOR_CLOSE, SoundEffects.BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEffects.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEffects.BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEffects.BAMBOO_WOOD_BUTTON_CLICK_OFF, SoundEffects.BAMBOO_WOOD_BUTTON_CLICK_ON));

    public BlockSetType(String s) {
        this(s, SoundEffectType.WOOD, SoundEffects.WOODEN_DOOR_CLOSE, SoundEffects.WOODEN_DOOR_OPEN, SoundEffects.WOODEN_TRAPDOOR_CLOSE, SoundEffects.WOODEN_TRAPDOOR_OPEN, SoundEffects.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEffects.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEffects.WOODEN_BUTTON_CLICK_OFF, SoundEffects.WOODEN_BUTTON_CLICK_ON);
    }

    private static BlockSetType register(BlockSetType blocksettype) {
        BlockSetType.VALUES.add(blocksettype);
        return blocksettype;
    }

    public static Stream<BlockSetType> values() {
        return BlockSetType.VALUES.stream();
    }
}
