package net.minecraft.server;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;

public class DefinedStructureManager {

    private final Map<String, DefinedStructure> a = Maps.newHashMap();
    private final String b;
    private final DataConverterManager c;

    public DefinedStructureManager(String s, DataConverterManager dataconvertermanager) {
        this.b = s;
        this.c = dataconvertermanager;
    }

    public DefinedStructure a(@Nullable MinecraftServer minecraftserver, MinecraftKey minecraftkey) {
        DefinedStructure definedstructure = this.b(minecraftserver, minecraftkey);

        if (definedstructure == null) {
            definedstructure = new DefinedStructure();
            this.a.put(minecraftkey.getKey(), definedstructure);
        }

        return definedstructure;
    }

    @Nullable
    public DefinedStructure b(@Nullable MinecraftServer minecraftserver, MinecraftKey minecraftkey) {
        String s = minecraftkey.getKey();

        if (this.a.containsKey(s)) {
            return (DefinedStructure) this.a.get(s);
        } else {
            if (minecraftserver == null) {
                this.c(minecraftkey);
            } else {
                this.a(minecraftkey);
            }

            return this.a.containsKey(s) ? (DefinedStructure) this.a.get(s) : null;
        }
    }

    public boolean a(MinecraftKey minecraftkey) {
        String s = minecraftkey.getKey();
        File file = new File(this.b, s + ".nbt");

        if (!file.exists()) {
            return this.c(minecraftkey);
        } else {
            FileInputStream fileinputstream = null;

            boolean flag;

            try {
                fileinputstream = new FileInputStream(file);
                this.a(s, (InputStream) fileinputstream);
                return true;
            } catch (Throwable throwable) {
                flag = false;
            } finally {
                IOUtils.closeQuietly(fileinputstream);
            }

            return flag;
        }
    }

    private boolean c(MinecraftKey minecraftkey) {
        String s = minecraftkey.b();
        String s1 = minecraftkey.getKey();
        InputStream inputstream = null;

        boolean flag;

        try {
            inputstream = MinecraftServer.class.getResourceAsStream("/assets/" + s + "/structures/" + s1 + ".nbt");
            this.a(s1, inputstream);
            return true;
        } catch (Throwable throwable) {
            flag = false;
        } finally {
            IOUtils.closeQuietly(inputstream);
        }

        return flag;
    }

    private void a(String s, InputStream inputstream) throws IOException {
        NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(inputstream);

        if (!nbttagcompound.hasKeyOfType("DataVersion", 99)) {
            nbttagcompound.setInt("DataVersion", 500);
        }

        DefinedStructure definedstructure = new DefinedStructure();

        definedstructure.b(this.c.a((DataConverterType) DataConverterTypes.STRUCTURE, nbttagcompound));
        this.a.put(s, definedstructure);
    }

    public boolean c(@Nullable MinecraftServer minecraftserver, MinecraftKey minecraftkey) {
        String s = minecraftkey.getKey();

        if (minecraftserver != null && this.a.containsKey(s)) {
            File file = new File(this.b);

            if (!file.exists()) {
                if (!file.mkdirs()) {
                    return false;
                }
            } else if (!file.isDirectory()) {
                return false;
            }

            File file1 = new File(file, s + ".nbt");
            DefinedStructure definedstructure = (DefinedStructure) this.a.get(s);
            FileOutputStream fileoutputstream = null;

            boolean flag;

            try {
                NBTTagCompound nbttagcompound = definedstructure.a(new NBTTagCompound());

                fileoutputstream = new FileOutputStream(file1);
                NBTCompressedStreamTools.a(nbttagcompound, (OutputStream) fileoutputstream);
                return true;
            } catch (Throwable throwable) {
                flag = false;
            } finally {
                IOUtils.closeQuietly(fileoutputstream);
            }

            return flag;
        } else {
            return false;
        }
    }

    public void b(MinecraftKey minecraftkey) {
        this.a.remove(minecraftkey.getKey());
    }
}
