package net.minecraft.server;

import com.google.common.base.Predicate;

public abstract class BlockFacingHorizontal extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", (Predicate) EnumDirection.EnumDirectionLimit.HORIZONTAL);

    protected BlockFacingHorizontal(Material material) {
        super(material);
    }

    protected BlockFacingHorizontal(Material material, MaterialMapColor materialmapcolor) {
        super(material, materialmapcolor);
    }
}
