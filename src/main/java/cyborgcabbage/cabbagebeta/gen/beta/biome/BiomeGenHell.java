package cyborgcabbage.cabbagebeta.gen.beta.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.SpawnSettings;

public class BiomeGenHell extends BiomeGenBase {
	@Override
	protected SpawnSettings.Builder getSpawnSettings() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
				.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 100, 1, 1))
				.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4));
		return builder;
	}

	@Override
	protected BiomeEffects.Builder getBiomeEffects() {
		return super.getBiomeEffects().fogColor(0x330808);
	}
}
