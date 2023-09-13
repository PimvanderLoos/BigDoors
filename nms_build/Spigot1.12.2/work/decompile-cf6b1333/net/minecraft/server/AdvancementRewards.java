package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;

public class AdvancementRewards {

    public static final AdvancementRewards a = new AdvancementRewards(0, new MinecraftKey[0], new MinecraftKey[0], CustomFunction.a.a);
    private final int b;
    private final MinecraftKey[] c;
    private final MinecraftKey[] d;
    private final CustomFunction.a e;

    public AdvancementRewards(int i, MinecraftKey[] aminecraftkey, MinecraftKey[] aminecraftkey1, CustomFunction.a customfunction_a) {
        this.b = i;
        this.c = aminecraftkey;
        this.d = aminecraftkey1;
        this.e = customfunction_a;
    }

    public void a(final EntityPlayer entityplayer) {
        entityplayer.giveExp(this.b);
        LootTableInfo loottableinfo = (new LootTableInfo.a(entityplayer.x())).a((Entity) entityplayer).a();
        boolean flag = false;
        MinecraftKey[] aminecraftkey = this.c;
        int i = aminecraftkey.length;

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = aminecraftkey[j];
            Iterator iterator = entityplayer.world.getLootTableRegistry().a(minecraftkey).a(entityplayer.getRandom(), loottableinfo).iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                if (entityplayer.c(itemstack)) {
                    entityplayer.world.a((EntityHuman) null, entityplayer.locX, entityplayer.locY, entityplayer.locZ, SoundEffects.dx, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRandom().nextFloat() - entityplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    flag = true;
                } else {
                    EntityItem entityitem = entityplayer.drop(itemstack, false);

                    if (entityitem != null) {
                        entityitem.r();
                        entityitem.d(entityplayer.getName());
                    }
                }
            }
        }

        if (flag) {
            entityplayer.defaultContainer.b();
        }

        if (this.d.length > 0) {
            entityplayer.a(this.d);
        }

        final MinecraftServer minecraftserver = entityplayer.server;
        CustomFunction customfunction = this.e.a(minecraftserver.aL());

        if (customfunction != null) {
            ICommandListener icommandlistener = new ICommandListener() {
                public String getName() {
                    return entityplayer.getName();
                }

                public IChatBaseComponent getScoreboardDisplayName() {
                    return entityplayer.getScoreboardDisplayName();
                }

                public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

                public boolean a(int i, String s) {
                    return i <= 2;
                }

                public BlockPosition getChunkCoordinates() {
                    return entityplayer.getChunkCoordinates();
                }

                public Vec3D d() {
                    return entityplayer.d();
                }

                public World getWorld() {
                    return entityplayer.world;
                }

                public Entity f() {
                    return entityplayer;
                }

                public boolean getSendCommandFeedback() {
                    return minecraftserver.worldServer[0].getGameRules().getBoolean("commandBlockOutput");
                }

                public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
                    entityplayer.a(commandobjectiveexecutor_enumcommandresult, i);
                }

                public MinecraftServer C_() {
                    return entityplayer.C_();
                }
            };

            minecraftserver.aL().a(customfunction, icommandlistener);
        }

    }

    public String toString() {
        return "AdvancementRewards{experience=" + this.b + ", loot=" + Arrays.toString(this.c) + ", recipes=" + Arrays.toString(this.d) + ", function=" + this.e + '}';
    }

    public static class a implements JsonDeserializer<AdvancementRewards> {

        public a() {}

        public AdvancementRewards a(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "rewards");
            int i = ChatDeserializer.a(jsonobject, "experience", 0);
            JsonArray jsonarray = ChatDeserializer.a(jsonobject, "loot", new JsonArray());
            MinecraftKey[] aminecraftkey = new MinecraftKey[jsonarray.size()];

            for (int j = 0; j < aminecraftkey.length; ++j) {
                aminecraftkey[j] = new MinecraftKey(ChatDeserializer.a(jsonarray.get(j), "loot[" + j + "]"));
            }

            JsonArray jsonarray1 = ChatDeserializer.a(jsonobject, "recipes", new JsonArray());
            MinecraftKey[] aminecraftkey1 = new MinecraftKey[jsonarray1.size()];

            for (int k = 0; k < aminecraftkey1.length; ++k) {
                aminecraftkey1[k] = new MinecraftKey(ChatDeserializer.a(jsonarray1.get(k), "recipes[" + k + "]"));
                IRecipe irecipe = CraftingManager.a(aminecraftkey1[k]);

                if (irecipe == null) {
                    throw new JsonSyntaxException("Unknown recipe \'" + aminecraftkey1[k] + "\'");
                }
            }

            CustomFunction.a customfunction_a;

            if (jsonobject.has("function")) {
                customfunction_a = new CustomFunction.a(new MinecraftKey(ChatDeserializer.h(jsonobject, "function")));
            } else {
                customfunction_a = CustomFunction.a.a;
            }

            return new AdvancementRewards(i, aminecraftkey, aminecraftkey1, customfunction_a);
        }

        public Object deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext) throws JsonParseException {
            return this.a(jsonelement, type, jsondeserializationcontext);
        }
    }
}
