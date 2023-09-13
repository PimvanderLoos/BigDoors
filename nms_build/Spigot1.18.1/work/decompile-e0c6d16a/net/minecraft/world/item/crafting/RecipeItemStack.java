package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.player.AutoRecipeStackManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IMaterial;

public final class RecipeItemStack implements Predicate<ItemStack> {

    public static final RecipeItemStack EMPTY = new RecipeItemStack(Stream.empty());
    private final RecipeItemStack.Provider[] values;
    @Nullable
    public ItemStack[] itemStacks;
    @Nullable
    private IntList stackingIds;

    public RecipeItemStack(Stream<? extends RecipeItemStack.Provider> stream) {
        this.values = (RecipeItemStack.Provider[]) stream.toArray((i) -> {
            return new RecipeItemStack.Provider[i];
        });
    }

    public ItemStack[] getItems() {
        this.dissolve();
        return this.itemStacks;
    }

    public void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = (ItemStack[]) Arrays.stream(this.values).flatMap((recipeitemstack_provider) -> {
                return recipeitemstack_provider.getItems().stream();
            }).distinct().toArray((i) -> {
                return new ItemStack[i];
            });
        }

    }

    public boolean test(@Nullable ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        } else {
            this.dissolve();
            if (this.itemStacks.length == 0) {
                return itemstack.isEmpty();
            } else {
                ItemStack[] aitemstack = this.itemStacks;
                int i = aitemstack.length;

                for (int j = 0; j < i; ++j) {
                    ItemStack itemstack1 = aitemstack[j];

                    if (itemstack1.is(itemstack.getItem())) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.dissolve();
            this.stackingIds = new IntArrayList(this.itemStacks.length);
            ItemStack[] aitemstack = this.itemStacks;
            int i = aitemstack.length;

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = aitemstack[j];

                this.stackingIds.add(AutoRecipeStackManager.getStackingIndex(itemstack));
            }

            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.stackingIds;
    }

    public void toNetwork(PacketDataSerializer packetdataserializer) {
        this.dissolve();
        packetdataserializer.writeCollection(Arrays.asList(this.itemStacks), PacketDataSerializer::writeItem);
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();
            RecipeItemStack.Provider[] arecipeitemstack_provider = this.values;
            int i = arecipeitemstack_provider.length;

            for (int j = 0; j < i; ++j) {
                RecipeItemStack.Provider recipeitemstack_provider = arecipeitemstack_provider[j];

                jsonarray.add(recipeitemstack_provider.serialize());
            }

            return jsonarray;
        }
    }

    public boolean isEmpty() {
        return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
    }

    private static RecipeItemStack fromValues(Stream<? extends RecipeItemStack.Provider> stream) {
        RecipeItemStack recipeitemstack = new RecipeItemStack(stream);

        return recipeitemstack.values.length == 0 ? RecipeItemStack.EMPTY : recipeitemstack;
    }

    public static RecipeItemStack of() {
        return RecipeItemStack.EMPTY;
    }

    public static RecipeItemStack of(IMaterial... aimaterial) {
        return of(Arrays.stream(aimaterial).map(ItemStack::new));
    }

    public static RecipeItemStack of(ItemStack... aitemstack) {
        return of(Arrays.stream(aitemstack));
    }

    public static RecipeItemStack of(Stream<ItemStack> stream) {
        return fromValues(stream.filter((itemstack) -> {
            return !itemstack.isEmpty();
        }).map(RecipeItemStack.StackProvider::new));
    }

    public static RecipeItemStack of(Tag<Item> tag) {
        return fromValues(Stream.of(new RecipeItemStack.b(tag)));
    }

    public static RecipeItemStack fromNetwork(PacketDataSerializer packetdataserializer) {
        return fromValues(packetdataserializer.readList(PacketDataSerializer::readItem).stream().map(RecipeItemStack.StackProvider::new));
    }

    public static RecipeItemStack fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (jsonelement.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(jsonelement.getAsJsonObject())));
            } else if (jsonelement.isJsonArray()) {
                JsonArray jsonarray = jsonelement.getAsJsonArray();

                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((jsonelement1) -> {
                        return valueFromJson(ChatDeserializer.convertToJsonObject(jsonelement1, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    private static RecipeItemStack.Provider valueFromJson(JsonObject jsonobject) {
        if (jsonobject.has("item") && jsonobject.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else if (jsonobject.has("item")) {
            Item item = ShapedRecipes.itemFromJson(jsonobject);

            return new RecipeItemStack.StackProvider(new ItemStack(item));
        } else if (jsonobject.has("tag")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "tag"));
            Tag<Item> tag = TagsInstance.getInstance().getTagOrThrow(IRegistry.ITEM_REGISTRY, minecraftkey, (minecraftkey1) -> {
                return new JsonSyntaxException("Unknown item tag '" + minecraftkey1 + "'");
            });

            return new RecipeItemStack.b(tag);
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    public interface Provider {

        Collection<ItemStack> getItems();

        JsonObject serialize();
    }

    private static class b implements RecipeItemStack.Provider {

        private final Tag<Item> tag;

        b(Tag<Item> tag) {
            this.tag = tag;
        }

        @Override
        public Collection<ItemStack> getItems() {
            List<ItemStack> list = Lists.newArrayList();
            Iterator iterator = this.tag.getValues().iterator();

            while (iterator.hasNext()) {
                Item item = (Item) iterator.next();

                list.add(new ItemStack(item));
            }

            return list;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("tag", TagsInstance.getInstance().getIdOrThrow(IRegistry.ITEM_REGISTRY, this.tag, () -> {
                return new IllegalStateException("Unknown item tag");
            }).toString());
            return jsonobject;
        }
    }

    public static class StackProvider implements RecipeItemStack.Provider {

        private final ItemStack item;

        public StackProvider(ItemStack itemstack) {
            this.item = itemstack;
        }

        @Override
        public Collection<ItemStack> getItems() {
            return Collections.singleton(this.item);
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();

            jsonobject.addProperty("item", IRegistry.ITEM.getKey(this.item.getItem()).toString());
            return jsonobject;
        }
    }
}
