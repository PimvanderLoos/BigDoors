package net.minecraft.stats;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ResourceKeyInvalidException;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.protocol.game.PacketPlayOutRecipes;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.item.crafting.IRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeBookServer extends RecipeBook {

    public static final String RECIPE_BOOK_TAG = "recipeBook";
    private static final Logger LOGGER = LogManager.getLogger();

    public RecipeBookServer() {}

    public int addRecipes(Collection<IRecipe<?>> collection, EntityPlayer entityplayer) {
        List<MinecraftKey> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            IRecipe<?> irecipe = (IRecipe) iterator.next();
            MinecraftKey minecraftkey = irecipe.getId();

            if (!this.known.contains(minecraftkey) && !irecipe.isSpecial()) {
                this.add(minecraftkey);
                this.addHighlight(minecraftkey);
                list.add(minecraftkey);
                CriterionTriggers.RECIPE_UNLOCKED.trigger(entityplayer, irecipe);
                ++i;
            }
        }

        this.sendRecipes(PacketPlayOutRecipes.Action.ADD, entityplayer, list);
        return i;
    }

    public int removeRecipes(Collection<IRecipe<?>> collection, EntityPlayer entityplayer) {
        List<MinecraftKey> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            IRecipe<?> irecipe = (IRecipe) iterator.next();
            MinecraftKey minecraftkey = irecipe.getId();

            if (this.known.contains(minecraftkey)) {
                this.remove(minecraftkey);
                list.add(minecraftkey);
                ++i;
            }
        }

        this.sendRecipes(PacketPlayOutRecipes.Action.REMOVE, entityplayer, list);
        return i;
    }

    private void sendRecipes(PacketPlayOutRecipes.Action packetplayoutrecipes_action, EntityPlayer entityplayer, List<MinecraftKey> list) {
        entityplayer.connection.send(new PacketPlayOutRecipes(packetplayoutrecipes_action, list, Collections.emptyList(), this.getBookSettings()));
    }

    public NBTTagCompound toNbt() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.getBookSettings().write(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.known.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();

            nbttaglist.add(NBTTagString.valueOf(minecraftkey.toString()));
        }

        nbttagcompound.put("recipes", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.highlight.iterator();

        while (iterator1.hasNext()) {
            MinecraftKey minecraftkey1 = (MinecraftKey) iterator1.next();

            nbttaglist1.add(NBTTagString.valueOf(minecraftkey1.toString()));
        }

        nbttagcompound.put("toBeDisplayed", nbttaglist1);
        return nbttagcompound;
    }

    public void fromNbt(NBTTagCompound nbttagcompound, CraftingManager craftingmanager) {
        this.setBookSettings(RecipeBookSettings.read(nbttagcompound));
        NBTTagList nbttaglist = nbttagcompound.getList("recipes", 8);

        this.loadRecipes(nbttaglist, this::add, craftingmanager);
        NBTTagList nbttaglist1 = nbttagcompound.getList("toBeDisplayed", 8);

        this.loadRecipes(nbttaglist1, this::addHighlight, craftingmanager);
    }

    private void loadRecipes(NBTTagList nbttaglist, Consumer<IRecipe<?>> consumer, CraftingManager craftingmanager) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            String s = nbttaglist.getString(i);

            try {
                MinecraftKey minecraftkey = new MinecraftKey(s);
                Optional<? extends IRecipe<?>> optional = craftingmanager.byKey(minecraftkey);

                if (!optional.isPresent()) {
                    RecipeBookServer.LOGGER.error("Tried to load unrecognized recipe: {} removed now.", minecraftkey);
                } else {
                    consumer.accept((IRecipe) optional.get());
                }
            } catch (ResourceKeyInvalidException resourcekeyinvalidexception) {
                RecipeBookServer.LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", s);
            }
        }

    }

    public void sendInitialRecipeBook(EntityPlayer entityplayer) {
        entityplayer.connection.send(new PacketPlayOutRecipes(PacketPlayOutRecipes.Action.INIT, this.known, this.highlight, this.getBookSettings()));
    }
}
