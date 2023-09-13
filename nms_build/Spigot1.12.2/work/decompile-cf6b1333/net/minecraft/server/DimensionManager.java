package net.minecraft.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum DimensionManager {

    OVERWORLD(0, "overworld", "", WorldProviderNormal.class), NETHER(-1, "the_nether", "_nether", WorldProviderHell.class), THE_END(1, "the_end", "_end", WorldProviderTheEnd.class);

    private final int d;
    private final String e;
    private final String f;
    private final Class<? extends WorldProvider> g;

    private DimensionManager(int i, String s, String s1, Class<? extends WorldProvider> oclass) {
        this.d = i;
        this.e = s;
        this.f = s1;
        this.g = oclass;
    }

    public int getDimensionID() {
        return this.d;
    }

    public String b() {
        return this.e;
    }

    public String c() {
        return this.f;
    }

    public WorldProvider d() {
        try {
            Constructor constructor = this.g.getConstructor(new Class[0]);

            return (WorldProvider) constructor.newInstance(new Object[0]);
        } catch (NoSuchMethodException nosuchmethodexception) {
            throw new Error("Could not create new dimension", nosuchmethodexception);
        } catch (InvocationTargetException invocationtargetexception) {
            throw new Error("Could not create new dimension", invocationtargetexception);
        } catch (InstantiationException instantiationexception) {
            throw new Error("Could not create new dimension", instantiationexception);
        } catch (IllegalAccessException illegalaccessexception) {
            throw new Error("Could not create new dimension", illegalaccessexception);
        }
    }

    public static DimensionManager a(int i) {
        DimensionManager[] adimensionmanager = values();
        int j = adimensionmanager.length;

        for (int k = 0; k < j; ++k) {
            DimensionManager dimensionmanager = adimensionmanager[k];

            if (dimensionmanager.getDimensionID() == i) {
                return dimensionmanager;
            }
        }

        throw new IllegalArgumentException("Invalid dimension id " + i);
    }

    public static DimensionManager a(String s) {
        DimensionManager[] adimensionmanager = values();
        int i = adimensionmanager.length;

        for (int j = 0; j < i; ++j) {
            DimensionManager dimensionmanager = adimensionmanager[j];

            if (dimensionmanager.b().equals(s)) {
                return dimensionmanager;
            }
        }

        throw new IllegalArgumentException("Invalid dimension " + s);
    }
}
