package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;

public interface RecipeSerializer<T extends IRecipe<?>> {

    RecipeSerializer<ShapedRecipes> SHAPED_RECIPE = register("crafting_shaped", new ShapedRecipes.a());
    RecipeSerializer<ShapelessRecipes> SHAPELESS_RECIPE = register("crafting_shapeless", new ShapelessRecipes.a());
    RecipeSerializer<RecipeArmorDye> ARMOR_DYE = register("crafting_special_armordye", new SimpleCraftingRecipeSerializer<>(RecipeArmorDye::new));
    RecipeSerializer<RecipeBookClone> BOOK_CLONING = register("crafting_special_bookcloning", new SimpleCraftingRecipeSerializer<>(RecipeBookClone::new));
    RecipeSerializer<RecipeMapClone> MAP_CLONING = register("crafting_special_mapcloning", new SimpleCraftingRecipeSerializer<>(RecipeMapClone::new));
    RecipeSerializer<RecipeMapExtend> MAP_EXTENDING = register("crafting_special_mapextending", new SimpleCraftingRecipeSerializer<>(RecipeMapExtend::new));
    RecipeSerializer<RecipeFireworks> FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SimpleCraftingRecipeSerializer<>(RecipeFireworks::new));
    RecipeSerializer<RecipeFireworksStar> FIREWORK_STAR = register("crafting_special_firework_star", new SimpleCraftingRecipeSerializer<>(RecipeFireworksStar::new));
    RecipeSerializer<RecipeFireworksFade> FIREWORK_STAR_FADE = register("crafting_special_firework_star_fade", new SimpleCraftingRecipeSerializer<>(RecipeFireworksFade::new));
    RecipeSerializer<RecipeTippedArrow> TIPPED_ARROW = register("crafting_special_tippedarrow", new SimpleCraftingRecipeSerializer<>(RecipeTippedArrow::new));
    RecipeSerializer<RecipeBannerDuplicate> BANNER_DUPLICATE = register("crafting_special_bannerduplicate", new SimpleCraftingRecipeSerializer<>(RecipeBannerDuplicate::new));
    RecipeSerializer<RecipiesShield> SHIELD_DECORATION = register("crafting_special_shielddecoration", new SimpleCraftingRecipeSerializer<>(RecipiesShield::new));
    RecipeSerializer<RecipeShulkerBox> SHULKER_BOX_COLORING = register("crafting_special_shulkerboxcoloring", new SimpleCraftingRecipeSerializer<>(RecipeShulkerBox::new));
    RecipeSerializer<RecipeSuspiciousStew> SUSPICIOUS_STEW = register("crafting_special_suspiciousstew", new SimpleCraftingRecipeSerializer<>(RecipeSuspiciousStew::new));
    RecipeSerializer<RecipeRepair> REPAIR_ITEM = register("crafting_special_repairitem", new SimpleCraftingRecipeSerializer<>(RecipeRepair::new));
    RecipeSerializer<FurnaceRecipe> SMELTING_RECIPE = register("smelting", new RecipeSerializerCooking<>(FurnaceRecipe::new, 200));
    RecipeSerializer<RecipeBlasting> BLASTING_RECIPE = register("blasting", new RecipeSerializerCooking<>(RecipeBlasting::new, 100));
    RecipeSerializer<RecipeSmoking> SMOKING_RECIPE = register("smoking", new RecipeSerializerCooking<>(RecipeSmoking::new, 100));
    RecipeSerializer<RecipeCampfire> CAMPFIRE_COOKING_RECIPE = register("campfire_cooking", new RecipeSerializerCooking<>(RecipeCampfire::new, 100));
    RecipeSerializer<RecipeStonecutting> STONECUTTER = register("stonecutting", new RecipeSingleItem.a<>(RecipeStonecutting::new));
    RecipeSerializer<RecipeSmithing> SMITHING = register("smithing", new RecipeSmithing.a());

    T fromJson(MinecraftKey minecraftkey, JsonObject jsonobject);

    T fromNetwork(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer);

    void toNetwork(PacketDataSerializer packetdataserializer, T t0);

    static <S extends RecipeSerializer<T>, T extends IRecipe<?>> S register(String s, S s0) {
        return (RecipeSerializer) IRegistry.register(BuiltInRegistries.RECIPE_SERIALIZER, s, s0);
    }
}
