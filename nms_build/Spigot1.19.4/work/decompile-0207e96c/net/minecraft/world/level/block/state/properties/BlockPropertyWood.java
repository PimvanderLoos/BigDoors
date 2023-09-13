package net.minecraft.world.level.block.state.properties;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.level.block.SoundEffectType;

public record BlockPropertyWood(String name, BlockSetType setType, SoundEffectType soundType, SoundEffectType hangingSignSoundType, SoundEffect fenceGateClose, SoundEffect fenceGateOpen) {

    private static final Set<BlockPropertyWood> VALUES = new ObjectArraySet();
    public static final BlockPropertyWood OAK = register(new BlockPropertyWood("oak", BlockSetType.OAK));
    public static final BlockPropertyWood SPRUCE = register(new BlockPropertyWood("spruce", BlockSetType.SPRUCE));
    public static final BlockPropertyWood BIRCH = register(new BlockPropertyWood("birch", BlockSetType.BIRCH));
    public static final BlockPropertyWood ACACIA = register(new BlockPropertyWood("acacia", BlockSetType.ACACIA));
    public static final BlockPropertyWood CHERRY = register(new BlockPropertyWood("cherry", BlockSetType.CHERRY, SoundEffectType.CHERRY_WOOD, SoundEffectType.CHERRY_WOOD_HANGING_SIGN, SoundEffects.CHERRY_WOOD_FENCE_GATE_CLOSE, SoundEffects.CHERRY_WOOD_FENCE_GATE_OPEN));
    public static final BlockPropertyWood JUNGLE = register(new BlockPropertyWood("jungle", BlockSetType.JUNGLE));
    public static final BlockPropertyWood DARK_OAK = register(new BlockPropertyWood("dark_oak", BlockSetType.DARK_OAK));
    public static final BlockPropertyWood CRIMSON = register(new BlockPropertyWood("crimson", BlockSetType.CRIMSON, SoundEffectType.NETHER_WOOD, SoundEffectType.NETHER_WOOD_HANGING_SIGN, SoundEffects.NETHER_WOOD_FENCE_GATE_CLOSE, SoundEffects.NETHER_WOOD_FENCE_GATE_OPEN));
    public static final BlockPropertyWood WARPED = register(new BlockPropertyWood("warped", BlockSetType.WARPED, SoundEffectType.NETHER_WOOD, SoundEffectType.NETHER_WOOD_HANGING_SIGN, SoundEffects.NETHER_WOOD_FENCE_GATE_CLOSE, SoundEffects.NETHER_WOOD_FENCE_GATE_OPEN));
    public static final BlockPropertyWood MANGROVE = register(new BlockPropertyWood("mangrove", BlockSetType.MANGROVE));
    public static final BlockPropertyWood BAMBOO = register(new BlockPropertyWood("bamboo", BlockSetType.BAMBOO, SoundEffectType.BAMBOO_WOOD, SoundEffectType.BAMBOO_WOOD_HANGING_SIGN, SoundEffects.BAMBOO_WOOD_FENCE_GATE_CLOSE, SoundEffects.BAMBOO_WOOD_FENCE_GATE_OPEN));

    public BlockPropertyWood(String s, BlockSetType blocksettype) {
        this(s, blocksettype, SoundEffectType.WOOD, SoundEffectType.HANGING_SIGN, SoundEffects.FENCE_GATE_CLOSE, SoundEffects.FENCE_GATE_OPEN);
    }

    private static BlockPropertyWood register(BlockPropertyWood blockpropertywood) {
        BlockPropertyWood.VALUES.add(blockpropertywood);
        return blockpropertywood;
    }

    public static Stream<BlockPropertyWood> values() {
        return BlockPropertyWood.VALUES.stream();
    }
}
