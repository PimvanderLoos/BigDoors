package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.util.MathHelper;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;

public class ArgumentVectorPosition implements IVectorPosition {

    public static final char PREFIX_LOCAL_COORDINATE = '^';
    private final double left;
    private final double up;
    private final double forwards;

    public ArgumentVectorPosition(double d0, double d1, double d2) {
        this.left = d0;
        this.up = d1;
        this.forwards = d2;
    }

    @Override
    public Vec3D getPosition(CommandListenerWrapper commandlistenerwrapper) {
        Vec2F vec2f = commandlistenerwrapper.getRotation();
        Vec3D vec3d = commandlistenerwrapper.getAnchor().apply(commandlistenerwrapper);
        float f = MathHelper.cos((vec2f.y + 90.0F) * 0.017453292F);
        float f1 = MathHelper.sin((vec2f.y + 90.0F) * 0.017453292F);
        float f2 = MathHelper.cos(-vec2f.x * 0.017453292F);
        float f3 = MathHelper.sin(-vec2f.x * 0.017453292F);
        float f4 = MathHelper.cos((-vec2f.x + 90.0F) * 0.017453292F);
        float f5 = MathHelper.sin((-vec2f.x + 90.0F) * 0.017453292F);
        Vec3D vec3d1 = new Vec3D((double) (f * f2), (double) f3, (double) (f1 * f2));
        Vec3D vec3d2 = new Vec3D((double) (f * f4), (double) f5, (double) (f1 * f4));
        Vec3D vec3d3 = vec3d1.cross(vec3d2).scale(-1.0D);
        double d0 = vec3d1.x * this.forwards + vec3d2.x * this.up + vec3d3.x * this.left;
        double d1 = vec3d1.y * this.forwards + vec3d2.y * this.up + vec3d3.y * this.left;
        double d2 = vec3d1.z * this.forwards + vec3d2.z * this.up + vec3d3.z * this.left;

        return new Vec3D(vec3d.x + d0, vec3d.y + d1, vec3d.z + d2);
    }

    @Override
    public Vec2F getRotation(CommandListenerWrapper commandlistenerwrapper) {
        return Vec2F.ZERO;
    }

    @Override
    public boolean isXRelative() {
        return true;
    }

    @Override
    public boolean isYRelative() {
        return true;
    }

    @Override
    public boolean isZRelative() {
        return true;
    }

    public static ArgumentVectorPosition parse(StringReader stringreader) throws CommandSyntaxException {
        int i = stringreader.getCursor();
        double d0 = readDouble(stringreader, i);

        if (stringreader.canRead() && stringreader.peek() == ' ') {
            stringreader.skip();
            double d1 = readDouble(stringreader, i);

            if (stringreader.canRead() && stringreader.peek() == ' ') {
                stringreader.skip();
                double d2 = readDouble(stringreader, i);

                return new ArgumentVectorPosition(d0, d1, d2);
            } else {
                stringreader.setCursor(i);
                throw ArgumentVec3.ERROR_NOT_COMPLETE.createWithContext(stringreader);
            }
        } else {
            stringreader.setCursor(i);
            throw ArgumentVec3.ERROR_NOT_COMPLETE.createWithContext(stringreader);
        }
    }

    private static double readDouble(StringReader stringreader, int i) throws CommandSyntaxException {
        if (!stringreader.canRead()) {
            throw ArgumentParserPosition.ERROR_EXPECTED_DOUBLE.createWithContext(stringreader);
        } else if (stringreader.peek() != '^') {
            stringreader.setCursor(i);
            throw ArgumentVec3.ERROR_MIXED_TYPE.createWithContext(stringreader);
        } else {
            stringreader.skip();
            return stringreader.canRead() && stringreader.peek() != ' ' ? stringreader.readDouble() : 0.0D;
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ArgumentVectorPosition)) {
            return false;
        } else {
            ArgumentVectorPosition argumentvectorposition = (ArgumentVectorPosition) object;

            return this.left == argumentvectorposition.left && this.up == argumentvectorposition.up && this.forwards == argumentvectorposition.forwards;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.left, this.up, this.forwards});
    }
}
