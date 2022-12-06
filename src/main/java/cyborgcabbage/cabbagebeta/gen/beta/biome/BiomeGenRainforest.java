package cyborgcabbage.cabbagebeta.gen.beta.biome;

import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenBigTree;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenTrees;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenerator;

import java.util.Random;

public class BiomeGenRainforest extends BiomeGenBase {
	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return random1.nextInt(3) == 0 ? new WorldGenBigTree() : new WorldGenTrees();
	}
}
