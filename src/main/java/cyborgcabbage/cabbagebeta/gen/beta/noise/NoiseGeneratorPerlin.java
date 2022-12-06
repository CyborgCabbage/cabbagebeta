package cyborgcabbage.cabbagebeta.gen.beta.noise;

import java.util.Random;

public class NoiseGeneratorPerlin {
    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public NoiseGeneratorPerlin() {
        this(new Random());
    }

    public NoiseGeneratorPerlin(Random random1) {
        this.permutations = new int[512];
        this.xCoord = random1.nextDouble() * 256.0D;
        this.yCoord = random1.nextDouble() * 256.0D;
        this.zCoord = random1.nextDouble() * 256.0D;

        int i2;
        for(i2 = 0; i2 < 256; this.permutations[i2] = i2++) {
        }

        for(i2 = 0; i2 < 256; ++i2) {
            int i3 = random1.nextInt(256 - i2) + i2;
            int i4 = this.permutations[i2];
            this.permutations[i2] = this.permutations[i3];
            this.permutations[i3] = i4;
            this.permutations[i2 + 256] = this.permutations[i2];
        }

    }

    public double generateNoise(double d1, double d3, double d5) {
        double d7 = d1 + this.xCoord;
        double d9 = d3 + this.yCoord;
        double d11 = d5 + this.zCoord;
        int i13 = (int)d7;
        int i14 = (int)d9;
        int i15 = (int)d11;
        if(d7 < (double)i13) {
            --i13;
        }

        if(d9 < (double)i14) {
            --i14;
        }

        if(d11 < (double)i15) {
            --i15;
        }

        int i16 = i13 & 255;
        int i17 = i14 & 255;
        int i18 = i15 & 255;
        d7 -= (double)i13;
        d9 -= (double)i14;
        d11 -= (double)i15;
        double d19 = d7 * d7 * d7 * (d7 * (d7 * 6.0D - 15.0D) + 10.0D);
        double d21 = d9 * d9 * d9 * (d9 * (d9 * 6.0D - 15.0D) + 10.0D);
        double d23 = d11 * d11 * d11 * (d11 * (d11 * 6.0D - 15.0D) + 10.0D);
        int i25 = this.permutations[i16] + i17;
        int i26 = this.permutations[i25] + i18;
        int i27 = this.permutations[i25 + 1] + i18;
        int i28 = this.permutations[i16 + 1] + i17;
        int i29 = this.permutations[i28] + i18;
        int i30 = this.permutations[i28 + 1] + i18;
        return this.lerp(d23, this.lerp(d21, this.lerp(d19, this.grad(this.permutations[i26], d7, d9, d11), this.grad(this.permutations[i29], d7 - 1.0D, d9, d11)), this.lerp(d19, this.grad(this.permutations[i27], d7, d9 - 1.0D, d11), this.grad(this.permutations[i30], d7 - 1.0D, d9 - 1.0D, d11))), this.lerp(d21, this.lerp(d19, this.grad(this.permutations[i26 + 1], d7, d9, d11 - 1.0D), this.grad(this.permutations[i29 + 1], d7 - 1.0D, d9, d11 - 1.0D)), this.lerp(d19, this.grad(this.permutations[i27 + 1], d7, d9 - 1.0D, d11 - 1.0D), this.grad(this.permutations[i30 + 1], d7 - 1.0D, d9 - 1.0D, d11 - 1.0D))));
    }

    public final double lerp(double d1, double d3, double d5) {
        return d3 + d1 * (d5 - d3);
    }

    public final double func_4110_a(int i1, double d2, double d4) {
        int i6 = i1 & 15;
        double d7 = (double)(1 - ((i6 & 8) >> 3)) * d2;
        double d9 = i6 < 4 ? 0.0D : (i6 != 12 && i6 != 14 ? d4 : d2);
        return ((i6 & 1) == 0 ? d7 : -d7) + ((i6 & 2) == 0 ? d9 : -d9);
    }

    public final double grad(int i1, double d2, double d4, double d6) {
        int i8 = i1 & 15;
        double d9 = i8 < 8 ? d2 : d4;
        double d11 = i8 < 4 ? d4 : (i8 != 12 && i8 != 14 ? d6 : d2);
        return ((i8 & 1) == 0 ? d9 : -d9) + ((i8 & 2) == 0 ? d11 : -d11);
    }

    public double func_801_a(double d1, double d3) {
        return this.generateNoise(d1, d3, 0.0D);
    }

