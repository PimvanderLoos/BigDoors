package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.resources.MinecraftKey;

public interface RecipeSerializer<T extends IRecipe<?>> {

    RecipeSerializer<ShapedRecipes> SHAPED_RECIPE = a("crafting_shaped", (RecipeSerializer) (new ShapedRecipes.a()));
    RecipeSerializer<ShapelessRecipes> SHAPELESS_RECIPE = a("crafting_shapeless", (RecipeSerializer) (new ShapelessRecipes.a()));
    RecipeSerializerComplex<RecipeArmorDye> ARMOR_DYE = (RecipeSerializerComplex) a("crafting_special_armordye", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeArmorDye::new)));
    RecipeSerializerComplex<RecipeBookClone> BOOK_CLONING = (RecipeSerializerComplex) a("crafting_special_bookcloning", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeBookClone::new)));
    RecipeSerializerComplex<RecipeMapClone> MAP_CLONING = (RecipeSerializerComplex) a("crafting_special_mapcloning", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeMapClone::new)));
    RecipeSerializerComplex<RecipeMapExtend> MAP_EXTENDING = (RecipeSerializerComplex) a("crafting_special_mapextending", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeMapExtend::new)));
    RecipeSerializerComplex<RecipeFireworks> FIREWORK_ROCKET = (RecipeSerializerComplex) a("crafting_special_firework_rocket", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeFireworks::new)));
    RecipeSerializerComplex<RecipeFireworksStar> FIREWORK_STAR = (RecipeSerializerComplex) a("crafting_special_firework_star", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeFireworksStar::new)));
    RecipeSerializerComplex<RecipeFireworksFade> FIREWORK_STAR_FADE = (RecipeSerializerComplex) a("crafting_special_firework_star_fade", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeFireworksFade::new)));
    RecipeSerializerComplex<RecipeTippedArrow> TIPPED_ARROW = (RecipeSerializerComplex) a("crafting_special_tippedarrow", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeTippedArrow::new)));
    RecipeSerializerComplex<RecipeBannerDuplicate> BANNER_DUPLICATE = (RecipeSerializerComplex) a("crafting_special_bannerduplicate", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeBannerDuplicate::new)));
    RecipeSerializerComplex<RecipiesShield> SHIELD_DECORATION = (RecipeSerializerComplex) a("crafting_special_shielddecoration", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipiesShield::new)));
    RecipeSerializerComplex<RecipeShulkerBox> SHULKER_BOX_COLORING = (RecipeSerializerComplex) a("crafting_special_shulkerboxcoloring", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeShulkerBox::new)));
    RecipeSerializerComplex<RecipeSuspiciousStew> SUSPICIOUS_STEW = (RecipeSerializerComplex) a("crafting_special_suspiciousstew", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeSuspiciousStew::new)));
    RecipeSerializerComplex<RecipeRepair> REPAIR_ITEM = (RecipeSerializerComplex) a("crafting_special_repairitem", (RecipeSerializer) (new RecipeSerializerComplex<>(RecipeRepair::new)));
    RecipeSerializerCooking<FurnaceRecipe> SMELTING_RECIPE = (RecipeSerializerCooking) a("smelting", (RecipeSerializer) (new RecipeSerializerCooking<>(FurnaceRecipe::new, 200)));
    RecipeSerializerCooking<RecipeBlasting> BLASTING_RECIPE = (RecipeSerializerCooking) a("blasting", (RecipeSerializer) (new RecipeSerializerCooking<>(RecipeBlasting::new, 100)));
    RecipeSerializerCooking<RecipeSmoking> SMOKING_RECIPE = (RecipeSerializerCooking) a("smoking", (RecipeSerializer) (new RecipeSerializerCooking<>(RecipeSmoking::new, 100)));
    RecipeSerializerCooking<RecipeCampfire> CAMPFIRE_COOKING_RECIPE = (RecipeSerializerCooking) a("campfire_cooking", (RecipeSerializer) (new RecipeSerializerCooking<>(RecipeCampfire::new, 100)));
    RecipeSerializer<RecipeStonecutting> STONECUTTER = a("stonecutting", (RecipeSerializer) (new RecipeSingleItem.a<>(RecipeStonecutting::new)));
    RecipeSerializer<RecipeSmithing> SMITHING = a("smithing", (RecipeSerializer) (new RecipeSmithing.a()));

    T a(MinecraftKey minecraftkey, JsonObject jsonobject);

    T a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer);

    void a(PacketDataSerializer packetdataserializer, T t0);

    static <S extends RecipeSerializer<T>, T extends IRecipe<?>> S a(String s, S s0) {
        return (RecipeSerializer) IRegistry.a(IRegistry.RECIPE_SERIALIZER, s, (Object) s0);
    }
}
