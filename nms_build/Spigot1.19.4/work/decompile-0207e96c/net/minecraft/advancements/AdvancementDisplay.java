package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
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
    @Nullable
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

    public void setLocation(float f, float f1) {
        this.x = f;
        this.y = f1;
    }

    public IChatBaseComponent getTitle() {
        return this.title;
    }

    public IChatBaseComponent getDescription() {
        return this.description;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    @Nullable
    public MinecraftKey getBackground() {
        return this.background;
    }

    public AdvancementFrameType getFrame() {
        return this.frame;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public boolean shouldShowToast() {
        return this.showToast;
    }

    public boolean shouldAnnounceChat() {
        return this.announceChat;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public static AdvancementDisplay fromJson(JsonObject jsonobject) {
        IChatMutableComponent ichatmutablecomponent = IChatBaseComponent.ChatSerializer.fromJson(jsonobject.get("title"));
        IChatMutableComponent ichatmutablecomponent1 = IChatBaseComponent.ChatSerializer.fromJson(jsonobject.get("description"));

        if (ichatmutablecomponent != null && ichatmutablecomponent1 != null) {
            ItemStack itemstack = getIcon(ChatDeserializer.getAsJsonObject(jsonobject, "icon"));
            MinecraftKey minecraftkey = jsonobject.has("background") ? new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "background")) : null;
            AdvancementFrameType advancementframetype = jsonobject.has("frame") ? AdvancementFrameType.byName(ChatDeserializer.getAsString(jsonobject, "frame")) : AdvancementFrameType.TASK;
            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "show_toast", true);
            boolean flag1 = ChatDeserializer.getAsBoolean(jsonobject, "announce_to_chat", true);
            boolean flag2 = ChatDeserializer.getAsBoolean(jsonobject, "hidden", false);

            return new AdvancementDisplay(itemstack, ichatmutablecomponent, ichatmutablecomponent1, minecraftkey, advancementframetype, flag, flag1, flag2);
        } else {
            throw new JsonSyntaxException("Both title and description must be set");
        }
    }

    private static ItemStack getIcon(JsonObject jsonobject) {
        if (!jsonobject.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        } else {
            Item item = ChatDeserializer.getAsItem(jsonobject, "item");

            if (jsonobject.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                ItemStack itemstack = new ItemStack(item);

                if (jsonobject.has("nbt")) {
                    try {
                        NBTTagCompound nbttagcompound = MojangsonParser.parseTag(ChatDeserializer.convertToString(jsonobject.get("nbt"), "nbt"));

                        itemstack.setTag(nbttagcompound);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
                    }
                }

                return itemstack;
            }
        }
    }

    public void serializeToNetwork(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeComponent(this.title);
        packetdataserializer.writeComponent(this.description);
        packetdataserializer.writeItem(this.icon);
        packetdataserializer.writeEnum(this.frame);
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
            packetdataserializer.writeResourceLocation(this.background);
        }

        packetdataserializer.writeFloat(this.x);
        packetdataserializer.writeFloat(this.y);
    }

    public static AdvancementDisplay fromNetwork(PacketDataSerializer packetdataserializer) {
        IChatBaseComponent ichatbasecomponent = packetdataserializer.readComponent();
        IChatBaseComponent ichatbasecomponent1 = packetdataserializer.readComponent();
        ItemStack itemstack = packetdataserializer.readItem();
        AdvancementFrameType advancementframetype = (AdvancementFrameType) packetdataserializer.readEnum(AdvancementFrameType.class);
        int i = packetdataserializer.readInt();
        MinecraftKey minecraftkey = (i & 1) != 0 ? packetdataserializer.readResourceLocation() : null;
        boolean flag = (i & 2) != 0;
        boolean flag1 = (i & 4) != 0;
        AdvancementDisplay advancementdisplay = new AdvancementDisplay(itemstack, ichatbasecomponent, ichatbasecomponent1, minecraftkey, advancementframetype, flag, false, flag1);

        advancementdisplay.setLocation(packetdataserializer.readFloat(), packetdataserializer.readFloat());
        return advancementdisplay;
    }

    public JsonElement serializeToJson() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.add("icon", this.serializeIcon());
        jsonobject.add("title", IChatBaseComponent.ChatSerializer.toJsonTree(this.title));
        jsonobject.add("description", IChatBaseComponent.ChatSerializer.toJsonTree(this.description));
        jsonobject.addProperty("frame", this.frame.getName());
        jsonobject.addProperty("show_toast", this.showToast);
        jsonobject.addProperty("announce_to_chat", this.announceChat);
        jsonobject.addProperty("hidden", this.hidden);
        if (this.background != null) {
            jsonobject.addProperty("background", this.background.toString());
        }

        return jsonobject;
    }

    private JsonObject serializeIcon() {
        JsonObject jsonobject = new JsonObject();

        jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            jsonobject.addProperty("nbt", this.icon.getTag().toString());
        }

        return jsonobject;
    }
}
