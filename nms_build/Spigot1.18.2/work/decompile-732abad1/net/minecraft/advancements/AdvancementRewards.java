package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CustomFunction;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class AdvancementRewards {

    public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new MinecraftKey[0], new MinecraftKey[0], CustomFunction.a.NONE);
    private final int experience;
    private final MinecraftKey[] loot;
    private final MinecraftKey[] recipes;
    private final CustomFunction.a function;

    public AdvancementRewards(int i, MinecraftKey[] aminecraftkey, MinecraftKey[] aminecraftkey1, CustomFunction.a customfunction_a) {
        this.experience = i;
        this.loot = aminecraftkey;
        this.recipes = aminecraftkey1;
        this.function = customfunction_a;
    }

    public MinecraftKey[] getRecipes() {
        return this.recipes;
    }

    public void grant(EntityPlayer entityplayer) {
        entityplayer.giveExperiencePoints(this.experience);
        LootTableInfo loottableinfo = (new LootTableInfo.Builder(entityplayer.getLevel())).withParameter(LootContextParameters.THIS_ENTITY, entityplayer).withParameter(LootContextParameters.ORIGIN, entityplayer.position()).withRandom(entityplayer.getRandom()).create(LootContextParameterSets.ADVANCEMENT_REWARD);
        boolean flag = false;
        MinecraftKey[] aminecraftkey = this.loot;
        int i = aminecraftkey.length;

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = aminecraftkey[j];
            Iterator iterator = entityplayer.server.getLootTables().get(minecraftkey).getRandomItems(loottableinfo).iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                if (entityplayer.addItem(itemstack)) {
                    entityplayer.level.playSound((EntityHuman) null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), SoundEffects.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRandom().nextFloat() - entityplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    flag = true;
                } else {
                    EntityItem entityitem = entityplayer.drop(itemstack, false);

                    if (entityitem != null) {
                        entityitem.setNoPickUpDelay();
                        entityitem.setOwner(entityplayer.getUUID());
                    }
                }
            }
        }

        if (flag) {
            entityplayer.containerMenu.broadcastChanges();
        }

        if (this.recipes.length > 0) {
            entityplayer.awardRecipesByKey(this.recipes);
        }

        MinecraftServer minecraftserver = entityplayer.server;

        this.function.get(minecraftserver.getFunctions()).ifPresent((customfunction) -> {
            minecraftserver.getFunctions().execute(customfunction, entityplayer.createCommandSourceStack().withSuppressedOutput().withPermission(2));
        });
    }

    public String toString() {
        return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + "}";
    }

    public JsonElement serializeToJson() {
        if (this == AdvancementRewards.EMPTY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.experience != 0) {
                jsonobject.addProperty("experience", this.experience);
            }

            JsonArray jsonarray;
            MinecraftKey[] aminecraftkey;
            int i;
            MinecraftKey minecraftkey;
            int j;

            if (this.loot.length > 0) {
                jsonarray = new JsonArray();
                aminecraftkey = this.loot;
                i = aminecraftkey.length;

                for (j = 0; j < i; ++j) {
                    minecraftkey = aminecraftkey[j];
                    jsonarray.add(minecraftkey.toString());
                }

                jsonobject.add("loot", jsonarray);
            }

            if (this.recipes.length > 0) {
                jsonarray = new JsonArray();
                aminecraftkey = this.recipes;
                i = aminecraftkey.length;

                for (j = 0; j < i; ++j) {
                    minecraftkey = aminecraftkey[j];
                    jsonarray.add(minecraftkey.toString());
                }

                jsonobject.add("recipes", jsonarray);
            }

            if (this.function.getId() != null) {
                jsonobject.addProperty("function", this.function.getId().toString());
            }

            return jsonobject;
        }
    }

    public static AdvancementRewards deserialize(JsonObject jsonobject) throws JsonParseException {
        int i = ChatDeserializer.getAsInt(jsonobject, "experience", 0);
        JsonArray jsonarray = ChatDeserializer.getAsJsonArray(jsonobject, "loot", new JsonArray());
        MinecraftKey[] aminecraftkey = new MinecraftKey[jsonarray.size()];

        for (int j = 0; j < aminecraftkey.length; ++j) {
            aminecraftkey[j] = new MinecraftKey(ChatDeserializer.convertToString(jsonarray.get(j), "loot[" + j + "]"));
        }

        JsonArray jsonarray1 = ChatDeserializer.getAsJsonArray(jsonobject, "recipes", new JsonArray());
        MinecraftKey[] aminecraftkey1 = new MinecraftKey[jsonarray1.size()];

        for (int k = 0; k < aminecraftkey1.length; ++k) {
            aminecraftkey1[k] = new MinecraftKey(ChatDeserializer.convertToString(jsonarray1.get(k), "recipes[" + k + "]"));
        }

        CustomFunction.a customfunction_a;

        if (jsonobject.has("function")) {
            customfunction_a = new CustomFunction.a(new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "function")));
        } else {
            customfunction_a = CustomFunction.a.NONE;
        }

        return new AdvancementRewards(i, aminecraftkey, aminecraftkey1, customfunction_a);
    }

    public static class a {

        private int experience;
        private final List<MinecraftKey> loot = Lists.newArrayList();
        private final List<MinecraftKey> recipes = Lists.newArrayList();
        @Nullable
        private MinecraftKey function;

        public a() {}

        public static AdvancementRewards.a experience(int i) {
            return (new AdvancementRewards.a()).addExperience(i);
        }

        public AdvancementRewards.a addExperience(int i) {
            this.experience += i;
            return this;
        }

        public static AdvancementRewards.a loot(MinecraftKey minecraftkey) {
            return (new AdvancementRewards.a()).addLootTable(minecraftkey);
        }

        public AdvancementRewards.a addLootTable(MinecraftKey minecraftkey) {
            this.loot.add(minecraftkey);
            return this;
        }

        public static AdvancementRewards.a recipe(MinecraftKey minecraftkey) {
            return (new AdvancementRewards.a()).addRecipe(minecraftkey);
        }

        public AdvancementRewards.a addRecipe(MinecraftKey minecraftkey) {
            this.recipes.add(minecraftkey);
            return this;
        }

        public static AdvancementRewards.a function(MinecraftKey minecraftkey) {
            return (new AdvancementRewards.a()).runs(minecraftkey);
        }

        public AdvancementRewards.a runs(MinecraftKey minecraftkey) {
            this.function = minecraftkey;
            return this;
        }

        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, (MinecraftKey[]) this.loot.toArray(new MinecraftKey[0]), (MinecraftKey[]) this.recipes.toArray(new MinecraftKey[0]), this.function == null ? CustomFunction.a.NONE : new CustomFunction.a(this.function));
        }
    }
}
