package net.minecraft.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.SystemUtils;
import net.minecraft.util.INamable;

public enum BlockPropertyJigsawOrientation implements INamable {

    DOWN_EAST("down_east", EnumDirection.DOWN, EnumDirection.EAST), DOWN_NORTH("down_north", EnumDirection.DOWN, EnumDirection.NORTH), DOWN_SOUTH("down_south", EnumDirection.DOWN, EnumDirection.SOUTH), DOWN_WEST("down_west", EnumDirection.DOWN, EnumDirection.WEST), UP_EAST("up_east", EnumDirection.UP, EnumDirection.EAST), UP_NORTH("up_north", EnumDirection.UP, EnumDirection.NORTH), UP_SOUTH("up_south", EnumDirection.UP, EnumDirection.SOUTH), UP_WEST("up_west", EnumDirection.UP, EnumDirection.WEST), WEST_UP("west_up", EnumDirection.WEST, EnumDirection.UP), EAST_UP("east_up", EnumDirection.EAST, EnumDirection.UP), NORTH_UP("north_up", EnumDirection.NORTH, EnumDirection.UP), SOUTH_UP("south_up", EnumDirection.SOUTH, EnumDirection.UP);

    private static final Int2ObjectMap<BlockPropertyJigsawOrientation> LOOKUP_TOP_FRONT = (Int2ObjectMap) SystemUtils.make(new Int2ObjectOpenHashMap(values().length), (int2objectopenhashmap) -> {
        BlockPropertyJigsawOrientation[] ablockpropertyjigsaworientation = values();
        int i = ablockpropertyjigsaworientation.length;

        for (int j = 0; j < i; ++j) {
            BlockPropertyJigsawOrientation blockpropertyjigsaworientation = ablockpropertyjigsaworientation[j];

            int2objectopenhashmap.put(lookupKey(blockpropertyjigsaworientation.front, blockpropertyjigsaworientation.top), blockpropertyjigsaworientation);
        }

    });
    private final String name;
    private final EnumDirection top;
    private final EnumDirection front;

    private static int lookupKey(EnumDirection enumdirection, EnumDirection enumdirection1) {
        return enumdirection1.ordinal() << 3 | enumdirection.ordinal();
    }

    private BlockPropertyJigsawOrientation(String s, EnumDirection enumdirection, EnumDirection enumdirection1) {
        this.name = s;
        this.front = enumdirection;
        this.top = enumdirection1;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static BlockPropertyJigsawOrientation fromFrontAndTop(EnumDirection enumdirection, EnumDirection enumdirection1) {
        int i = lookupKey(enumdirection, enumdirection1);

        return (BlockPropertyJigsawOrientation) BlockPropertyJigsawOrientation.LOOKUP_TOP_FRONT.get(i);
    }

    public EnumDirection front() {
        return this.front;
    }

    public EnumDirection top() {
        return this.top;
    }
}
