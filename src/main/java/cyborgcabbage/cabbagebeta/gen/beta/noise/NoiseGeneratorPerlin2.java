package cyborgcabbage.cabbagebeta.gen.beta.noise;

import java.util.Random;

public class NoiseGeneratorPerlin2 {
    private static int[][] field_4296_d = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    private int[] field_4295_e;
    public double field_4292_a;
    public double field_4291_b;
    public double field_4297_c;
    private static final double field_4294_f = 0.5D * (Math.sqrt(3.0D) - 1.0D);
    private static final double field_4293_g = (3.0D - Math.sqrt(3.0D)) / 6.0D;

    public NoiseGeneratorPerlin2() {
        this(new Random());
    }

    public NoiseGeneratorPerlin2(Random random1) {
        this.field_4295_e = new int[512];
        this.field_4292_a = random1.nextDouble() * 256.0D;
        this.field_4291_b = random1.nextDouble() * 256.0D;
        this.field_4297_c = random1.nextDouble() * 256.0D;

        int i2;
        for(i2 = 0; i2 < 256; this.field_4295_e[i2] = i2++) {
        }

        for(i2 = 0; i2 < 256; ++i2) {
            int i3 = random1.nextInt(256 - i2) + i2;
            int i4 = this.field_4295_e[i2];
            this.field_4295_e[i2] = this.field_4295_e[i3];
            this.field_4295_e[i3] = i4;
            this.field_4295_e[i2 + 256] = this.field_4295_e[i2];
        }

    }

    private static int wrap(double d0) {
        return d0 > 0.0D ? (int)d0 : (int)d0 - 1;
    }

    private static double func_4156_a(int[] i0, double d1, double d3) {
        return (double)i0[0] * d1 + (double)i0[1] * d3;
    }

    public void func_4157_a(double[] d1, double d2, double d4, int i6, int i7, double d8, double d10, double d12) {
        int i14 = 0;

        for(int i15 = 0; i15 < i6; ++i15) {
            double d16 = (d2 + (double)i15) * d8 + this.field_4292_a;

            for(int i18 = 0; i18 < i7; ++i18) {
                double d19 = (d4 + (double)i18) * d10 + this.field_4291_b;
                double d27 = (d16 + d19) * field_4294_f;
                int i29 = wrap(d16 + d27);
                int i30 = wrap(d19 + d27);
                double d31 = (double)(i29 + i30) * field_4293_g;
                double d33 = (double)i29 - d31;
                double d35 = (double)i30 - d31;
                double d37 = d16 - d33;
                double d39 = d19 - d35;
                byte b41;
                byte b42;
                if(d37 > d39) {
                    b41 = 1;
                    b42 = 0;
                } else {
                    b41 = 0;
                    b42 = 1;
                }

                double d43 = d37 - (double)b41 + field_4293_g;
                double d45 = d39 - (double)b42 + field_4293_g;
                double d47 = d37 - 1.0D + 2.0D * field_4293_g;
                double d49 = d39 - 1.0D + 2.0D * field_4293_g;
                int i51 = i29 & 255;
                int i52 = i30 & 255;
                int i53 = this.field_4295_e[i51 + this.field_4295_e[i52]] % 12;
                int i54 = this.field_4295_e[i51 + b41 + this.field_4295_e[i52 + b42]] % 12;
                int i55 = this.field_4295_e[i51 + 1 + this.field_4295_e[i52 + 1]] % 12;
                double d56 = 0.5D - d37 * d37 - d39 * d39;
                double d21;
                if(d56 < 0.0D) {
                    d21 = 0.0D;
                } else {
                    d56 *= d56;
                    d21 = d56 * d56 * func_4156_a(field_4296_d[i53], d37, d39);
                }

                double d58 = 0.5D - d43 * d43 - d45 * d45;
                double d23;
                if(d58 < 0.0D) {
                    d23 = 0.0D;
                } else {
                    d58 *= d58;
                    d23 = d58 * d58 * func_4156_a(field_4296_d[i54], d43, d45);
                }

                double d60 = 0.5D - d47 * d47 - d49 * d49;
                double d25;
                if(d60 < 0.0D) {
                    d25 = 0.0D;
                } else {
                    d60 *= d60;
                    d25 = d60 * d60 * func_4156_a(field_4296_d[i55], d47, d49);
                }

                int i10001 = i14++;
                d1[i10001] += 70.0D * (d21 + d23 + d25) * d12;
            }
        }

    }
}

