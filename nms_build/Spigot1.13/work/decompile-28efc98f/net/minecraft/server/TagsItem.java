package net.minecraft.server;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class TagsItem {

    private static Tags<Item> E = new Tags((minecraftkey) -> {
        return false;
    }, (minecraftkey) -> {
        return null;
    }, "", false, "");
    private static int F;
    public static final Tag<Item> a = a("wool");
    public static final Tag<Item> b = a("planks");
    public static final Tag<Item> c = a("stone_bricks");
    public static final Tag<Item> d = a("wooden_buttons");
    public static final Tag<Item> e = a("buttons");
    public static final Tag<Item> f = a("carpets");
    public static final Tag<Item> g = a("wooden_doors");
    public static final Tag<Item> h = a("wooden_stairs");
    public static final Tag<Item> i = a("wooden_slabs");
    public static final Tag<Item> j = a("wooden_pressure_plates");
    public static final Tag<Item> k = a("wooden_trapdoors");
    public static final Tag<Item> l = a("doors");
    public static final Tag<Item> m = a("saplings");
    public static final Tag<Item> n = a("logs");
    public static final Tag<Item> o = a("dark_oak_logs");
    public static final Tag<Item> p = a("oak_logs");
    public static final Tag<Item> q = a("birch_logs");
    public static final Tag<Item> r = a("acacia_logs");
    public static final Tag<Item> s = a("jungle_logs");
    public static final Tag<Item> t = a("spruce_logs");
    public static final Tag<Item> u = a("banners");
    public static final Tag<Item> v = a("sand");
    public static final Tag<Item> w = a("stairs");
    public static final Tag<Item> x = a("slabs");
    public static final Tag<Item> y = a("anvil");
    public static final Tag<Item> z = a("rails");
    public static final Tag<Item> A = a("leaves");
    public static final Tag<Item> B = a("trapdoors");
    public static final Tag<Item> C = a("boats");
    public static final Tag<Item> D = a("fishes");

    public static void a(Tags<Item> tags) {
        TagsItem.E = tags;
        ++TagsItem.F;
    }

    public static Tags<Item> a() {
        return TagsItem.E;
    }

    private static Tag<Item> a(String s) {
        return new TagsItem.a(new MinecraftKey(s));
    }

    public static class a extends Tag<Item> {

        private int a = -1;
        private Tag<Item> b;

        public a(MinecraftKey minecraftkey) {
            super(minecraftkey);
        }

        public boolean a(Item item) {
            if (this.a != TagsItem.F) {
                this.b = TagsItem.E.b(this.c());
                this.a = TagsItem.F;
            }

            return this.b.isTagged(item);
        }

        public Collection<Item> a() {
            if (this.a != TagsItem.F) {
                this.b = TagsItem.E.b(this.c());
                this.a = TagsItem.F;
            }

            return this.b.a();
        }

        public Collection<Tag.b<Item>> b() {
            if (this.a != TagsItem.F) {
                this.b = TagsItem.E.b(this.c());
                this.a = TagsItem.F;
            }

            return this.b.b();
        }
    }
}
