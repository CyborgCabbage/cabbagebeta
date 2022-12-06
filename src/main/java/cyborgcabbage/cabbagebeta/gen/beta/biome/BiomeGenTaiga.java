package cyborgcabbage.cabbagebeta.gen.beta.biome;

import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenTaiga1;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenTaiga2;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;

import java.util.Random;

public class BiomeGenTaiga extends BiomeGenBase {
	@Override
	protected SpawnSettings.Builder getSpawnSettings() {
		SpawnSettings.Builder builder = super.getSpawnSettings();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 2, 4, 4));
		return builder;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return random1.nextInt(3) == 0 ? new WorldGenTaiga1() : new WorldGenTaiga2();
	}
}
