package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves2;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class BetaBiomesSampler {
    private final NoiseGeneratorOctaves2 temperatureGenerator;
    private final NoiseGeneratorOctaves2 humidityGenerator;
    private final NoiseGeneratorOctaves2 jitterGenerator;

    public BetaBiomesSampler(long seed) {
        this.temperatureGenerator = new NoiseGeneratorOctaves2(new Random(seed * 9871L), 4);
        this.humidityGenerator = new NoiseGeneratorOctaves2(new Random(seed * 39811L), 4);
        this.jitterGenerator = new NoiseGeneratorOctaves2(new Random(seed * 543321L), 2);
    }

    public BiomeGenBase getBiomeAtBlock( int x, int z) {
        float temperature = this.temperatureGenerator.sample(null, x, z, 1, 1, 0.02500000037252903d, 0.02500000037252903d, 0.25D)[0];
        float humidity = this.humidityGenerator.sample(null, x, z, 1, 1, 0.05F, 0.05F, 0.3333333333333333d)[0];
        float biomeJitter = this.jitterGenerator.sample(null, x, z, 1, 1, 0.25D, 0.25D, 0.5882352941176471D)[0];
        float d9 = biomeJitter * 1.1f + 0.5f;
        float weightTemp = 0.01F;
        float tempValue = (temperature * 0.15F + 0.7F) * (1.0F - weightTemp) + d9 * weightTemp;
        float weightHumid = 0.002F;
        float humidValue = (humidity * 0.15F + 0.5F) * (1.0F - weightHumid) + d9 * weightHumid;
        tempValue = 1.0F - (1.0F - tempValue) * (1.0F - tempValue);
        tempValue = MathHelper.clamp(tempValue,0,1);
        humidValue = MathHelper.clamp(humidValue, 0, 1);
        return BiomeGenBase.getBiomeFromLookup(tempValue, humidValue);
    }

    public float getTemperatureAtBlock( int x, int z) {
        float temperature = this.temperatureGenerator.sample(null, x, z, 1, 1, 0.02500000037252903d, 0.02500000037252903d, 0.25D)[0];
        float biomeJitter = this.jitterGenerator.sample(null, x, z, 1, 1, 0.25D, 0.25D, 0.5882352941176471D)[0];
        float d9 = biomeJitter * 1.1F + 0.5F;
        float weightTemp = 0.01F;
        float tempValue = (temperature * 0.15F + 0.7F) * (1.0F - weightTemp) + d9 * weightTemp;
        tempValue = 1.0F - (1.0F - tempValue) * (1.0F - tempValue);
        tempValue = MathHelper.clamp(tempValue,0,1);
        return tempValue;
    }

    public float getHumidityAtBlock( int x, int z) {
        float humidity = this.humidityGenerator.sample(null, x, z, 1, 1, 0.05F, 0.05F, 0.3333333333333333d)[0];
        float biomeJitter = this.jitterGenerator.sample(null, x, z, 1, 1, 0.25D, 0.25D, 0.5882352941176471D)[0];
        float d9 = biomeJitter * 1.1F + 0.5F;
        float weightHumid = 0.002F;
        float humidValue = (humidity * 0.15F + 0.5F) * (1.0F - weightHumid) + d9 * weightHumid;
        humidValue = MathHelper.clamp(humidValue, 0, 1);
        return humidValue;
    }
}