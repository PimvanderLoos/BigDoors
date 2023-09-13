package net.minecraft.world.level;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Block;

public record BlockActionData(BlockPosition pos, Block block, int paramA, int paramB) {

}
