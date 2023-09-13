package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CraftingManager {

    private static final Logger b = LogManager.getLogger();
    private static int c;
    public static RegistryMaterials<MinecraftKey, IRecipe> recipes = new RegistryMaterials();

    public CraftingManager() {}

    public static boolean init() {
        try {
            a("armordye", new RecipeArmorDye());
            a("bookcloning", new RecipeBookClone());
            a("mapcloning", new RecipeMapClone());
            a("mapextending", new RecipeMapExtend());
            a("fireworks", new RecipeFireworks());
            a("repairitem", new RecipeRepair());
            a("tippedarrow", new RecipeTippedArrow());
            a("bannerduplicate", new RecipesBanner.DuplicateRecipe());
            a("banneraddpattern", new RecipesBanner.AddRecipe());
            a("shielddecoration", new RecipiesShield.Decoration());
            a("shulkerboxcoloring", new RecipeShulkerBox.Dye());
            return b();
        } catch (Throwable throwable) {
            return false;
        }
    }

    private static boolean b() {
        FileSystem filesystem = null;
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

        try {
            boolean flag;

            try {
                URL url = CraftingManager.class.getResource("/assets/.mcassetsroot");

                if (url == null) {
                    CraftingManager.b.error("Couldn\'t find .mcassetsroot");
                    flag = false;
                    return flag;
                }

                URI uri = url.toURI();
                java.nio.file.Path java_nio_file_path;

                if ("file".equals(uri.getScheme())) {
                    java_nio_file_path = Paths.get(CraftingManager.class.getResource("/assets/minecraft/recipes").toURI());
                } else {
                    if (!"jar".equals(uri.getScheme())) {
                        CraftingManager.b.error("Unsupported scheme " + uri + " trying to list all recipes");
                        boolean flag1 = false;

                        return flag1;
                    }

                    filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    java_nio_file_path = filesystem.getPath("/assets/minecraft/recipes", new String[0]);
                }

                Iterator iterator = Files.walk(java_nio_file_path, new FileVisitOption[0]).iterator();

                while (iterator.hasNext()) {
                    java.nio.file.Path java_nio_file_path1 = (java.nio.file.Path) iterator.next();

                    if ("json".equals(FilenameUtils.getExtension(java_nio_file_path1.toString()))) {
                        java.nio.file.Path java_nio_file_path2 = java_nio_file_path.relativize(java_nio_file_path1);
                        String s = FilenameUtils.removeExtension(java_nio_file_path2.toString()).replaceAll("\\\\", "/");
                        MinecraftKey minecraftkey = new MinecraftKey(s);
                        BufferedReader bufferedreader = null;

                        try {
                            boolean flag2;

                            try {
                                bufferedreader = Files.newBufferedReader(java_nio_file_path1);
                                a(s, a((JsonObject) ChatDeserializer.a(gson, (Reader) bufferedreader, JsonObject.class)));
                            } catch (JsonParseException jsonparseexception) {
                                CraftingManager.b.error("Parsing error loading recipe " + minecraftkey, jsonparseexception);
                                flag2 = false;
                                return flag2;
                            } catch (IOException ioexception) {
                                CraftingManager.b.error("Couldn\'t read recipe " + minecraftkey + " from " + java_nio_file_path1, ioexception);
                                flag2 = false;
                                return flag2;
                            }
                        } finally {
                            IOUtils.closeQuietly(bufferedreader);
                        }
                    }
                }
            } catch (IOException | URISyntaxException urisyntaxexception) {
                CraftingManager.b.error("Couldn\'t get a list of all recipe files", urisyntaxexception);
                flag = false;
                return flag;
            }
        } finally {
            IOUtils.closeQuietly(filesystem);
        }

        return true;
    }

    private static IRecipe a(JsonObject jsonobject) {
        String s = ChatDeserializer.h(jsonobject, "type");

        if ("crafting_shaped".equals(s)) {
            return ShapedRecipes.a(jsonobject);
        } else if ("crafting_shapeless".equals(s)) {
            return ShapelessRecipes.a(jsonobject);
        } else {
            throw new JsonSyntaxException("Invalid or unsupported recipe type \'" + s + "\'");
        }
    }

    public static void a(String s, IRecipe irecipe) {
        a(new MinecraftKey(s), irecipe);
    }

    public static void a(MinecraftKey minecraftkey, IRecipe irecipe) {
        if (CraftingManager.recipes.d(minecraftkey)) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + minecraftkey);
        } else {
            CraftingManager.recipes.a(CraftingManager.c++, minecraftkey, irecipe);
        }
    }

    public static ItemStack craft(InventoryCrafting inventorycrafting, World world) {
        Iterator iterator = CraftingManager.recipes.iterator();

        IRecipe irecipe;

        do {
            if (!iterator.hasNext()) {
                return ItemStack.a;
            }

            irecipe = (IRecipe) iterator.next();
        } while (!irecipe.a(inventorycrafting, world));

        return irecipe.craftItem(inventorycrafting);
    }

    @Nullable
    public static IRecipe b(InventoryCrafting inventorycrafting, World world) {
        Iterator iterator = CraftingManager.recipes.iterator();

        IRecipe irecipe;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            irecipe = (IRecipe) iterator.next();
        } while (!irecipe.a(inventorycrafting, world));

        return irecipe;
    }

    public static NonNullList<ItemStack> c(InventoryCrafting inventorycrafting, World world) {
        Iterator iterator = CraftingManager.recipes.iterator();

        while (iterator.hasNext()) {
            IRecipe irecipe = (IRecipe) iterator.next();

            if (irecipe.a(inventorycrafting, world)) {
                return irecipe.b(inventorycrafting);
            }
        }

        NonNullList nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, inventorycrafting.getItem(i));
        }

        return nonnulllist;
    }

    @Nullable
    public static IRecipe a(MinecraftKey minecraftkey) {
        return (IRecipe) CraftingManager.recipes.get(minecraftkey);
    }

    public static int a(IRecipe irecipe) {
        return CraftingManager.recipes.a((Object) irecipe);
    }

    @Nullable
    public static IRecipe a(int i) {
        return (IRecipe) CraftingManager.recipes.getId(i);
    }
}
