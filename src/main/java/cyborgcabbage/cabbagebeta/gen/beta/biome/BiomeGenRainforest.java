package cyborgcabbage.cabbagebeta.gen.beta.biome;

import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenBigOak;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenOak;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenerator;

import java.util.Random;

public class BiomeGenRainforest extends BiomeGenBase {
	public WorldGenerator getRandomWorldGenForTrees(Random random1, int height) {
		return random1.nextInt(3) == 0 ? new WorldGenBigOak() : new WorldGenOak(height);
	}
}
