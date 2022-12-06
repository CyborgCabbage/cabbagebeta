package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves2;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class BetaBiomes {
    private final NoiseGeneratorOctaves2 temperatureGenerator;
    private final NoiseGeneratorOctaves2 humidityGenrator;
    private final NoiseGeneratorOctaves2 noise9;
    public double[] temperature;
    public double[] humidity;
    public double[] biomeJitter;
    public BiomeGenBase[] biomeRegion;
    public BiomeGenBase[] biomes;

    public BetaBiomes(long seed) {
        this.temperatureGenerator = new NoiseGeneratorOctaves2(new Random(seed * 9871L), 4);
        this.humidityGenrator = new NoiseGeneratorOctaves2(new Random(seed * 39811L), 4);
        this.noise9 = new NoiseGeneratorOctaves2(new Random(seed * 543321L), 2);
    }

    public BiomeGenBase getBiomeAtBlock(int x, int z) {
        return this.getBiomesInRegion(x, z, 1, 1)[0];
    }

    public double getTemperature(int i1, int i2) {
        this.temperature = this.temperatureGenerator.func_4112_a(this.temperature, i1, i2, 1, 1, 0.02500000037252903d, 0.02500000037252903d, 0.5D);
        return this.temperature[0];
    }

    public BiomeGenBase[] getBiomesInRegion(int xPos, int zPos, int xSize, int zSize) {
        this.biomeRegion = this.generateBiomes(this.biomeRegion, xPos, zPos, xSize, zSize);
        return this.biomeRegion;
    }

    public double[] getTemperatures(double[] d1, int i2, int i3, int i4, int i5) {
        if(d1 == null || d1.length < i4 * i5) {
            d1 = new double[i4 * i5];
        }

        d1 = this.temperatureGenerator.func_4112_a(d1, i2, i3, i4, i5, 0.02500000037252903d, 0.02500000037252903d, 0.25D);
        this.biomeJitter = this.noise9.func_4112_a(this.biomeJitter, i2, i3, i4, i5, 0.25D, 0.25D, 0.5882352941176471D);
        int i6 = 0;

        for(int i7 = 0; i7 < i4; ++i7) {
            for(int i8 = 0; i8 < i5; ++i8) {
                double d9 = this.biomeJitter[i6] * 1.1D + 0.5D;
                double d11 = 0.01D;
                double d13 = 1.0D - d11;
                double d15 = (d1[i6] * 0.15D + 0.7D) * d13 + d9 * d11;
                d15 = 1.0D - (1.0D - d15) * (1.0D - d15);
                if(d15 < 0.0D) {
                    d15 = 0.0D;
                }

                if(d15 > 1.0D) {
                    d15 = 1.0D;
                }

                d1[i6] = d15;
                ++i6;
            }
        }

        return d1;
    }

    public BiomeGenBase[] generateBiomes(BiomeGenBase[] biomes, int xChunk, int zChunk, int xSize, int zSize) {
        if(biomes == null || biomes.length < xSize * zSize) {
            biomes = new BiomeGenBase[xSize * zSize];
        }

        this.temperature = this.temperatureGenerator.func_4112_a(this.temperature, xChunk, zChunk, xSize, xSize, 0.02500000037252903d, 0.02500000037252903d, 0.25D);
        this.humidity = this.humidityGenrator.func_4112_a(this.humidity, xChunk, zChunk, xSize, xSize, 0.05F, 0.05F, 0.3333333333333333d);
        this.biomeJitter = this.noise9.func_4112_a(this.biomeJitter, xChunk, zChunk, xSize, xSize, 0.25D, 0.25D, 0.5882352941176471D);
        int i = 0;

        for(int k = 0; k < xSize; ++k) {
            for(int l = 0; l < zSize; ++l) {
                double d9 = this.biomeJitter[i] * 1.1D + 0.5D;
                double weightTemp = 0.01D;
                double tempValue = (this.temperature[i] * 0.15D + 0.7D) * (1.0D - weightTemp) + d9 * weightTemp;
                double weightHumid = 0.002d;
                double humidValue = (this.humidity[i] * 0.15D + 0.5D) * (1.0D - weightHumid) + d9 * weightHumid;
                tempValue = 1.0D - (1.0D - tempValue) * (1.0D - tempValue);
                tempValue = MathHelper.clamp(tempValue,0,1);
                humidValue = MathHelper.clamp(humidValue, 0, 1);
                this.temperature[i] = tempValue;
                this.humidity[i] = humidValue;
                biomes[i++] = BiomeGenBase.getBiomeFromLookup(tempValue, humidValue);
            }
        }
        return biomes;
    }
}
