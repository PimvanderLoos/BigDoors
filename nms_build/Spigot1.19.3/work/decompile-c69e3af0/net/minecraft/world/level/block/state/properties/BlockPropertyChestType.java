package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.INamable;

public enum BlockPropertyChestType implements INamable {

    SINGLE("single"), LEFT("left"), RIGHT("right");

    private final String name;

    private BlockPropertyChestType(String s) {
        this.name = s;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public BlockPropertyChestType getOpposite() {
        BlockPropertyChestType blockpropertychesttype;

        switch (this) {
            case SINGLE:
                blockpropertychesttype = BlockPropertyChestType.SINGLE;
                break;
            case LEFT:
                blockpropertychesttype = BlockPropertyChestType.RIGHT;
                break;
            case RIGHT:
                blockpropertychesttype = BlockPropertyChestType.LEFT;
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return blockpropertychesttype;
    }
}
