package cyborgcabbage.cabbagebeta.gen.beta.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;

public class BiomeGenSky extends BiomeGenBase {
	@Override
	protected SpawnSettings.Builder getSpawnSettings() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		return builder;
	}

	public int getSkyColorByTemp(float f1) {
		return 12632319;
	}
}
