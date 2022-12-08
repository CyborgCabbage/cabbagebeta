package cyborgcabbage.cabbagebeta.gen.beta.noise;

import java.util.Arrays;
import java.util.Random;

public class NoiseGeneratorOctaves2 {
    private NoiseGeneratorPerlin2[] field_4234_a;
    private int field_4233_b;

    public NoiseGeneratorOctaves2(Random random1, int i2) {
        this.field_4233_b = i2;
        this.field_4234_a = new NoiseGeneratorPerlin2[i2];

        for(int i3 = 0; i3 < i2; ++i3) {
            this.field_4234_a[i3] = new NoiseGeneratorPerlin2(random1);
        }

    }

    public float[] sample(float[] output, double xOffset, double yOffset, int xSize, int ySize, double xScale, double yScale, double scaleFactor) {
        return this.sample(output, xOffset, yOffset, xSize, ySize, xScale, yScale, scaleFactor, 0.5f);
    }

    public float[] sample(float[] output, double xOffset, double yOffset, int xSize, int ySize, double xScale, double yScale, double scaleFactor, float magnitudeFactor) {
        xScale /= 1.5D;
        yScale /= 1.5D;
        if(output != null && output.length >= xSize * ySize) {
            Arrays.fill(output, 0.f);
        } else {
            output = new float[xSize * ySize];
        }

        float accumulateMagnitude = 1.f;
        double accumulateScale = 1.0D;

        for(int i20 = 0; i20 < this.field_4233_b; ++i20) {
            this.field_4234_a[i20].func_4157_a(output, xOffset, yOffset, xSize, ySize, xScale * accumulateScale, yScale * accumulateScale, 0.55f / accumulateMagnitude);
            accumulateScale *= scaleFactor;
            accumulateMagnitude *= magnitudeFactor;
        }

        return output;
    }
}

