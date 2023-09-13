package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugReportTagsItem extends DebugReportTags<Item> {

    private static final Logger e = LogManager.getLogger();

    public DebugReportTagsItem(DebugReportGenerator debugreportgenerator) {
        super(debugreportgenerator, Item.REGISTRY);
    }

    protected void b() {
        this.a(TagsBlock.a, TagsItem.a);
        this.a(TagsBlock.b, TagsItem.b);
        this.a(TagsBlock.c, TagsItem.c);
        this.a(TagsBlock.d, TagsItem.d);
        this.a(TagsBlock.e, TagsItem.e);
        this.a(TagsBlock.f, TagsItem.f);
        this.a(TagsBlock.g, TagsItem.g);
        this.a(TagsBlock.h, TagsItem.h);
        this.a(TagsBlock.i, TagsItem.i);
        this.a(TagsBlock.j, TagsItem.j);
        this.a(TagsBlock.l, TagsItem.l);
        this.a(TagsBlock.m, TagsItem.m);
        this.a(TagsBlock.p, TagsItem.p);
        this.a(TagsBlock.o, TagsItem.o);
        this.a(TagsBlock.q, TagsItem.q);
        this.a(TagsBlock.r, TagsItem.r);
        this.a(TagsBlock.t, TagsItem.t);
        this.a(TagsBlock.s, TagsItem.s);
        this.a(TagsBlock.n, TagsItem.n);
        this.a(TagsBlock.v, TagsItem.v);
        this.a(TagsBlock.x, TagsItem.x);
        this.a(TagsBlock.w, TagsItem.w);
        this.a(TagsBlock.y, TagsItem.y);
        this.a(TagsBlock.z, TagsItem.z);
        this.a(TagsBlock.D, TagsItem.A);
        this.a(TagsBlock.k, TagsItem.k);
        this.a(TagsBlock.E, TagsItem.B);
        this.a(TagsItem.u).a((Object[]) (new Item[] { Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER}));
        this.a(TagsItem.C).a((Object[]) (new Item[] { Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT}));
        this.a(TagsItem.D).a((Object[]) (new Item[] { Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH}));
    }

    protected void a(Tag<Block> tag, Tag<Item> tag1) {
        Tag.a tag_a = this.a(tag1);
        Iterator iterator = tag.b().iterator();

        while (iterator.hasNext()) {
            Tag.b tag_b = (Tag.b) iterator.next();
            Tag.b tag_b1 = this.a(tag_b);

            tag_a.a(tag_b1);
        }

    }

    private Tag.b<Item> a(Tag.b<Block> tag_b) {
        if (tag_b instanceof Tag.c) {
            return new Tag.c(((Tag.c) tag_b).a());
        } else if (tag_b instanceof Tag.d) {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator = ((Tag.d) tag_b).a().iterator();

            while (iterator.hasNext()) {
                Block block = (Block) iterator.next();
                Item item = block.getItem();

                if (item == Items.AIR) {
                    DebugReportTagsItem.e.warn("Itemless block copied to item tag: {}", Block.REGISTRY.b(block));
                } else {
                    arraylist.add(item);
                }
            }

            return new Tag.d(arraylist);
        } else {
            throw new UnsupportedOperationException("Unknown tag entry " + tag_b);
        }
    }

    protected java.nio.file.Path a(MinecraftKey minecraftkey) {
        return this.b.b().resolve("data/" + minecraftkey.b() + "/tags/items/" + minecraftkey.getKey() + ".json");
    }

    public String a() {
        return "Item Tags";
    }

    protected void a(Tags<Item> tags) {
        TagsItem.a(tags);
    }
}
