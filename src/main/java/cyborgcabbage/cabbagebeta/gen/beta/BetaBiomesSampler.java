package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves2;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class BetaBiomesSampler {
    private final NoiseGeneratorOctaves2 temperatureGenerator;
    private final NoiseGeneratorOctaves2 humidityGenerator;
    private final NoiseGeneratorOctaves2 noise9;

    public BetaBiomesSampler(long seed) {
        this.temperatureGenerator = new NoiseGeneratorOctaves2(new Random(seed * 9871L), 4);
        this.humidityGenerator = new NoiseGeneratorOctaves2(new Random(seed * 39811L), 4);
        this.noise9 = new NoiseGeneratorOctaves2(new Random(seed * 543321L), 2);
    }

    public BiomeGenBase getBiomeAtBlock( int x, int z) {
        double temperature = this.temperatureGenerator.func_4112_a(null, x, z, 1, 1, 0.02500000037252903d, 0.02500000037252903d, 0.25D)[0];
        double humidity = this.humidityGenerator.func_4112_a(null, x, z, 1, 1, 0.05F, 0.05F, 0.3333333333333333d)[0];
        double biomeJitter = this.noise9.func_4112_a(null, x, z, 1, 1, 0.25D, 0.25D, 0.5882352941176471D)[0];
        double d9 = biomeJitter * 1.1D + 0.5D;
        double weightTemp = 0.01D;
        double tempValue = (temperature * 0.15D + 0.7D) * (1.0D - weightTemp) + d9 * weightTemp;
        double weightHumid = 0.002d;
        double humidValue = (humidity * 0.15D + 0.5D) * (1.0D - weightHumid) + d9 * weightHumid;
        tempValue = 1.0D - (1.0D - tempValue) * (1.0D - tempValue);
        tempValue = MathHelper.clamp(tempValue,0,1);
        humidValue = MathHelper.clamp(humidValue, 0, 1);
        return BiomeGenBase.getBiomeFromLookup(tempValue, humidValue);
    }

    public double getTemperatureAtBlock( int x, int z) {
        double temperature = this.temperatureGenerator.func_4112_a(null, x, z, 1, 1, 0.02500000037252903d, 0.02500000037252903d, 0.25D)[0];
        double biomeJitter = this.noise9.func_4112_a(null, x, z, 1, 1, 0.25D, 0.25D, 0.5882352941176471D)[0];
        double d9 = biomeJitter * 1.1D + 0.5D;
        double weightTemp = 0.01D;
        double tempValue = (temperature * 0.15D + 0.7D) * (1.0D - weightTemp) + d9 * weightTemp;
        tempValue = 1.0D - (1.0D - tempValue) * (1.0D - tempValue);
        tempValue = MathHelper.clamp(tempValue,0,1);
        return tempValue;
    }

    public double getHumidityAtBlock( int x, int z) {
        double humidity = this.humidityGenerator.func_4112_a(null, x, z, 1, 1, 0.05F, 0.05F, 0.3333333333333333d)[0];
        double biomeJitter = this.noise9.func_4112_a(null, x, z, 1, 1, 0.25D, 0.25D, 0.5882352941176471D)[0];
        double d9 = biomeJitter * 1.1D + 0.5D;
        double weightHumid = 0.002d;
        double humidValue = (humidity * 0.15D + 0.5D) * (1.0D - weightHumid) + d9 * weightHumid;
        humidValue = MathHelper.clamp(humidValue, 0, 1);
        return humidValue;
    }
}