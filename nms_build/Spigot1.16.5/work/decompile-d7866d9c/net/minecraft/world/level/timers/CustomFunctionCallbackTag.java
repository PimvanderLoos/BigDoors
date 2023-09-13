package net.minecraft.world.level.timers;

import java.util.Iterator;
import net.minecraft.commands.CustomFunction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;

public class CustomFunctionCallbackTag implements CustomFunctionCallbackTimer<MinecraftServer> {

    private final MinecraftKey a;

    public CustomFunctionCallbackTag(MinecraftKey minecraftkey) {
        this.a = minecraftkey;
    }

    public void a(MinecraftServer minecraftserver, CustomFunctionCallbackTimerQueue<MinecraftServer> customfunctioncallbacktimerqueue, long i) {
        CustomFunctionData customfunctiondata = minecraftserver.getFunctionData();
        Tag<CustomFunction> tag = customfunctiondata.b(this.a);
        Iterator iterator = tag.getTagged().iterator();

        while (iterator.hasNext()) {
            CustomFunction customfunction = (CustomFunction) iterator.next();

            customfunctiondata.a(customfunction, customfunctiondata.e());
        }

    }

    public static class a extends CustomFunctionCallbackTimer.a<MinecraftServer, CustomFunctionCallbackTag> {

        public a() {
            super(new MinecraftKey("function_tag"), CustomFunctionCallbackTag.class);
        }

        public void a(NBTTagCompound nbttagcompound, CustomFunctionCallbackTag customfunctioncallbacktag) {
            nbttagcompound.setString("Name", customfunctioncallbacktag.a.toString());
        }

        @Override
        public CustomFunctionCallbackTag b(NBTTagCompound nbttagcompound) {
            MinecraftKey minecraftkey = new MinecraftKey(nbttagcompound.getString("Name"));

            return new CustomFunctionCallbackTag(minecraftkey);
        }
    }
}
