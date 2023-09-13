package net.minecraft.server;

public class Material {

    public static final Material AIR = new MaterialGas(MaterialMapColor.c);
    public static final Material GRASS = new Material(MaterialMapColor.d);
    public static final Material EARTH = new Material(MaterialMapColor.m);
    public static final Material WOOD = (new Material(MaterialMapColor.p)).g();
    public static final Material STONE = (new Material(MaterialMapColor.n)).f();
    public static final Material ORE = (new Material(MaterialMapColor.i)).f();
    public static final Material HEAVY = (new Material(MaterialMapColor.i)).f().o();
    public static final Material WATER = (new MaterialLiquid(MaterialMapColor.o)).n();
    public static final Material LAVA = (new MaterialLiquid(MaterialMapColor.g)).n();
    public static final Material LEAVES = (new Material(MaterialMapColor.j)).g().s().n();
    public static final Material PLANT = (new MaterialDecoration(MaterialMapColor.j)).n();
    public static final Material REPLACEABLE_PLANT = (new MaterialDecoration(MaterialMapColor.j)).g().n().i();
    public static final Material SPONGE = new Material(MaterialMapColor.u);
    public static final Material CLOTH = (new Material(MaterialMapColor.f)).g();
    public static final Material FIRE = (new MaterialGas(MaterialMapColor.c)).n();
    public static final Material SAND = new Material(MaterialMapColor.e);
    public static final Material ORIENTABLE = (new MaterialDecoration(MaterialMapColor.c)).n();
    public static final Material WOOL = (new MaterialDecoration(MaterialMapColor.f)).g();
    public static final Material SHATTERABLE = (new Material(MaterialMapColor.c)).s().p();
    public static final Material BUILDABLE_GLASS = (new Material(MaterialMapColor.c)).p();
    public static final Material TNT = (new Material(MaterialMapColor.g)).g().s();
    public static final Material CORAL = (new Material(MaterialMapColor.j)).n();
    public static final Material ICE = (new Material(MaterialMapColor.h)).s().p();
    public static final Material SNOW_LAYER = (new Material(MaterialMapColor.h)).p();
    public static final Material PACKED_ICE = (new MaterialDecoration(MaterialMapColor.k)).i().s().f().n();
    public static final Material SNOW_BLOCK = (new Material(MaterialMapColor.k)).f();
    public static final Material CACTUS = (new Material(MaterialMapColor.j)).s().n();
    public static final Material CLAY = new Material(MaterialMapColor.l);
    public static final Material PUMPKIN = (new Material(MaterialMapColor.j)).n();
    public static final Material DRAGON_EGG = (new Material(MaterialMapColor.j)).n();
    public static final Material PORTAL = (new MaterialPortal(MaterialMapColor.c)).o();
    public static final Material CAKE = (new Material(MaterialMapColor.c)).n();
    public static final Material WEB = (new Material(MaterialMapColor.f) {
        public boolean isSolid() {
            return false;
        }
    }).f().n();
    public static final Material PISTON = (new Material(MaterialMapColor.n)).o();
    public static final Material BANNER = (new Material(MaterialMapColor.c)).f().o();
    public static final Material J = new MaterialGas(MaterialMapColor.c);
    private boolean canBurn;
    private boolean L;
    private boolean M;
    private final MaterialMapColor N;
    private boolean O = true;
    private EnumPistonReaction P;
    private boolean Q;

    public Material(MaterialMapColor materialmapcolor) {
        this.P = EnumPistonReaction.NORMAL;
        this.N = materialmapcolor;
    }

    public boolean isLiquid() {
        return false;
    }

    public boolean isBuildable() {
        return true;
    }

    public boolean blocksLight() {
        return true;
    }

    public boolean isSolid() {
        return true;
    }

    private Material s() {
        this.M = true;
        return this;
    }

    protected Material f() {
        this.O = false;
        return this;
    }

    protected Material g() {
        this.canBurn = true;
        return this;
    }

    public boolean isBurnable() {
        return this.canBurn;
    }

    public Material i() {
        this.L = true;
        return this;
    }

    public boolean isReplaceable() {
        return this.L;
    }

    public boolean k() {
        return this.M ? false : this.isSolid();
    }

    public boolean isAlwaysDestroyable() {
        return this.O;
    }

    public EnumPistonReaction getPushReaction() {
        return this.P;
    }

    protected Material n() {
        this.P = EnumPistonReaction.DESTROY;
        return this;
    }

    protected Material o() {
        this.P = EnumPistonReaction.BLOCK;
        return this;
    }

    protected Material p() {
        this.Q = true;
        return this;
    }

    public MaterialMapColor r() {
        return this.N;
    }
}
