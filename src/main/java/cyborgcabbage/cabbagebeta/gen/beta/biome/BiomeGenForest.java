package cyborgcabbage.cabbagebeta.gen.beta.biome;

import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenBigOak;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenBirch;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenOak;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;

import java.util.Random;

public class BiomeGenForest extends BiomeGenBase {
	public WorldGenerator getRandomWorldGenForTrees(Random random1, int height) {
		return random1.nextInt(5) == 0 ? new WorldGenBirch(height) : (random1.nextInt(3) == 0 ? new WorldGenBigOak() : new WorldGenOak(height));
	}

	@Override
	protected SpawnSettings.Builder getSpawnSettings() {
		SpawnSettings.Builder builder = super.getSpawnSettings();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 2, 4, 4));
		return builder;
	}
}
