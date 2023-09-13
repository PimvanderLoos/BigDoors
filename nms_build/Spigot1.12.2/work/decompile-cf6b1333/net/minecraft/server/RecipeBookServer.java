package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeBookServer extends RecipeBook {

    private static final Logger e = LogManager.getLogger();

    public RecipeBookServer() {}

    public void a(List<IRecipe> list, EntityPlayer entityplayer) {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();

            if (!this.a.get(d(irecipe)) && !irecipe.c()) {
                this.a(irecipe);
                this.g(irecipe);
                arraylist.add(irecipe);
                CriterionTriggers.f.a(entityplayer, irecipe);
            }
        }

        this.a(PacketPlayOutRecipes.Action.ADD, entityplayer, arraylist);
    }

    public void b(List<IRecipe> list, EntityPlayer entityplayer) {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();

            if (this.a.get(d(irecipe))) {
                this.c(irecipe);
                arraylist.add(irecipe);
            }
        }

        this.a(PacketPlayOutRecipes.Action.REMOVE, entityplayer, arraylist);
    }

    private void a(PacketPlayOutRecipes.Action packetplayoutrecipes_action, EntityPlayer entityplayer, List<IRecipe> list) {
        entityplayer.playerConnection.sendPacket(new PacketPlayOutRecipes(packetplayoutrecipes_action, list, Collections.emptyList(), this.c, this.d));
    }

    public NBTTagCompound c() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setBoolean("isGuiOpen", this.c);
        nbttagcompound.setBoolean("isFilteringCraftable", this.d);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.d().iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();

            nbttaglist.add(new NBTTagString(((MinecraftKey) CraftingManager.recipes.b(irecipe)).toString()));
        }

        nbttagcompound.set("recipes", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.e().iterator();

        while (iterator1.hasNext()) {
            IRecipe irecipe1 = (IRecipe) iterator1.next();

            nbttaglist1.add(new NBTTagString(((MinecraftKey) CraftingManager.recipes.b(irecipe1)).toString()));
        }

        nbttagcompound.set("toBeDisplayed", nbttaglist1);
        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.c = nbttagcompound.getBoolean("isGuiOpen");
        this.d = nbttagcompound.getBoolean("isFilteringCraftable");
        NBTTagList nbttaglist = nbttagcompound.getList("recipes", 8);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            MinecraftKey minecraftkey = new MinecraftKey(nbttaglist.getString(i));
            IRecipe irecipe = CraftingManager.a(minecraftkey);

            if (irecipe == null) {
                RecipeBookServer.e.info("Tried to load unrecognized recipe: {} removed now.", minecraftkey);
            } else {
                this.a(irecipe);
            }
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("toBeDisplayed", 8);

        for (int j = 0; j < nbttaglist1.size(); ++j) {
            MinecraftKey minecraftkey1 = new MinecraftKey(nbttaglist1.getString(j));
            IRecipe irecipe1 = CraftingManager.a(minecraftkey1);

            if (irecipe1 == null) {
                RecipeBookServer.e.info("Tried to load unrecognized recipe: {} removed now.", minecraftkey1);
            } else {
                this.g(irecipe1);
            }
        }

    }

    private List<IRecipe> d() {
        ArrayList arraylist = Lists.newArrayList();

        for (int i = this.a.nextSetBit(0); i >= 0; i = this.a.nextSetBit(i + 1)) {
            arraylist.add(CraftingManager.recipes.getId(i));
        }

        return arraylist;
    }

    private List<IRecipe> e() {
        ArrayList arraylist = Lists.newArrayList();

        for (int i = this.b.nextSetBit(0); i >= 0; i = this.b.nextSetBit(i + 1)) {
            arraylist.add(CraftingManager.recipes.getId(i));
        }

        return arraylist;
    }

    public void a(EntityPlayer entityplayer) {
        entityplayer.playerConnection.sendPacket(new PacketPlayOutRecipes(PacketPlayOutRecipes.Action.INIT, this.d(), this.e(), this.c, this.d));
    }
}
