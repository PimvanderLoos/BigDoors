package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFunctionData implements ITickable {

    private static final Logger a = LogManager.getLogger();
    private final File b;
    private final MinecraftServer c;
    private final Map<MinecraftKey, CustomFunction> d = Maps.newHashMap();
    private String e = "-";
    private CustomFunction f;
    private final ArrayDeque<CustomFunctionData.a> g = new ArrayDeque();
    private boolean h = false;
    private final ICommandListener i = new ICommandListener() {
        public String getName() {
            return CustomFunctionData.this.e;
        }

        public boolean a(int i, String s) {
            return i <= 2;
        }

        public World getWorld() {
            return CustomFunctionData.this.c.worldServer[0];
        }

        public MinecraftServer C_() {
            return CustomFunctionData.this.c;
        }
    };

    public CustomFunctionData(@Nullable File file, MinecraftServer minecraftserver) {
        this.b = file;
        this.c = minecraftserver;
        this.f();
    }

    @Nullable
    public CustomFunction a(MinecraftKey minecraftkey) {
        return (CustomFunction) this.d.get(minecraftkey);
    }

    public ICommandHandler a() {
        return this.c.getCommandHandler();
    }

    public int c() {
        return this.c.worldServer[0].getGameRules().c("maxCommandChainLength");
    }

    public Map<MinecraftKey, CustomFunction> d() {
        return this.d;
    }

    public void e() {
        String s = this.c.worldServer[0].getGameRules().get("gameLoopFunction");

        if (!s.equals(this.e)) {
            this.e = s;
            this.f = this.a(new MinecraftKey(s));
        }

        if (this.f != null) {
            this.a(this.f, this.i);
        }

    }

    public int a(CustomFunction customfunction, ICommandListener icommandlistener) {
        int i = this.c();

        if (this.h) {
            if (this.g.size() < i) {
                this.g.addFirst(new CustomFunctionData.a(this, icommandlistener, new CustomFunction.d(customfunction)));
            }

            return 0;
        } else {
            int j;

            try {
                this.h = true;
                int k = 0;
                CustomFunction.c[] acustomfunction_c = customfunction.a();

                for (j = acustomfunction_c.length - 1; j >= 0; --j) {
                    this.g.push(new CustomFunctionData.a(this, icommandlistener, acustomfunction_c[j]));
                }

                do {
                    if (this.g.isEmpty()) {
                        j = k;
                        return j;
                    }

                    ((CustomFunctionData.a) this.g.removeFirst()).a(this.g, i);
                    ++k;
                } while (k < i);

                j = k;
            } finally {
                this.g.clear();
                this.h = false;
            }

            return j;
        }
    }

    public void f() {
        this.d.clear();
        this.f = null;
        this.e = "-";
        this.h();
    }

    private void h() {
        if (this.b != null) {
            this.b.mkdirs();
            Iterator iterator = FileUtils.listFiles(this.b, new String[] { "mcfunction"}, true).iterator();

            while (iterator.hasNext()) {
                File file = (File) iterator.next();
                String s = FilenameUtils.removeExtension(this.b.toURI().relativize(file.toURI()).toString());
                String[] astring = s.split("/", 2);

                if (astring.length == 2) {
                    MinecraftKey minecraftkey = new MinecraftKey(astring[0], astring[1]);

                    try {
                        this.d.put(minecraftkey, CustomFunction.a(this, Files.readLines(file, StandardCharsets.UTF_8)));
                    } catch (Throwable throwable) {
                        CustomFunctionData.a.error("Couldn\'t read custom function " + minecraftkey + " from " + file, throwable);
                    }
                }
            }

            if (!this.d.isEmpty()) {
                CustomFunctionData.a.info("Loaded " + this.d.size() + " custom command functions");
            }

        }
    }

    public static class a {

        private final CustomFunctionData a;
        private final ICommandListener b;
        private final CustomFunction.c c;

        public a(CustomFunctionData customfunctiondata, ICommandListener icommandlistener, CustomFunction.c customfunction_c) {
            this.a = customfunctiondata;
            this.b = icommandlistener;
            this.c = customfunction_c;
        }

        public void a(ArrayDeque<CustomFunctionData.a> arraydeque, int i) {
            this.c.a(this.a, this.b, arraydeque, i);
        }

        public String toString() {
            return this.c.toString();
        }
    }
}
