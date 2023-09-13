package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;

public class SpawnerCreatureProbabilities {

    private final List<SpawnerCreatureProbabilities.a> a = Lists.newArrayList();

    public SpawnerCreatureProbabilities() {}

    public void a(BlockPosition blockposition, double d0) {
        if (d0 != 0.0D) {
            this.a.add(new SpawnerCreatureProbabilities.a(blockposition, d0));
        }

    }

    public double b(BlockPosition blockposition, double d0) {
        if (d0 == 0.0D) {
            return 0.0D;
        } else {
            double d1 = 0.0D;

            SpawnerCreatureProbabilities.a spawnercreatureprobabilities_a;

            for (Iterator iterator = this.a.iterator(); iterator.hasNext(); d1 += spawnercreatureprobabilities_a.a(blockposition)) {
                spawnercreatureprobabilities_a = (SpawnerCreatureProbabilities.a) iterator.next();
            }

            return d1 * d0;
        }
    }

    static class a {

        private final BlockPosition a;
        private final double b;

        public a(BlockPosition blockposition, double d0) {
            this.a = blockposition;
            this.b = d0;
        }

        public double a(BlockPosition blockposition) {
            double d0 = this.a.j(blockposition);

            return d0 == 0.0D ? Double.POSITIVE_INFINITY : this.b / Math.sqrt(d0);
        }
    }
}
