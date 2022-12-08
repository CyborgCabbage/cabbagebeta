package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves2;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class BetaBiomes {
    private final NoiseGeneratorOctaves2 temperatureGenerator;
    private final NoiseGeneratorOctaves2 humidityGenrator;
    private final NoiseGeneratorOctaves2 noise9;
    public float[] temperature;
    public float[] humidity;
    public float[] biomeJitter;
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

    public float getTemperature(int i1, int i2) {
        this.temperature = this.temperatureGenerator.sample(this.temperature, i1, i2, 1, 1, 0.02500000037252903d, 0.02500000037252903d, 0.5D);
        return this.temperature[0];
    }

    public BiomeGenBase[] getBiomesInRegion(int xPos, int zPos, int xSize, int zSize) {
        this.biomeRegion = this.generateBiomes(this.biomeRegion, xPos, zPos, xSize, zSize);
        return this.biomeRegion;
    }

    public float[] getTemperatures(float[] d1, int i2, int i3, int i4, int i5) {
        if(d1 == null || d1.length < i4 * i5) {
            d1 = new float[i4 * i5];
        }

        d1 = this.temperatureGenerator.sample(d1, i2, i3, i4, i5, 0.02500000037252903d, 0.02500000037252903d, 0.25D);
        this.biomeJitter = this.noise9.sample(this.biomeJitter, i2, i3, i4, i5, 0.25D, 0.25D, 0.5882352941176471D);
        int i6 = 0;

        for(int i7 = 0; i7 < i4; ++i7) {
            for(int i8 = 0; i8 < i5; ++i8) {
                float d9 = this.biomeJitter[i6] * 1.1f + 0.5f;
                float d11 = 0.01f;
                float d13 = 1.0f - d11;
                float d15 = (d1[i6] * 0.15f + 0.7f) * d13 + d9 * d11;
                d15 = 1.0F - (1.0F - d15) * (1.0F - d15);
                if(d15 < 0.0F) {
                    d15 = 0.0F;
                }

                if(d15 > 1.0F) {
                    d15 = 1.0F;
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

        this.temperature = this.temperatureGenerator.sample(this.temperature, xChunk, zChunk, xSize, zSize, 0.02500000037252903d, 0.02500000037252903d, 0.25D);
        this.humidity = this.humidityGenrator.sample(this.humidity, xChunk, zChunk, xSize, zSize, 0.05F, 0.05F, 0.3333333333333333d);
        this.biomeJitter = this.noise9.sample(this.biomeJitter, xChunk, zChunk, xSize, zSize, 0.25D, 0.25D, 0.5882352941176471D);
        int i = 0;

        for(int k = 0; k < xSize; ++k) {
            for(int l = 0; l < zSize; ++l) {
                float d9 = this.biomeJitter[i] * 1.1F + 0.5F;
                float weightTemp = 0.01F;
                float tempValue = (this.temperature[i] * 0.15F + 0.7F) * (1.0F - weightTemp) + d9 * weightTemp;
                float weightHumid = 0.002F;
                float humidValue = (this.humidity[i] * 0.15F + 0.5F) * (1.0F - weightHumid) + d9 * weightHumid;
                tempValue = 1.0F - (1.0F - tempValue) * (1.0F - tempValue);
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
