package cyborgcabbage.cabbagebeta.gen.beta;

import cyborgcabbage.cabbagebeta.gen.BetaProperties;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BetaBiomesSampler;
import cyborgcabbage.cabbagebeta.gen.beta.noise.NoiseGeneratorOctaves;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

import java.util.Random;

public class BetaOverworldDensityFunction implements DensityFunction.Base {
    private final BetaProperties prop;
    private final BetaBiomesSampler biomeSampler;
    private final Random rand;
    private NoiseGeneratorOctaves noise16a;
    private NoiseGeneratorOctaves noise16b;
    private NoiseGeneratorOctaves noise8a;
    private NoiseGeneratorOctaves noise4a;
    private NoiseGeneratorOctaves noise4b;
    private NoiseGeneratorOctaves noise10a;
    private NoiseGeneratorOctaves noise16c;
    private NoiseGeneratorOctaves treeNoise;

    public BetaOverworldDensityFunction(long seed, BetaProperties prop, BetaBiomesSampler biomeSampler) {
        this.rand = new Random(seed);
        this.prop = prop;
        this.biomeSampler = biomeSampler;
        this.noise16a = new NoiseGeneratorOctaves(this.rand, 16);
        this.noise16b = new NoiseGeneratorOctaves(this.rand, 16);
        this.noise8a = new NoiseGeneratorOctaves(this.rand, 8);
        this.noise4a = new NoiseGeneratorOctaves(this.rand, 4);
        this.noise4b = new NoiseGeneratorOctaves(this.rand, 4);
        this.noise10a = new NoiseGeneratorOctaves(this.rand, 10);
        this.noise16c = new NoiseGeneratorOctaves(this.rand, 16);
        this.treeNoise = new NoiseGeneratorOctaves(this.rand, 8);
    }

    public double sample(NoisePos pos) {
        int x = pos.blockX();
        int y = pos.blockY();
        int z = pos.blockZ();

        double hScale = 684.412d;
        double vScale = 684.412d;
        double temp = this.biomeSampler.getTemperatureAtBlock(x*4, z*4);
        double humidity = this.biomeSampler.getHumidityAtBlock(x*4, z*4);

        double highFreq2d10s = this.noise10a.generateNoiseOctaves(null, x, 10.0D, z, 1, 1, 1, 1.121D/prop.worldScale(), 1.0D, 1.121D/prop.worldScale())[0];
        double lowFreq2d16s = this.noise16c.generateNoiseOctaves(null, x, 10.0D, z, 1, 1, 1, 200.0D/prop.worldScale(), 1.0D, 200.0D/prop.worldScale())[0];
        double highFreq3d8s = this.noise8a.generateNoiseOctaves(null, x, y, z, 1, 1, 1, hScale / 80.0D/prop.worldScale(), vScale / 160.0D/prop.worldScale(), hScale / 80.0D/prop.worldScale())[0];
        double lowFreq3d16as = this.noise16a.generateNoiseOctaves(null, x, y, z, 1, 1, 1, hScale/prop.worldScale(), vScale/prop.worldScale(), hScale/prop.worldScale())[0];
        double lowFreq3d16bs = this.noise16b.generateNoiseOctaves(null, x, y, z, 1, 1, 1, hScale/prop.worldScale(), vScale/prop.worldScale(), hScale/prop.worldScale())[0];

        int yNoiseIndex = y/8;
        int yNoiseSize = prop.generationHeight()/8+1;
        double humidityVal = humidity * temp;
        humidityVal = 1.0D - humidityVal;
        humidityVal *= humidityVal;
        humidityVal *= humidityVal;
        humidityVal = 1.0D - humidityVal;
        double highFreqHumid = (highFreq2d10s + 256.0D) / 512.0D;
        highFreqHumid *= humidityVal;
        if(highFreqHumid > 1.0D) {
            highFreqHumid = 1.0D;
        }

        double lowFreq2d3 = lowFreq2d16s / 8000.0D;
        if(lowFreq2d3 < 0.0D) {
            lowFreq2d3 = -lowFreq2d3 * 0.3d;
        }

        lowFreq2d3 = lowFreq2d3 * 3.0D - 2.0D;
        if(lowFreq2d3 < 0.0D) {
            lowFreq2d3 /= 2.0D;
            if(lowFreq2d3 < -1.0D) {
                lowFreq2d3 = -1.0D;
            }

            lowFreq2d3 /= 1.4D;
            lowFreq2d3 /= 2.0D;
            //double decliff = 0.3;//[0, 1.0] -> [0, 0.35]
            if(prop.decliff()*0.35 <= 0.001){
                highFreqHumid = 0.0D;
            }else{
                double temp2 = lowFreq2d3*(-1/(prop.decliff()*0.35));
                highFreqHumid *= Math.max(0.9*(1-temp2), 0);
            }


        } else {
            if(lowFreq2d3 > 1.0D) {
                lowFreq2d3 = 1.0D;
            }

            lowFreq2d3 /= 8.0D;
        }

        if(highFreqHumid < 0.0D) {
            highFreqHumid = 0.0D;
        }

        highFreqHumid += 0.5D;
        lowFreq2d3 = lowFreq2d3 * (double) 17 / 16.0D;
        double groundLevelLocal = (double)prop.groundLevel()/8.0 + lowFreq2d3 * 4.0D;
        double bias = ((double)yNoiseIndex/prop.worldScale() - groundLevelLocal) * prop.factor() / highFreqHumid;
        if(bias < 0.0D) {
            bias *= 4.0D;
        }

        double a = lowFreq3d16as / 512.0D;
        double b = lowFreq3d16bs / 512.0D;
        double mix = highFreq3d8s / 20.0D * prop.mixing() + 0.5D;
        double noiseValue;
        if(mix < 0.0D) {
            noiseValue = a;
        } else if(mix > 1.0D) {
            noiseValue = b;
        } else {
            noiseValue = a + (b - a) * mix;
        }
        noiseValue -= bias;
        //Fall-off
        float fallOffStart = prop.generationHeight() / 32.f;
        if(yNoiseIndex > yNoiseSize - fallOffStart) {
            double d44 = (yNoiseIndex - (yNoiseSize - fallOffStart)) / (fallOffStart-1.f);
            noiseValue = noiseValue * (1.0D - d44) + -10.0D * d44;
        }
        return noiseValue;
    }

    public double minValue() {
        return Double.NEGATIVE_INFINITY;
    }

    public double maxValue() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public CodecHolder<? extends DensityFunction> getCodecHolder() {
        return null;
    }
}

