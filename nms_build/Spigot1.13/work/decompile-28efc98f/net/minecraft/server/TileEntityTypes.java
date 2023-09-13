package net.minecraft.server;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityTypes<T extends TileEntity> {

    private static final Logger A = LogManager.getLogger();
    public static final RegistryMaterials<MinecraftKey, TileEntityTypes<?>> a = new RegistryMaterials();
    public static final TileEntityTypes<TileEntityFurnace> b = a("furnace", TileEntityTypes.a.a(TileEntityFurnace::new));
    public static final TileEntityTypes<TileEntityChest> c = a("chest", TileEntityTypes.a.a(TileEntityChest::new));
    public static final TileEntityTypes<TileEntityChestTrapped> d = a("trapped_chest", TileEntityTypes.a.a(TileEntityChestTrapped::new));
    public static final TileEntityTypes<TileEntityEnderChest> e = a("ender_chest", TileEntityTypes.a.a(TileEntityEnderChest::new));
    public static final TileEntityTypes<TileEntityJukeBox> f = a("jukebox", TileEntityTypes.a.a(TileEntityJukeBox::new));
    public static final TileEntityTypes<TileEntityDispenser> g = a("dispenser", TileEntityTypes.a.a(TileEntityDispenser::new));
    public static final TileEntityTypes<TileEntityDropper> h = a("dropper", TileEntityTypes.a.a(TileEntityDropper::new));
    public static final TileEntityTypes<TileEntitySign> i = a("sign", TileEntityTypes.a.a(TileEntitySign::new));
    public static final TileEntityTypes<TileEntityMobSpawner> j = a("mob_spawner", TileEntityTypes.a.a(TileEntityMobSpawner::new));
    public static final TileEntityTypes<TileEntityPiston> k = a("piston", TileEntityTypes.a.a(TileEntityPiston::new));
    public static final TileEntityTypes<TileEntityBrewingStand> l = a("brewing_stand", TileEntityTypes.a.a(TileEntityBrewingStand::new));
    public static final TileEntityTypes<TileEntityEnchantTable> m = a("enchanting_table", TileEntityTypes.a.a(TileEntityEnchantTable::new));
    public static final TileEntityTypes<TileEntityEnderPortal> n = a("end_portal", TileEntityTypes.a.a(TileEntityEnderPortal::new));
    public static final TileEntityTypes<TileEntityBeacon> o = a("beacon", TileEntityTypes.a.a(TileEntityBeacon::new));
    public static final TileEntityTypes<TileEntitySkull> p = a("skull", TileEntityTypes.a.a(TileEntitySkull::new));
    public static final TileEntityTypes<TileEntityLightDetector> q = a("daylight_detector", TileEntityTypes.a.a(TileEntityLightDetector::new));
    public static final TileEntityTypes<TileEntityHopper> r = a("hopper", TileEntityTypes.a.a(TileEntityHopper::new));
    public static final TileEntityTypes<TileEntityComparator> s = a("comparator", TileEntityTypes.a.a(TileEntityComparator::new));
    public static final TileEntityTypes<TileEntityBanner> t = a("banner", TileEntityTypes.a.a(TileEntityBanner::new));
    public static final TileEntityTypes<TileEntityStructure> u = a("structure_block", TileEntityTypes.a.a(TileEntityStructure::new));
    public static final TileEntityTypes<TileEntityEndGateway> v = a("end_gateway", TileEntityTypes.a.a(TileEntityEndGateway::new));
    public static final TileEntityTypes<TileEntityCommand> w = a("command_block", TileEntityTypes.a.a(TileEntityCommand::new));
    public static final TileEntityTypes<TileEntityShulkerBox> x = a("shulker_box", TileEntityTypes.a.a(TileEntityShulkerBox::new));
    public static final TileEntityTypes<TileEntityBed> y = a("bed", TileEntityTypes.a.a(TileEntityBed::new));
    public static final TileEntityTypes<TileEntityConduit> CONDUIT = a("conduit", TileEntityTypes.a.a(TileEntityConduit::new));
    private final Supplier<? extends T> B;
    private final Type<?> C;

    @Nullable
    public static MinecraftKey a(TileEntityTypes<?> tileentitytypes) {
        return (MinecraftKey) TileEntityTypes.a.b(tileentitytypes);
    }

    public static <T extends TileEntity> TileEntityTypes<T> a(String s, TileEntityTypes.a<T> tileentitytypes_a) {
        Type type = null;

        try {
            type = DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(1519)).getChoiceType(DataConverterTypes.j, s);
        } catch (IllegalStateException illegalstateexception) {
            if (SharedConstants.b) {
                throw illegalstateexception;
            }

            TileEntityTypes.A.warn("No data fixer registered for block entity {}", s);
        }

        TileEntityTypes tileentitytypes = tileentitytypes_a.a(type);

        TileEntityTypes.a.a(new MinecraftKey(s), tileentitytypes);
        return tileentitytypes;
    }

    public TileEntityTypes(Supplier<? extends T> supplier, Type<?> type) {
        this.B = supplier;
        this.C = type;
    }

    @Nullable
    public T a() {
        return (TileEntity) this.B.get();
    }

    @Nullable
    static TileEntity a(String s) {
        TileEntityTypes tileentitytypes = (TileEntityTypes) TileEntityTypes.a.get(new MinecraftKey(s));

        return tileentitytypes == null ? null : tileentitytypes.a();
    }

    public static final class a<T extends TileEntity> {

        private final Supplier<? extends T> a;

        private a(Supplier<? extends T> supplier) {
            this.a = supplier;
        }

        public static <T extends TileEntity> TileEntityTypes.a<T> a(Supplier<? extends T> supplier) {
            return new TileEntityTypes.a(supplier);
        }

        public TileEntityTypes<T> a(Type<?> type) {
            return new TileEntityTypes(this.a, type);
        }
    }
}
