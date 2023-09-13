package net.minecraft.server;

import com.google.gson.JsonParseException;

public class DataConverterBook implements IDataConverter {

    public DataConverterBook() {}

    public int a() {
        return 165;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if ("minecraft:written_book".equals(nbttagcompound.getString("id"))) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("tag");

            if (nbttagcompound1.hasKeyOfType("pages", 9)) {
                NBTTagList nbttaglist = nbttagcompound1.getList("pages", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);
                    Object object = null;

                    if (!"null".equals(s) && !UtilColor.b(s)) {
                        if ((s.charAt(0) != 34 || s.charAt(s.length() - 1) != 34) && (s.charAt(0) != 123 || s.charAt(s.length() - 1) != 125)) {
                            object = new ChatComponentText(s);
                        } else {
                            try {
                                object = (IChatBaseComponent) ChatDeserializer.a(DataConverterSignText.a, s, IChatBaseComponent.class, true);
                                if (object == null) {
                                    object = new ChatComponentText("");
                                }
                            } catch (JsonParseException jsonparseexception) {
                                ;
                            }

                            if (object == null) {
                                try {
                                    object = IChatBaseComponent.ChatSerializer.a(s);
                                } catch (JsonParseException jsonparseexception1) {
                                    ;
                                }
                            }

                            if (object == null) {
                                try {
                                    object = IChatBaseComponent.ChatSerializer.b(s);
                                } catch (JsonParseException jsonparseexception2) {
                                    ;
                                }
                            }

                            if (object == null) {
                                object = new ChatComponentText(s);
                            }
                        }
                    } else {
                        object = new ChatComponentText("");
                    }

                    nbttaglist.a(i, new NBTTagString(IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) object)));
                }

                nbttagcompound1.set("pages", nbttaglist);
            }
        }

        return nbttagcompound;
    }
}
