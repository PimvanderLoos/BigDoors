package net.minecraft.server.packs.resources;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;

public class Resource implements IResource {

    private final String a;
    private final MinecraftKey b;
    private final InputStream c;
    private final InputStream d;

    public Resource(String s, MinecraftKey minecraftkey, InputStream inputstream, @Nullable InputStream inputstream1) {
        this.a = s;
        this.b = minecraftkey;
        this.c = inputstream;
        this.d = inputstream1;
    }

    @Override
    public InputStream b() {
        return this.c;
    }

    @Override
    public String d() {
        return this.a;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Resource)) {
            return false;
        } else {
            Resource resource;
            label32:
            {
                resource = (Resource) object;
                if (this.b != null) {
                    if (this.b.equals(resource.b)) {
                        break label32;
                    }
                } else if (resource.b == null) {
                    break label32;
                }

                return false;
            }

            if (this.a != null) {
                if (this.a.equals(resource.a)) {
                    return true;
                }
            } else if (resource.a == null) {
                return true;
            }

            return false;
        }
    }

    public int hashCode() {
        int i = this.a != null ? this.a.hashCode() : 0;

        i = 31 * i + (this.b != null ? this.b.hashCode() : 0);
        return i;
    }

    public void close() throws IOException {
        this.c.close();
        if (this.d != null) {
            this.d.close();
        }

    }
}