    public void func_805_a(double[] output, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double inverseMagnitude) {
        int i10001;
        int i19;
        int i22;
        double d31;
        double d35;
        int i37;
        double d38;
        int i40;
        int i41;
        double d42;
        int i75;
        if(ySize == 1) {
            boolean z64 = false;
            boolean z65 = false;
            boolean z21 = false;
            boolean z68 = false;
            double d70 = 0.0D;
            double d73 = 0.0D;
            i75 = 0;
            double d77 = 1.0D / inverseMagnitude;

            for(int i30 = 0; i30 < xSize; ++i30) {
                d31 = (xOffset + (double)i30) * xScale + this.xCoord;
                int i78 = (int)d31;
                if(d31 < (double)i78) {
                    --i78;
                }

                int i34 = i78 & 255;
                d31 -= (double)i78;
                d35 = d31 * d31 * d31 * (d31 * (d31 * 6.0D - 15.0D) + 10.0D);

                for(i37 = 0; i37 < zSize; ++i37) {
                    d38 = (zOffset + (double)i37) * zScale + this.zCoord;
                    i40 = (int)d38;
                    if(d38 < (double)i40) {
                        --i40;
                    }

                    i41 = i40 & 255;
                    d38 -= (double)i40;
                    d42 = d38 * d38 * d38 * (d38 * (d38 * 6.0D - 15.0D) + 10.0D);
                    i19 = this.permutations[i34] + 0;
                    int i66 = this.permutations[i19] + i41;
                    int i67 = this.permutations[i34 + 1] + 0;
                    i22 = this.permutations[i67] + i41;
                    d70 = this.lerp(d35, this.func_4110_a(this.permutations[i66], d31, d38), this.grad(this.permutations[i22], d31 - 1.0D, 0.0D, d38));
                    d73 = this.lerp(d35, this.grad(this.permutations[i66 + 1], d31, 0.0D, d38 - 1.0D), this.grad(this.permutations[i22 + 1], d31 - 1.0D, 0.0D, d38 - 1.0D));
                    double d79 = this.lerp(d42, d70, d73);
                    i10001 = i75++;
                    output[i10001] += d79 * d77;
                }
            }

        } else {
            i19 = 0;
            double d20 = 1.0D / inverseMagnitude;
            i22 = -1;
            boolean z23 = false;
            boolean z24 = false;
            boolean z25 = false;
            boolean z26 = false;
            boolean z27 = false;
            boolean z28 = false;
            double d29 = 0.0D;
            d31 = 0.0D;
            double d33 = 0.0D;
            d35 = 0.0D;

            for(i37 = 0; i37 < xSize; ++i37) {
                d38 = (xOffset + (double)i37) * xScale + this.xCoord;
                i40 = (int)d38;
                if(d38 < (double)i40) {
                    --i40;
                }

                i41 = i40 & 255;
                d38 -= (double)i40;
                d42 = d38 * d38 * d38 * (d38 * (d38 * 6.0D - 15.0D) + 10.0D);

                for(int i44 = 0; i44 < zSize; ++i44) {
                    double d45 = (zOffset + (double)i44) * zScale + this.zCoord;
                    int i47 = (int)d45;
                    if(d45 < (double)i47) {
                        --i47;
                    }

                    int i48 = i47 & 255;
                    d45 -= (double)i47;
                    double d49 = d45 * d45 * d45 * (d45 * (d45 * 6.0D - 15.0D) + 10.0D);

                    for(int i51 = 0; i51 < ySize; ++i51) {
                        double d52 = (yOffset + (double)i51) * yScale + this.yCoord;
                        int i54 = (int)d52;
                        if(d52 < (double)i54) {
                            --i54;
                        }

                        int i55 = i54 & 255;
                        d52 -= (double)i54;
                        double d56 = d52 * d52 * d52 * (d52 * (d52 * 6.0D - 15.0D) + 10.0D);
                        if(i51 == 0 || i55 != i22) {
                            i22 = i55;
                            int i69 = this.permutations[i41] + i55;
                            int i71 = this.permutations[i69] + i48;
                            int i72 = this.permutations[i69 + 1] + i48;
                            int i74 = this.permutations[i41 + 1] + i55;
                            i75 = this.permutations[i74] + i48;
                            int i76 = this.permutations[i74 + 1] + i48;
                            d29 = this.lerp(d42, this.grad(this.permutations[i71], d38, d52, d45), this.grad(this.permutations[i75], d38 - 1.0D, d52, d45));
                            d31 = this.lerp(d42, this.grad(this.permutations[i72], d38, d52 - 1.0D, d45), this.grad(this.permutations[i76], d38 - 1.0D, d52 - 1.0D, d45));
                            d33 = this.lerp(d42, this.grad(this.permutations[i71 + 1], d38, d52, d45 - 1.0D), this.grad(this.permutations[i75 + 1], d38 - 1.0D, d52, d45 - 1.0D));
                            d35 = this.lerp(d42, this.grad(this.permutations[i72 + 1], d38, d52 - 1.0D, d45 - 1.0D), this.grad(this.permutations[i76 + 1], d38 - 1.0D, d52 - 1.0D, d45 - 1.0D));
                        }

                        double d58 = this.lerp(d56, d29, d31);
                        double d60 = this.lerp(d56, d33, d35);
                        double d62 = this.lerp(d49, d58, d60);
                        i10001 = i19++;
                        output[i10001] += d62 * d20;
                    }
                }
            }

        }
    }
}

