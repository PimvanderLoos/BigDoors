package net.minecraft.world.level.levelgen.structure;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureBoundingBox {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<StructureBoundingBox> CODEC = Codec.INT_STREAM.comapFlatMap((intstream) -> {
        return SystemUtils.a(intstream, 6).map((aint) -> {
            return new StructureBoundingBox(aint[0], aint[1], aint[2], aint[3], aint[4], aint[5]);
        });
    }, (structureboundingbox) -> {
        return IntStream.of(new int[]{structureboundingbox.minX, structureboundingbox.minY, structureboundingbox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, structureboundingbox.maxZ});
    }).stable();
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public StructureBoundingBox(BlockPosition blockposition) {
        this(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public StructureBoundingBox(int i, int j, int k, int l, int i1, int j1) {
        this.minX = i;
        this.minY = j;
        this.minZ = k;
        this.maxX = l;
        this.maxY = i1;
        this.maxZ = j1;
        if (l < i || i1 < j || j1 < k) {
            String s = "Invalid bounding box data, inverted bounds for: " + this;

            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw new IllegalStateException(s);
            }

            StructureBoundingBox.LOGGER.error(s);
            this.minX = Math.min(i, l);
            this.minY = Math.min(j, i1);
            this.minZ = Math.min(k, j1);
            this.maxX = Math.max(i, l);
            this.maxY = Math.max(j, i1);
            this.maxZ = Math.max(k, j1);
        }

    }

    public static StructureBoundingBox a(BaseBlockPosition baseblockposition, BaseBlockPosition baseblockposition1) {
        return new StructureBoundingBox(Math.min(baseblockposition.getX(), baseblockposition1.getX()), Math.min(baseblockposition.getY(), baseblockposition1.getY()), Math.min(baseblockposition.getZ(), baseblockposition1.getZ()), Math.max(baseblockposition.getX(), baseblockposition1.getX()), Math.max(baseblockposition.getY(), baseblockposition1.getY()), Math.max(baseblockposition.getZ(), baseblockposition1.getZ()));
    }

    public static StructureBoundingBox a() {
        return new StructureBoundingBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static StructureBoundingBox a(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, EnumDirection enumdirection) {
        switch (enumdirection) {
            case SOUTH:
            default:
                return new StructureBoundingBox(i + l, j + i1, k + j1, i + k1 - 1 + l, j + l1 - 1 + i1, k + i2 - 1 + j1);
            case NORTH:
                return new StructureBoundingBox(i + l, j + i1, k - i2 + 1 + j1, i + k1 - 1 + l, j + l1 - 1 + i1, k + j1);
            case WEST:
                return new StructureBoundingBox(i - i2 + 1 + j1, j + i1, k + l, i + j1, j + l1 - 1 + i1, k + k1 - 1 + l);
            case EAST:
                return new StructureBoundingBox(i + j1, j + i1, k + l, i + i2 - 1 + j1, j + l1 - 1 + i1, k + k1 - 1 + l);
        }
    }

    public boolean a(StructureBoundingBox structureboundingbox) {
        return this.maxX >= structureboundingbox.minX && this.minX <= structureboundingbox.maxX && this.maxZ >= structureboundingbox.minZ && this.minZ <= structureboundingbox.maxZ && this.maxY >= structureboundingbox.minY && this.minY <= structureboundingbox.maxY;
    }

    public boolean a(int i, int j, int k, int l) {
        return this.maxX >= i && this.minX <= k && this.maxZ >= j && this.minZ <= l;
    }

    public static Optional<StructureBoundingBox> a(Iterable<BlockPosition> iterable) {
        Iterator<BlockPosition> iterator = iterable.iterator();

        if (!iterator.hasNext()) {
            return Optional.empty();
        } else {
            StructureBoundingBox structureboundingbox = new StructureBoundingBox((BlockPosition) iterator.next());

            Objects.requireNonNull(structureboundingbox);
            iterator.forEachRemaining(structureboundingbox::a);
            return Optional.of(structureboundingbox);
        }
    }

    public static Optional<StructureBoundingBox> b(Iterable<StructureBoundingBox> iterable) {
        Iterator<StructureBoundingBox> iterator = iterable.iterator();

        if (!iterator.hasNext()) {
            return Optional.empty();
        } else {
            StructureBoundingBox structureboundingbox = (StructureBoundingBox) iterator.next();
            StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(structureboundingbox.minX, structureboundingbox.minY, structureboundingbox.minZ, structureboundingbox.maxX, structureboundingbox.maxY, structureboundingbox.maxZ);

            Objects.requireNonNull(structureboundingbox1);
            iterator.forEachRemaining(structureboundingbox1::b);
            return Optional.of(structureboundingbox1);
        }
    }

    public StructureBoundingBox b(StructureBoundingBox structureboundingbox) {
        this.minX = Math.min(this.minX, structureboundingbox.minX);
        this.minY = Math.min(this.minY, structureboundingbox.minY);
        this.minZ = Math.min(this.minZ, structureboundingbox.minZ);
        this.maxX = Math.max(this.maxX, structureboundingbox.maxX);
        this.maxY = Math.max(this.maxY, structureboundingbox.maxY);
        this.maxZ = Math.max(this.maxZ, structureboundingbox.maxZ);
        return this;
    }

    public StructureBoundingBox a(BlockPosition blockposition) {
        this.minX = Math.min(this.minX, blockposition.getX());
        this.minY = Math.min(this.minY, blockposition.getY());
        this.minZ = Math.min(this.minZ, blockposition.getZ());
        this.maxX = Math.max(this.maxX, blockposition.getX());
        this.maxY = Math.max(this.maxY, blockposition.getY());
        this.maxZ = Math.max(this.maxZ, blockposition.getZ());
        return this;
    }

    public StructureBoundingBox a(int i) {
        this.minX -= i;
        this.minY -= i;
        this.minZ -= i;
        this.maxX += i;
        this.maxY += i;
        this.maxZ += i;
        return this;
    }

    public StructureBoundingBox a(int i, int j, int k) {
        this.minX += i;
        this.minY += j;
        this.minZ += k;
        this.maxX += i;
        this.maxY += j;
        this.maxZ += k;
        return this;
    }

    public StructureBoundingBox a(BaseBlockPosition baseblockposition) {
        return this.a(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public StructureBoundingBox b(int i, int j, int k) {
        return new StructureBoundingBox(this.minX + i, this.minY + j, this.minZ + k, this.maxX + i, this.maxY + j, this.maxZ + k);
    }

    public boolean b(BaseBlockPosition baseblockposition) {
        return baseblockposition.getX() >= this.minX && baseblockposition.getX() <= this.maxX && baseblockposition.getZ() >= this.minZ && baseblockposition.getZ() <= this.maxZ && baseblockposition.getY() >= this.minY && baseblockposition.getY() <= this.maxY;
    }

    public BaseBlockPosition b() {
        return new BaseBlockPosition(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
    }

    public int c() {
        return this.maxX - this.minX + 1;
    }

    public int d() {
        return this.maxY - this.minY + 1;
    }

    public int e() {
        return this.maxZ - this.minZ + 1;
    }

    public BlockPosition f() {
        return new BlockPosition(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
    }

    public void a(Consumer<BlockPosition> consumer) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        consumer.accept(blockposition_mutableblockposition.d(this.maxX, this.maxY, this.maxZ));
        consumer.accept(blockposition_mutableblockposition.d(this.minX, this.maxY, this.maxZ));
        consumer.accept(blockposition_mutableblockposition.d(this.maxX, this.minY, this.maxZ));
        consumer.accept(blockposition_mutableblockposition.d(this.minX, this.minY, this.maxZ));
        consumer.accept(blockposition_mutableblockposition.d(this.maxX, this.maxY, this.minZ));
        consumer.accept(blockposition_mutableblockposition.d(this.minX, this.maxY, this.minZ));
        consumer.accept(blockposition_mutableblockposition.d(this.maxX, this.minY, this.minZ));
        consumer.accept(blockposition_mutableblockposition.d(this.minX, this.minY, this.minZ));
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("minX", this.minX).add("minY", this.minY).add("minZ", this.minZ).add("maxX", this.maxX).add("maxY", this.maxY).add("maxZ", this.maxZ).toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof StructureBoundingBox)) {
            return false;
        } else {
            StructureBoundingBox structureboundingbox = (StructureBoundingBox) object;

            return this.minX == structureboundingbox.minX && this.minY == structureboundingbox.minY && this.minZ == structureboundingbox.minZ && this.maxX == structureboundingbox.maxX && this.maxY == structureboundingbox.maxY && this.maxZ == structureboundingbox.maxZ;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
    }

    public int g() {
        return this.minX;
    }

    public int h() {
        return this.minY;
    }

    public int i() {
        return this.minZ;
    }

    public int j() {
        return this.maxX;
    }

    public int k() {
        return this.maxY;
    }

    public int l() {
        return this.maxZ;
    }
}
