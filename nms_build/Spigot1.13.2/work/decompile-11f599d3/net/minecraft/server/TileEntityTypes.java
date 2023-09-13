package net.minecraft.server;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityTypes<T extends TileEntity> {

    private static final Logger z = LogManager.getLogger();
    public static final TileEntityTypes<TileEntityFurnace> FURNACE = a("furnace", TileEntityTypes.a.a(TileEntityFurnace::new));
    public static final TileEntityTypes<TileEntityChest> CHEST = a("chest", TileEntityTypes.a.a(TileEntityChest::new));
    public static final TileEntityTypes<TileEntityChestTrapped> TRAPPED_CHEST = a("trapped_chest", TileEntityTypes.a.a(TileEntityChestTrapped::new));
    public static final TileEntityTypes<TileEntityEnderChest> ENDER_CHEST = a("ender_chest", TileEntityTypes.a.a(TileEntityEnderChest::new));
    public static final TileEntityTypes<TileEntityJukeBox> JUKEBOX = a("jukebox", TileEntityTypes.a.a(TileEntityJukeBox::new));
    public static final TileEntityTypes<TileEntityDispenser> DISPENSER = a("dispenser", TileEntityTypes.a.a(TileEntityDispenser::new));
    public static final TileEntityTypes<TileEntityDropper> DROPPER = a("dropper", TileEntityTypes.a.a(TileEntityDropper::new));
    public static final TileEntityTypes<TileEntitySign> SIGN = a("sign", TileEntityTypes.a.a(TileEntitySign::new));
    public static final TileEntityTypes<TileEntityMobSpawner> MOB_SPAWNER = a("mob_spawner", TileEntityTypes.a.a(TileEntityMobSpawner::new));
    public static final TileEntityTypes<TileEntityPiston> PISTON = a("piston", TileEntityTypes.a.a(TileEntityPiston::new));
    public static final TileEntityTypes<TileEntityBrewingStand> BREWING_STAND = a("brewing_stand", TileEntityTypes.a.a(TileEntityBrewingStand::new));
    public static final TileEntityTypes<TileEntityEnchantTable> ENCHANTING_TABLE = a("enchanting_table", TileEntityTypes.a.a(TileEntityEnchantTable::new));
    public static final TileEntityTypes<TileEntityEnderPortal> END_PORTAL = a("end_portal", TileEntityTypes.a.a(TileEntityEnderPortal::new));
    public static final TileEntityTypes<TileEntityBeacon> BEACON = a("beacon", TileEntityTypes.a.a(TileEntityBeacon::new));
    public static final TileEntityTypes<TileEntitySkull> SKULL = a("skull", TileEntityTypes.a.a(TileEntitySkull::new));
    public static final TileEntityTypes<TileEntityLightDetector> DAYLIGHT_DETECTOR = a("daylight_detector", TileEntityTypes.a.a(TileEntityLightDetector::new));
    public static final TileEntityTypes<TileEntityHopper> HOPPER = a("hopper", TileEntityTypes.a.a(TileEntityHopper::new));
    public static final TileEntityTypes<TileEntityComparator> COMPARATOR = a("comparator", TileEntityTypes.a.a(TileEntityComparator::new));
    public static final TileEntityTypes<TileEntityBanner> BANNER = a("banner", TileEntityTypes.a.a(TileEntityBanner::new));
    public static final TileEntityTypes<TileEntityStructure> STRUCTURE_BLOCK = a("structure_block", TileEntityTypes.a.a(TileEntityStructure::new));
    public static final TileEntityTypes<TileEntityEndGateway> END_GATEWAY = a("end_gateway", TileEntityTypes.a.a(TileEntityEndGateway::new));
    public static final TileEntityTypes<TileEntityCommand> COMMAND_BLOCK = a("command_block", TileEntityTypes.a.a(TileEntityCommand::new));
    public static final TileEntityTypes<TileEntityShulkerBox> SHULKER_BOX = a("shulker_box", TileEntityTypes.a.a(TileEntityShulkerBox::new));
    public static final TileEntityTypes<TileEntityBed> BED = a("bed", TileEntityTypes.a.a(TileEntityBed::new));
    public static final TileEntityTypes<TileEntityConduit> CONDUIT = a("conduit", TileEntityTypes.a.a(TileEntityConduit::new));
    private final Supplier<? extends T> A;
    private final Type<?> B;

    @Nullable
    public static MinecraftKey a(TileEntityTypes<?> tileentitytypes) {
        return IRegistry.BLOCK_ENTITY_TYPE.getKey(tileentitytypes);
    }

    public static <T extends TileEntity> TileEntityTypes<T> a(String s, TileEntityTypes.a<T> tileentitytypes_a) {
        Type type = null;

        try {
            type = DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(DataConverterTypes.j, s);
        } catch (IllegalStateException illegalstateexception) {
            if (SharedConstants.b) {
                throw illegalstateexception;
            }

            TileEntityTypes.z.warn("No data fixer registered for block entity {}", s);
        }

        TileEntityTypes<T> tileentitytypes = tileentitytypes_a.a(type);

        IRegistry.BLOCK_ENTITY_TYPE.a(new MinecraftKey(s), (Object) tileentitytypes);
        return tileentitytypes;
    }

    public static void a() {}

    public TileEntityTypes(Supplier<? extends T> supplier, Type<?> type) {
        this.A = supplier;
        this.B = type;
    }

    @Nullable
    public T b() {
        return (TileEntity) this.A.get();
    }

    @Nullable
    static TileEntity a(String s) {
        TileEntityTypes<?> tileentitytypes = (TileEntityTypes) IRegistry.BLOCK_ENTITY_TYPE.get(new MinecraftKey(s));

        return tileentitytypes == null ? null : tileentitytypes.b();
    }

    public static final class a<T extends TileEntity> {

        private final Supplier<? extends T> a;

        private a(Supplier<? extends T> supplier) {
            this.a = supplier;
        }

        public static <T extends TileEntity> TileEntityTypes.a<T> a(Supplier<? extends T> supplier) {
            return new TileEntityTypes.a<>(supplier);
        }

        public TileEntityTypes<T> a(Type<?> type) {
            return new TileEntityTypes<>(this.a, type);
        }
    }
}
