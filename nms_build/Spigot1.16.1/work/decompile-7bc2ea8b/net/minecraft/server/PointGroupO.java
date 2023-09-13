package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public enum PointGroupO implements INamable {

    IDENTITY("identity", PointGroupS.P123, false, false, false), ROT_180_FACE_XY("rot_180_face_xy", PointGroupS.P123, true, true, false), ROT_180_FACE_XZ("rot_180_face_xz", PointGroupS.P123, true, false, true), ROT_180_FACE_YZ("rot_180_face_yz", PointGroupS.P123, false, true, true), ROT_120_NNN("rot_120_nnn", PointGroupS.P231, false, false, false), ROT_120_NNP("rot_120_nnp", PointGroupS.P312, true, false, true), ROT_120_NPN("rot_120_npn", PointGroupS.P312, false, true, true), ROT_120_NPP("rot_120_npp", PointGroupS.P231, true, false, true), ROT_120_PNN("rot_120_pnn", PointGroupS.P312, true, true, false), ROT_120_PNP("rot_120_pnp", PointGroupS.P231, true, true, false), ROT_120_PPN("rot_120_ppn", PointGroupS.P231, false, true, true), ROT_120_PPP("rot_120_ppp", PointGroupS.P312, false, false, false), ROT_180_EDGE_XY_NEG("rot_180_edge_xy_neg", PointGroupS.P213, true, true, true), ROT_180_EDGE_XY_POS("rot_180_edge_xy_pos", PointGroupS.P213, false, false, true), ROT_180_EDGE_XZ_NEG("rot_180_edge_xz_neg", PointGroupS.P321, true, true, true), ROT_180_EDGE_XZ_POS("rot_180_edge_xz_pos", PointGroupS.P321, false, true, false), ROT_180_EDGE_YZ_NEG("rot_180_edge_yz_neg", PointGroupS.P132, true, true, true), ROT_180_EDGE_YZ_POS("rot_180_edge_yz_pos", PointGroupS.P132, true, false, false), ROT_90_X_NEG("rot_90_x_neg", PointGroupS.P132, false, false, true), ROT_90_X_POS("rot_90_x_pos", PointGroupS.P132, false, true, false), ROT_90_Y_NEG("rot_90_y_neg", PointGroupS.P321, true, false, false), ROT_90_Y_POS("rot_90_y_pos", PointGroupS.P321, false, false, true), ROT_90_Z_NEG("rot_90_z_neg", PointGroupS.P213, false, true, false), ROT_90_Z_POS("rot_90_z_pos", PointGroupS.P213, true, false, false), INVERSION("inversion", PointGroupS.P123, true, true, true), INVERT_X("invert_x", PointGroupS.P123, true, false, false), INVERT_Y("invert_y", PointGroupS.P123, false, true, false), INVERT_Z("invert_z", PointGroupS.P123, false, false, true), ROT_60_REF_NNN("rot_60_ref_nnn", PointGroupS.P312, true, true, true), ROT_60_REF_NNP("rot_60_ref_nnp", PointGroupS.P231, true, false, false), ROT_60_REF_NPN("rot_60_ref_npn", PointGroupS.P231, false, false, true), ROT_60_REF_NPP("rot_60_ref_npp", PointGroupS.P312, false, false, true), ROT_60_REF_PNN("rot_60_ref_pnn", PointGroupS.P231, false, true, false), ROT_60_REF_PNP("rot_60_ref_pnp", PointGroupS.P312, true, false, false), ROT_60_REF_PPN("rot_60_ref_ppn", PointGroupS.P312, false, true, false), ROT_60_REF_PPP("rot_60_ref_ppp", PointGroupS.P231, true, true, true), SWAP_XY("swap_xy", PointGroupS.P213, false, false, false), SWAP_YZ("swap_yz", PointGroupS.P132, false, false, false), SWAP_XZ("swap_xz", PointGroupS.P321, false, false, false), SWAP_NEG_XY("swap_neg_xy", PointGroupS.P213, true, true, false), SWAP_NEG_YZ("swap_neg_yz", PointGroupS.P132, false, true, true), SWAP_NEG_XZ("swap_neg_xz", PointGroupS.P321, true, false, true), ROT_90_REF_X_NEG("rot_90_ref_x_neg", PointGroupS.P132, true, false, true), ROT_90_REF_X_POS("rot_90_ref_x_pos", PointGroupS.P132, true, true, false), ROT_90_REF_Y_NEG("rot_90_ref_y_neg", PointGroupS.P321, true, true, false), ROT_90_REF_Y_POS("rot_90_ref_y_pos", PointGroupS.P321, false, true, true), ROT_90_REF_Z_NEG("rot_90_ref_z_neg", PointGroupS.P213, false, true, true), ROT_90_REF_Z_POS("rot_90_ref_z_pos", PointGroupS.P213, true, false, true);

    private final Matrix3f W;
    private final String X;
    @Nullable
    private Map<EnumDirection, EnumDirection> Y;
    private final boolean Z;
    private final boolean aa;
    private final boolean ab;
    private final PointGroupS ac;
    private static final PointGroupO[][] ad = (PointGroupO[][]) SystemUtils.a((Object) (new PointGroupO[values().length][values().length]), (apointgroupo) -> {
        Map<Pair<PointGroupS, BooleanList>, PointGroupO> map = (Map) Arrays.stream(values()).collect(Collectors.toMap((pointgroupo) -> {
            return Pair.of(pointgroupo.ac, pointgroupo.b());
        }, (pointgroupo) -> {
            return pointgroupo;
        }));
        PointGroupO[] apointgroupo1 = values();
        int i = apointgroupo1.length;

        for (int j = 0; j < i; ++j) {
            PointGroupO pointgroupo = apointgroupo1[j];
            PointGroupO[] apointgroupo2 = values();
            int k = apointgroupo2.length;

            for (int l = 0; l < k; ++l) {
                PointGroupO pointgroupo1 = apointgroupo2[l];
                BooleanList booleanlist = pointgroupo.b();
                BooleanList booleanlist1 = pointgroupo1.b();
                PointGroupS pointgroups = pointgroupo1.ac.a(pointgroupo.ac);
                BooleanArrayList booleanarraylist = new BooleanArrayList(3);

                for (int i1 = 0; i1 < 3; ++i1) {
                    booleanarraylist.add(booleanlist.getBoolean(i1) ^ booleanlist1.getBoolean(pointgroupo.ac.a(i1)));
                }

                apointgroupo[pointgroupo.ordinal()][pointgroupo1.ordinal()] = (PointGroupO) map.get(Pair.of(pointgroups, booleanarraylist));
            }
        }

    });
    private static final PointGroupO[] ae = (PointGroupO[]) Arrays.stream(values()).map((pointgroupo) -> {
        return (PointGroupO) Arrays.stream(values()).filter((pointgroupo1) -> {
            return pointgroupo.a(pointgroupo1) == PointGroupO.IDENTITY;
        }).findAny().get();
    }).toArray((i) -> {
        return new PointGroupO[i];
    });

    private PointGroupO(String s, PointGroupS pointgroups, boolean flag, boolean flag1, boolean flag2) {
        this.X = s;
        this.Z = flag;
        this.aa = flag1;
        this.ab = flag2;
        this.ac = pointgroups;
        this.W = new Matrix3f();
        this.W.a = flag ? -1.0F : 1.0F;
        this.W.e = flag1 ? -1.0F : 1.0F;
        this.W.i = flag2 ? -1.0F : 1.0F;
        this.W.b(pointgroups.a());
    }

    private BooleanList b() {
        return new BooleanArrayList(new boolean[]{this.Z, this.aa, this.ab});
    }

    public PointGroupO a(PointGroupO pointgroupo) {
        return PointGroupO.ad[this.ordinal()][pointgroupo.ordinal()];
    }

    public String toString() {
        return this.X;
    }

    @Override
    public String getName() {
        return this.X;
    }

    public EnumDirection a(EnumDirection enumdirection) {
        if (this.Y == null) {
            this.Y = Maps.newEnumMap(EnumDirection.class);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection1 = aenumdirection[j];
                EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection1.n();
                EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection = enumdirection1.e();
                EnumDirection.EnumAxis enumdirection_enumaxis1 = EnumDirection.EnumAxis.values()[this.ac.a(enumdirection_enumaxis.ordinal())];
                EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection1 = this.a(enumdirection_enumaxis1) ? enumdirection_enumaxisdirection.c() : enumdirection_enumaxisdirection;
                EnumDirection enumdirection2 = EnumDirection.a(enumdirection_enumaxis1, enumdirection_enumaxisdirection1);

                this.Y.put(enumdirection1, enumdirection2);
            }
        }

        return (EnumDirection) this.Y.get(enumdirection);
    }

    public boolean a(EnumDirection.EnumAxis enumdirection_enumaxis) {
        switch (enumdirection_enumaxis) {
            case X:
                return this.Z;
            case Y:
                return this.aa;
            case Z:
            default:
                return this.ab;
        }
    }

    public BlockPropertyJigsawOrientation a(BlockPropertyJigsawOrientation blockpropertyjigsaworientation) {
        return BlockPropertyJigsawOrientation.a(this.a(blockpropertyjigsaworientation.b()), this.a(blockpropertyjigsaworientation.c()));
    }
}
