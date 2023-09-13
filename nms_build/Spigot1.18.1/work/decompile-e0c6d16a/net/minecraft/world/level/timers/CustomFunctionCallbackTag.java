package net.minecraft.world.level.timers;

import java.util.Iterator;
import net.minecraft.commands.CustomFunction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;

public class CustomFunctionCallbackTag implements CustomFunctionCallbackTimer<MinecraftServer> {

    final MinecraftKey tagId;

    public CustomFunctionCallbackTag(MinecraftKey minecraftkey) {
        this.tagId = minecraftkey;
    }

    public void handle(MinecraftServer minecraftserver, CustomFunctionCallbackTimerQueue<MinecraftServer> customfunctioncallbacktimerqueue, long i) {
        CustomFunctionData customfunctiondata = minecraftserver.getFunctions();
        Tag<CustomFunction> tag = customfunctiondata.getTag(this.tagId);
        Iterator iterator = tag.getValues().iterator();

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
