package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;

public class SpawnerCreatureProbabilities {

    private final List<SpawnerCreatureProbabilities.a> charges = Lists.newArrayList();

    public SpawnerCreatureProbabilities() {}

    public void a(BlockPosition blockposition, double d0) {
        if (d0 != 0.0D) {
            this.charges.add(new SpawnerCreatureProbabilities.a(blockposition, d0));
        }

    }

    public double b(BlockPosition blockposition, double d0) {
        if (d0 == 0.0D) {
            return 0.0D;
        } else {
            double d1 = 0.0D;

            SpawnerCreatureProbabilities.a spawnercreatureprobabilities_a;

            for (Iterator iterator = this.charges.iterator(); iterator.hasNext(); d1 += spawnercreatureprobabilities_a.a(blockposition)) {
                spawnercreatureprobabilities_a = (SpawnerCreatureProbabilities.a) iterator.next();
            }

            return d1 * d0;
        }
    }

    private static class a {

        private final BlockPosition pos;
        private final double charge;

        public a(BlockPosition blockposition, double d0) {
            this.pos = blockposition;
            this.charge = d0;
        }

        public double a(BlockPosition blockposition) {
            double d0 = this.pos.j(blockposition);

            return d0 == 0.0D ? Double.POSITIVE_INFINITY : this.charge / Math.sqrt(d0);
        }
    }
}
