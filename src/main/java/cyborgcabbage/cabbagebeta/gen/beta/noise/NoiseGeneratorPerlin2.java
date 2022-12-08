package cyborgcabbage.cabbagebeta.gen.beta.noise;

import java.util.Random;

public class NoiseGeneratorPerlin2 {
    private static int[][] diagonals = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    private int[] permutations;
    public double originX;
    public double originY;
    public double originZ;
    private static final float constant0 = 0.5f * ((float)Math.sqrt(3.0f) - 1.0f);
    private static final float constant1 = (3.0f - (float)Math.sqrt(3.0f)) / 6.0f;

    public NoiseGeneratorPerlin2() {
        this(new Random());
    }

    public NoiseGeneratorPerlin2(Random random1) {
        this.permutations = new int[512];
        this.originX = random1.nextDouble() * 256.0D;
        this.originY = random1.nextDouble() * 256.0D;
        this.originZ = random1.nextDouble() * 256.0D;

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

    private static int wrap(double d0) {
        return d0 > 0.0D ? (int)d0 : (int)d0 - 1;
    }

    private static double dotArray(int[] i0, double d1, double d3) {
        return (double)i0[0] * d1 + (double)i0[1] * d3;
    }

    public void func_4157_a(float[] output, double xOffset, double yOffset, int xSize, int ySize, double xScale, double yScale, float magnitudeScale) {
        int i = 0;
        for(int xi = 0; xi < xSize; ++xi) {
            double x = (xOffset + (double)xi) * xScale + this.originX;
            for(int yi = 0; yi < ySize; ++yi) {
                double y = (yOffset + (double)yi) * yScale + this.originY;
                double v0 = (x + y) * constant0;
                int xFloored = wrap(x + v0);
                int yFloored = wrap(y + v0);
                double v1 = (double)(xFloored + yFloored) * constant1;
                double vxFloor = (double)xFloored - v1;
                double vyFloor = (double)yFloored - v1;
                double vx0 = x - vxFloor;
                double vy0 = y - vyFloor;
                byte bx;
                byte by;
                if(vx0 > vy0) {
                    bx = 1;
                    by = 0;
                } else {
                    bx = 0;
                    by = 1;
                }

                double vx1 = vx0 - (double)bx + constant1;
                double vy1 = vy0 - (double)by + constant1;
                double vx2 = vx0 - 1.0D + 2.0D * constant1;
                double vy2 = vy0 - 1.0D + 2.0D * constant1;
                int xCell = xFloored & 255;
                int yCell = yFloored & 255;
                int p0 = this.permutations[xCell + this.permutations[yCell]] % 12;
                int p1 = this.permutations[xCell + bx + this.permutations[yCell + by]] % 12;
                int p2 = this.permutations[xCell + 1 + this.permutations[yCell + 1]] % 12;
                double sv0 = 0.5D - vx0 * vx0 - vy0 * vy0;
                float s0;
                if(sv0 < 0.0D) {
                    s0 = 0.f;
                } else {
                    sv0 *= sv0;
                    s0 = (float)(sv0 * sv0 * dotArray(diagonals[p0], vx0, vy0));
                }

                double sv1 = 0.5D - vx1 * vx1 - vy1 * vy1;
                float s1;
                if(sv1 < 0.0D) {
                    s1 = 0.f;
                } else {
                    sv1 *= sv1;
                    s1 = (float)(sv1 * sv1 * dotArray(diagonals[p1], vx1, vy1));
                }

                double sv2 = 0.5D - vx2 * vx2 - vy2 * vy2;
                float s2;
                if(sv2 < 0.0D) {
                    s2 = 0.f;
                } else {
                    sv2 *= sv2;
                    s2 = (float)(sv2 * sv2 * dotArray(diagonals[p2], vx2, vy2));
                }

                int index = i++;
                output[index] += 70.f * (s0 + s1 + s2) * magnitudeScale;
            }
        }

    }
}

