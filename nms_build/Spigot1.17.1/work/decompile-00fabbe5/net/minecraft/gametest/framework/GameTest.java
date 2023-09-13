package net.minecraft.gametest.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTest {

    int a() default 100;

    String b() default "defaultBatch";

    int c() default 0;

    boolean d() default true;

    String e() default "";

    long f() default 0L;

    int g() default 1;

    int h() default 1;
}
