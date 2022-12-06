package cyborgcabbage.cabbagebeta.gen.beta.noise;

import java.util.Arrays;
import java.util.Random;

public class NoiseGeneratorOctaves {
    private NoiseGeneratorPerlin[] generatorCollection;
    private int layers;

    public NoiseGeneratorOctaves(Random random, int _layers) {
        this.layers = _layers;
        this.generatorCollection = new NoiseGeneratorPerlin[_layers];

        for(int i3 = 0; i3 < _layers; ++i3) {
            this.generatorCollection[i3] = new NoiseGeneratorPerlin(random);
        }

    }

    public double func_806_a(double d1, double d3) {
        double d5 = 0.0D;
        double d7 = 1.0D;

        for(int i9 = 0; i9 < this.layers; ++i9) {
            d5 += this.generatorCollection[i9].func_801_a(d1 * d7, d3 * d7) / d7;
            d7 /= 2.0D;
        }

        return d5;
    }

    public double[] generateNoiseOctaves(double[] output, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if(output == null) {
            output = new double[xSize * ySize * zSize];
        } else {
            Arrays.fill(output, 0.0D);
        }

        double inverseMagnitude = 1.0D;

        for(int l = 0; l < this.layers; ++l) {
            this.generatorCollection[l].func_805_a(output, xOffset, yOffset, zOffset, xSize, ySize, zSize, xScale * inverseMagnitude, yScale * inverseMagnitude, zScale * inverseMagnitude, inverseMagnitude);
            inverseMagnitude /= 2.0D;
        }

        return output;
    }

    public double[] func_4109_a(double[] d1, int i2, int i3, int i4, int i5, double d6, double d8, double d10) {
        return this.generateNoiseOctaves(d1, (double)i2, 10.0D, (double)i3, i4, 1, i5, d6, 1.0D, d8);
    }
}
