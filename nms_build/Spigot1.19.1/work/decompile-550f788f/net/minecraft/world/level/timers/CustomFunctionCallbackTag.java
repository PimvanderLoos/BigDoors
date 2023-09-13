package net.minecraft.world.level.timers;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CustomFunction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.MinecraftServer;

public class CustomFunctionCallbackTag implements CustomFunctionCallbackTimer<MinecraftServer> {

    final MinecraftKey tagId;

    public CustomFunctionCallbackTag(MinecraftKey minecraftkey) {
        this.tagId = minecraftkey;
    }

    public void handle(MinecraftServer minecraftserver, CustomFunctionCallbackTimerQueue<MinecraftServer> customfunctioncallbacktimerqueue, long i) {
        CustomFunctionData customfunctiondata = minecraftserver.getFunctions();
        Collection<CustomFunction> collection = customfunctiondata.getTag(this.tagId);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            CustomFunction customfunction = (CustomFunction) iterator.next();

            customfunctiondata.execute(customfunction, customfunctiondata.getGameLoopSender());
        }

    }

    public static class a extends CustomFunctionCallbackTimer.a<MinecraftServer, CustomFunctionCallbackTag> {

        public a() {
            super(new MinecraftKey("function_tag"), CustomFunctionCallbackTag.class);
        }

        public void serialize(NBTTagCompound nbttagcompound, CustomFunctionCallbackTag customfunctioncallbacktag) {
            nbttagcompound.putString("Name", customfunctioncallbacktag.tagId.toString());
        }

        @Override
        public CustomFunctionCallbackTag deserialize(NBTTagCompound nbttagcompound) {
            MinecraftKey minecraftkey = new MinecraftKey(nbttagcompound.getString("Name"));

            return new CustomFunctionCallbackTag(minecraftkey);
        }
    }
}
