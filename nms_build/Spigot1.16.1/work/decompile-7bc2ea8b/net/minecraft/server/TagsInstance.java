package net.minecraft.server;

public class TagsInstance {

    private static volatile TagsInstance a = new TagsInstance(TagsBlock.b(), TagsItem.b(), TagsFluid.b(), TagsEntity.b());
    private final Tags<Block> b;
    private final Tags<Item> c;
    private final Tags<FluidType> d;
    private final Tags<EntityTypes<?>> e;

    private TagsInstance(Tags<Block> tags, Tags<Item> tags1, Tags<FluidType> tags2, Tags<EntityTypes<?>> tags3) {
        this.b = tags;
        this.c = tags1;
        this.d = tags2;
        this.e = tags3;
    }

    public Tags<Block> a() {
        return this.b;
    }

    public Tags<Item> b() {
        return this.c;
    }

    public Tags<FluidType> c() {
        return this.d;
    }

    public Tags<EntityTypes<?>> d() {
        return this.e;
    }

    public static TagsInstance e() {
        return TagsInstance.a;
    }

    public static void a(Tags<Block> tags, Tags<Item> tags1, Tags<FluidType> tags2, Tags<EntityTypes<?>> tags3) {
        TagsInstance.a = new TagsInstance(tags, tags1, tags2, tags3);
    }
}
