package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AdvancementDisplay {

    private final IChatBaseComponent title;
    private final IChatBaseComponent description;
    private final ItemStack icon;
    private final MinecraftKey background;
    private final AdvancementFrameType frame;
    private final boolean showToast;
    private final boolean announceChat;
    private final boolean hidden;
    private float x;
    private float y;

    public AdvancementDisplay(ItemStack itemstack, IChatBaseComponent ichatbasecomponent, IChatBaseComponent ichatbasecomponent1, @Nullable MinecraftKey minecraftkey, AdvancementFrameType advancementframetype, boolean flag, boolean flag1, boolean flag2) {
        this.title = ichatbasecomponent;
        this.description = ichatbasecomponent1;
        this.icon = itemstack;
        this.background = minecraftkey;
        this.frame = advancementframetype;
        this.showToast = flag;
        this.announceChat = flag1;
        this.hidden = flag2;
    }

    public void a(float f, float f1) {
        this.x = f;
        this.y = f1;
    }

    public IChatBaseComponent a() {
        return this.title;
    }

    public IChatBaseComponent b() {
        return this.description;
    }

    public ItemStack c() {
        return this.icon;
    }

    @Nullable
    public MinecraftKey d() {
        return this.background;
    }

    public AdvancementFrameType e() {
        return this.frame;
    }

    public float f() {
        return this.x;
    }

    public float g() {
        return this.y;
    }

    public boolean h() {
        return this.showToast;
    }

    public boolean i() {
        return this.announceChat;
    }

    public boolean j() {
        return this.hidden;
    }

    public static AdvancementDisplay a(JsonObject jsonobject) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.a(jsonobject.get("title"));
        IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.ChatSerializer.a(jsonobject.get("description"));

        if (ichatmutablecomponent != null && ichatmutablecomponent1 != null) {
            ItemStack itemstack = b(ChatDeserializer.t(jsonobject, "icon"));
            MinecraftKey minecraftkey = jsonobject.has("background") ? new MinecraftKey(ChatDeserializer.h(jsonobject, "background")) : null;
            AdvancementFrameType advancementframetype = jsonobject.has("frame") ? AdvancementFrameType.a(ChatDeserializer.h(jsonobject, "frame")) : AdvancementFrameType.TASK;
            boolean flag = ChatDeserializer.a(jsonobject, "show_toast", true);
            boolean flag1 = ChatDeserializer.a(jsonobject, "announce_to_chat", true);
            boolean flag2 = ChatDeserializer.a(jsonobject, "hidden", false);

            return new AdvancementDisplay(itemstack, ichatmutablecomponent, ichatmutablecomponent1, minecraftkey, advancementframetype, flag, flag1, flag2);
        } else {
            throw new JsonSyntaxException("Both title and description must be set");
        }
    }

    private static ItemStack b(JsonObject jsonobject) {
        if (!jsonobject.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        } else {
            Item item = ChatDeserializer.i(jsonobject, "item");

            if (jsonobject.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                ItemStack itemstack = new ItemStack(item);

                if (jsonobject.has("nbt")) {
                    try {
                        NBTTagCompound nbttagcompound = MojangsonParser.parse(ChatDeserializer.a(jsonobject.get("nbt"), "nbt"));

                        itemstack.setTag(nbttagcompound);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
                    }
                }

                return itemstack;
            }
        }
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(this.title);
        packetdataserializer.a(this.description);
        packetdataserializer.a(this.icon);
        packetdataserializer.a((Enum) this.frame);
        int i = 0;

        if (this.background != null) {
            i |= 1;
        }

        if (this.showToast) {
            i |= 2;
        }

        if (this.hidden) {
            i |= 4;
        }

        packetdataserializer.writeInt(i);
        if (this.background != null) {
            packetdataserializer.a(this.background);
        }

        packetdataserializer.writeFloat(this.x);
        packetdataserializer.writeFloat(this.y);
    }

    public static AdvancementDisplay b(PacketDataSerializer packetdataserializer) {
        IChatBaseComponent ichatbasecomponent = packetdataserializer.i();
        IChatBaseComponent ichatbasecomponent1 = packetdataserializer.i();
        ItemStack itemstack = packetdataserializer.o();
        AdvancementFrameType advancementframetype = (AdvancementFrameType) packetdataserializer.a(AdvancementFrameType.class);
        int i = packetdataserializer.readInt();
        MinecraftKey minecraftkey = (i & 1) != 0 ? packetdataserializer.q() : null;
        boolean flag = (i & 2) != 0;
        boolean flag1 = (i & 4) != 0;
        AdvancementDisplay advancementdisplay = new AdvancementDisplay(itemstack, ichatbasecomponent, ichatbasecomponent1, minecraftkey, advancementframetype, flag, false, flag1);

        advancementdisplay.a(packetdataserializer.readFloat(), packetdataserializer.readFloat());
        return advancementdisplay;
    }

    public JsonElement k() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.add("icon", this.l());
        jsonobject.add("title", IChatBaseComponent.ChatSerializer.b(this.title));
        jsonobject.add("description", IChatBaseComponent.ChatSerializer.b(this.description));
        jsonobject.addProperty("frame", this.frame.a());
        jsonobject.addProperty("show_toast", this.showToast);
        jsonobject.addProperty("announce_to_chat", this.announceChat);
        jsonobject.addProperty("hidden", this.hidden);
        if (this.background != null) {
            jsonobject.addProperty("background", this.background.toString());
        }

        return jsonobject;
    }

    private JsonObject l() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("item", IRegistry.ITEM.getKey(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            jsonobject.addProperty("nbt", this.icon.getTag().toString());
        }

        return jsonobject;
    }
}
