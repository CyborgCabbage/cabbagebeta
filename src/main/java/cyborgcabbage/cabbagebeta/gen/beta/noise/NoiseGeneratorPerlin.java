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
        return this.lerp(d23, this.lerp(d21, this.lerp(d19, this.grad2(this.permutations[i26], d7, d9, d11), this.grad2(this.permutations[i29], d7 - 1.0D, d9, d11)), this.lerp(d19, this.grad2(this.permutations[i27], d7, d9 - 1.0D, d11), this.grad2(this.permutations[i30], d7 - 1.0D, d9 - 1.0D, d11))), this.lerp(d21, this.lerp(d19, this.grad2(this.permutations[i26 + 1], d7, d9, d11 - 1.0D), this.grad2(this.permutations[i29 + 1], d7 - 1.0D, d9, d11 - 1.0D)), this.lerp(d19, this.grad2(this.permutations[i27 + 1], d7, d9 - 1.0D, d11 - 1.0D), this.grad2(this.permutations[i30 + 1], d7 - 1.0D, d9 - 1.0D, d11 - 1.0D))));
    }

    public final double lerp(double d1, double d3, double d5) {
        return d3 + d1 * (d5 - d3);
    }

    public final float lerp(float d1, float d3, float d5) {
        return d3 + d1 * (d5 - d3);
    }

    public final double grad1(int i1, double a, double b) {
        int _4bits = i1 & 15;
        double d0 = (double)(1 - ((_4bits & 8) >> 3)) * a;
        double d1 = _4bits < 4 ? 0.0D : (_4bits != 12 && _4bits != 14 ? b : a);
        return ((_4bits & 1) == 0 ? d0 : -d0) + ((_4bits & 2) == 0 ? d1 : -d1);
    }

    public final double grad2(int i1, double a, double b, double c) {
        int _4bits = i1 & 15;
        double d0 = _4bits < 8 ? a : b;
        double d1 = _4bits < 4 ? b : (_4bits != 12 && _4bits != 14 ? c : a);
        return ((_4bits & 1) == 0 ? d0 : -d0) + ((_4bits & 2) == 0 ? d1 : -d1);
    }

    public final float grad1(int i1, float a, float b) {
        int _4bits = i1 & 15;
        float d0 = (float)(1 - ((_4bits & 8) >> 3)) * a;
        float d1 = _4bits < 4 ? 0.f : (_4bits != 12 && _4bits != 14 ? b : a);
        return ((_4bits & 1) == 0 ? d0 : -d0) + ((_4bits & 2) == 0 ? d1 : -d1);
    }

    public final float grad2(int i1, float a, float b, float c) {
        int _4bits = i1 & 15;
        float d0 = _4bits < 8 ? a : b;
        float d1 = _4bits < 4 ? b : (_4bits != 12 && _4bits != 14 ? c : a);
        return ((_4bits & 1) == 0 ? d0 : -d0) + ((_4bits & 2) == 0 ? d1 : -d1);
    }

    public double func_801_a(double d1, double d3) {
        return this.generateNoise(d1, d3, 0.0D);
    }

    public void func_805_a(float[] output, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, float inverseMagnitude) {
        if(ySize == 1) {
            boolean z64 = false;
            boolean z65 = false;
            boolean z21 = false;
            boolean z68 = false;
            int i75 = 0;
            float inverse = 1.0f/inverseMagnitude;

            for(int xi = 0; xi < xSize; ++xi) {
                double x = (xOffset + (double)xi) * xScale + this.xCoord;
                double xFloored = Math.floor(x);
                int xCell = (int)xFloored & 255;
                float xFrac = (float)(x-xFloored);//z = fractional component
                float xSmooth = xFrac * xFrac * xFrac * (xFrac * (xFrac * 6.f - 15.f) + 10.f);
                for(int zi = 0; zi < zSize; ++zi) {
                    double z = (zOffset + (double)zi) * zScale + this.zCoord;
                    double zFloored = Math.floor(z);
                    int zCell = (int)zFloored & 255;
                    float zFrac = (float)(z-zFloored);//z = fractional component
                    float zSmooth = zFrac * zFrac * zFrac * (zFrac * (zFrac * 6.f - 15.f) + 10.f);

                    int i19 = this.permutations[xCell];
                    int i66 = this.permutations[i19] + zCell;
                    int i67 = this.permutations[xCell + 1];
                    int i22 = this.permutations[i67] + zCell;
                    float d70 = this.lerp(xSmooth, this.grad1(this.permutations[i66], xFrac, zFrac), this.grad2(this.permutations[i22], xFrac - 1.f, 0.f, zFrac));
                    float d73 = this.lerp(xSmooth, this.grad2(this.permutations[i66 + 1], xFrac, 0.f, zFrac - 1.f), this.grad2(this.permutations[i22 + 1], xFrac - 1.f, 0.f, zFrac - 1.f));
                    float d79 = this.lerp(zSmooth, d70, d73);
                    int i10001 = i75++;
                    output[i10001] += d79 * inverse;
                }
            }

        } else {
            int i19 = 0;
            float d20 = 1.f / inverseMagnitude;
            int i22 = -1;
            float d29 = 0.f;
            float d31 = 0.f;
            float d33 = 0.f;
            float d35 = 0.f;
            for(int xi = 0; xi < xSize; ++xi) {
                double x = (xOffset + (double)xi) * xScale + this.xCoord;
                double xFloored = Math.floor(x);
                int xCell = (int)xFloored & 255;
                float xFrac = (float)(x-xFloored);
                float xSmooth = xFrac * xFrac * xFrac * (xFrac * (xFrac * 6.f - 15.f) + 10.f);

                for(int zi = 0; zi < zSize; ++zi) {
                    double z = (zOffset + (double)zi) * zScale + this.zCoord;
                    double zFloored = Math.floor(z);
                    int zCell = (int)zFloored & 255;
                    float zFrac = (float)(z-zFloored);
                    float zSmooth = zFrac * zFrac * zFrac * (zFrac * (zFrac * 6.f - 15.f) + 10.f);

                    for(int yi = 0; yi < ySize; ++yi) {
                        double y = (yOffset + (double)yi) * yScale + this.yCoord;
                        double yFloored = Math.floor(y);
                        int yCell = (int)yFloored & 255;
                        float yFrac = (float)(y-yFloored);
                        float ySmooth = yFrac * yFrac * yFrac * (yFrac * (yFrac * 6.f - 15.f) + 10.f);
                        if(yi == 0 || yCell != i22) {
                            i22 = yCell;
                            int i69 = this.permutations[xCell] + yCell;
                            int i71 = this.permutations[i69] + zCell;
                            int i72 = this.permutations[i69 + 1] + zCell;
                            int i74 = this.permutations[xCell + 1] + yCell;
                            int i75 = this.permutations[i74] + zCell;
                            int i76 = this.permutations[i74 + 1] + zCell;
                            d29 = this.lerp(xSmooth, this.grad2(this.permutations[i71], xFrac, yFrac, zFrac), this.grad2(this.permutations[i75], xFrac - 1.f, yFrac, zFrac));
                            d31 = this.lerp(xSmooth, this.grad2(this.permutations[i72], xFrac, yFrac - 1.f, zFrac), this.grad2(this.permutations[i76], xFrac - 1.f, yFrac - 1.f, zFrac));
                            d33 = this.lerp(xSmooth, this.grad2(this.permutations[i71 + 1], xFrac, yFrac, zFrac - 1.f), this.grad2(this.permutations[i75 + 1], xFrac - 1.f, yFrac, zFrac - 1.f));
                            d35 = this.lerp(xSmooth, this.grad2(this.permutations[i72 + 1], xFrac, yFrac - 1.f, zFrac - 1.f), this.grad2(this.permutations[i76 + 1], xFrac - 1.f, yFrac - 1.f, zFrac - 1.f));
                        }

                        float d58 = this.lerp(ySmooth, d29, d31);
                        float d60 = this.lerp(ySmooth, d33, d35);
                        float d62 = this.lerp(zSmooth, d58, d60);
                        int i10001 = i19++;
                        output[i10001] += d62 * d20;
                    }
                }
            }

        }
    }
}

