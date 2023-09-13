package net.minecraft.world.level.material;

public final class Material {

    public static final Material AIR = (new Material.a(MaterialMapColor.b)).c().i().b().e().h();
    public static final Material STRUCTURE_VOID = (new Material.a(MaterialMapColor.b)).c().i().b().e().h();
    public static final Material PORTAL = (new Material.a(MaterialMapColor.b)).c().i().b().g().h();
    public static final Material WOOL = (new Material.a(MaterialMapColor.e)).c().i().b().d().h();
    public static final Material PLANT = (new Material.a(MaterialMapColor.i)).c().i().b().f().h();
    public static final Material WATER_PLANT = (new Material.a(MaterialMapColor.n)).c().i().b().f().h();
    public static final Material REPLACEABLE_PLANT = (new Material.a(MaterialMapColor.i)).c().i().b().f().e().d().h();
    public static final Material h = (new Material.a(MaterialMapColor.i)).c().i().b().f().e().h();
    public static final Material REPLACEABLE_WATER_PLANT = (new Material.a(MaterialMapColor.n)).c().i().b().f().e().h();
    public static final Material WATER = (new Material.a(MaterialMapColor.n)).c().i().b().f().e().a().h();
    public static final Material BUBBLE_COLUMN = (new Material.a(MaterialMapColor.n)).c().i().b().f().e().a().h();
    public static final Material LAVA = (new Material.a(MaterialMapColor.f)).c().i().b().f().e().a().h();
    public static final Material PACKED_ICE = (new Material.a(MaterialMapColor.j)).c().i().b().f().e().h();
    public static final Material FIRE = (new Material.a(MaterialMapColor.b)).c().i().b().f().e().h();
    public static final Material ORIENTABLE = (new Material.a(MaterialMapColor.b)).c().i().b().f().h();
    public static final Material WEB = (new Material.a(MaterialMapColor.e)).c().i().f().h();
    public static final Material BUILDABLE_GLASS = (new Material.a(MaterialMapColor.b)).h();
    public static final Material CLAY = (new Material.a(MaterialMapColor.k)).h();
    public static final Material EARTH = (new Material.a(MaterialMapColor.l)).h();
    public static final Material GRASS = (new Material.a(MaterialMapColor.c)).h();
    public static final Material SNOW_LAYER = (new Material.a(MaterialMapColor.g)).h();
    public static final Material SAND = (new Material.a(MaterialMapColor.d)).h();
    public static final Material SPONGE = (new Material.a(MaterialMapColor.t)).h();
    public static final Material SHULKER_SHELL = (new Material.a(MaterialMapColor.z)).h();
    public static final Material WOOD = (new Material.a(MaterialMapColor.o)).d().h();
    public static final Material NETHER_WOOD = (new Material.a(MaterialMapColor.o)).h();
    public static final Material BAMBOO_SAPLING = (new Material.a(MaterialMapColor.o)).d().f().c().h();
    public static final Material BAMBOO = (new Material.a(MaterialMapColor.o)).d().f().h();
    public static final Material CLOTH = (new Material.a(MaterialMapColor.e)).d().h();
    public static final Material TNT = (new Material.a(MaterialMapColor.f)).d().i().h();
    public static final Material LEAVES = (new Material.a(MaterialMapColor.i)).d().i().f().h();
    public static final Material SHATTERABLE = (new Material.a(MaterialMapColor.b)).i().h();
    public static final Material ICE = (new Material.a(MaterialMapColor.g)).i().h();
    public static final Material CACTUS = (new Material.a(MaterialMapColor.i)).i().f().h();
    public static final Material STONE = (new Material.a(MaterialMapColor.m)).h();
    public static final Material ORE = (new Material.a(MaterialMapColor.h)).h();
    public static final Material SNOW_BLOCK = (new Material.a(MaterialMapColor.j)).h();
    public static final Material HEAVY = (new Material.a(MaterialMapColor.h)).g().h();
    public static final Material BANNER = (new Material.a(MaterialMapColor.b)).g().h();
    public static final Material PISTON = (new Material.a(MaterialMapColor.m)).g().h();
    public static final Material CORAL = (new Material.a(MaterialMapColor.i)).f().h();
    public static final Material PUMPKIN = (new Material.a(MaterialMapColor.i)).f().h();
    public static final Material DRAGON_EGG = (new Material.a(MaterialMapColor.i)).f().h();
    public static final Material CAKE = (new Material.a(MaterialMapColor.b)).f().h();
    private final MaterialMapColor S;
    private final EnumPistonReaction T;
    private final boolean U;
    private final boolean canBurn;
    private final boolean W;
    private final boolean X;
    private final boolean Y;
    private final boolean Z;

    public Material(MaterialMapColor materialmapcolor, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4, boolean flag5, EnumPistonReaction enumpistonreaction) {
        this.S = materialmapcolor;
        this.W = flag;
        this.Z = flag1;
        this.U = flag2;
        this.X = flag3;
        this.canBurn = flag4;
        this.Y = flag5;
        this.T = enumpistonreaction;
    }

    public boolean isLiquid() {
        return this.W;
    }

    public boolean isBuildable() {
        return this.Z;
    }

    public boolean isSolid() {
        return this.U;
    }

    public boolean isBurnable() {
        return this.canBurn;
    }

    public boolean isReplaceable() {
        return this.Y;
    }

    public boolean f() {
        return this.X;
    }

    public EnumPistonReaction getPushReaction() {
        return this.T;
    }

    public MaterialMapColor h() {
        return this.S;
    }

    public static class a {

        private EnumPistonReaction a;
        private boolean b;
        private boolean c;
        private boolean d;
        private boolean e;
        private boolean f;
        private final MaterialMapColor g;
        private boolean h;

        public a(MaterialMapColor materialmapcolor) {
            this.a = EnumPistonReaction.NORMAL;
            this.b = true;
            this.f = true;
            this.h = true;
            this.g = materialmapcolor;
        }

        public Material.a a() {
            this.d = true;
            return this;
        }

        public Material.a b() {
            this.f = false;
            return this;
        }

        public Material.a c() {
            this.b = false;
            return this;
        }

        private Material.a i() {
            this.h = false;
            return this;
        }

        protected Material.a d() {
            this.c = true;
            return this;
        }

        public Material.a e() {
            this.e = true;
            return this;
        }

        protected Material.a f() {
            this.a = EnumPistonReaction.DESTROY;
            return this;
        }

        protected Material.a g() {
            this.a = EnumPistonReaction.BLOCK;
            return this;
        }

        public Material h() {
            return new Material(this.g, this.d, this.f, this.b, this.h, this.c, this.e, this.a);
        }
    }
}
