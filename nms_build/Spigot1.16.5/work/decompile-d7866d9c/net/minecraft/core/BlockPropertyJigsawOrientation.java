package net.minecraft.core;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.INamable;

public enum BlockPropertyJigsawOrientation implements INamable {

    DOWN_EAST("down_east", EnumDirection.DOWN, EnumDirection.EAST), DOWN_NORTH("down_north", EnumDirection.DOWN, EnumDirection.NORTH), DOWN_SOUTH("down_south", EnumDirection.DOWN, EnumDirection.SOUTH), DOWN_WEST("down_west", EnumDirection.DOWN, EnumDirection.WEST), UP_EAST("up_east", EnumDirection.UP, EnumDirection.EAST), UP_NORTH("up_north", EnumDirection.UP, EnumDirection.NORTH), UP_SOUTH("up_south", EnumDirection.UP, EnumDirection.SOUTH), UP_WEST("up_west", EnumDirection.UP, EnumDirection.WEST), WEST_UP("west_up", EnumDirection.WEST, EnumDirection.UP), EAST_UP("east_up", EnumDirection.EAST, EnumDirection.UP), NORTH_UP("north_up", EnumDirection.NORTH, EnumDirection.UP), SOUTH_UP("south_up", EnumDirection.SOUTH, EnumDirection.UP);

    private static final Int2ObjectMap<BlockPropertyJigsawOrientation> m = new Int2ObjectOpenHashMap(values().length);
    private final String n;
    private final EnumDirection o;
    private final EnumDirection p;

    private static int b(EnumDirection enumdirection, EnumDirection enumdirection1) {
        return enumdirection.ordinal() << 3 | enumdirection1.ordinal();
    }

    private BlockPropertyJigsawOrientation(String s, EnumDirection enumdirection, EnumDirection enumdirection1) {
        this.n = s;
        this.p = enumdirection;
        this.o = enumdirection1;
    }

    @Override
    public String getName() {
        return this.n;
    }

    public static BlockPropertyJigsawOrientation a(EnumDirection enumdirection, EnumDirection enumdirection1) {
        int i = b(enumdirection1, enumdirection);

        return (BlockPropertyJigsawOrientation) BlockPropertyJigsawOrientation.m.get(i);
    }

    public EnumDirection b() {
        return this.p;
    }

    public EnumDirection c() {
        return this.o;
    }

    static {
        BlockPropertyJigsawOrientation[] ablockpropertyjigsaworientation = values();
        int i = ablockpropertyjigsaworientation.length;

        for (int j = 0; j < i; ++j) {
            BlockPropertyJigsawOrientation blockpropertyjigsaworientation = ablockpropertyjigsaworientation[j];

            BlockPropertyJigsawOrientation.m.put(b(blockpropertyjigsaworientation.o, blockpropertyjigsaworientation.p), blockpropertyjigsaworientation);
        }

    }
}
