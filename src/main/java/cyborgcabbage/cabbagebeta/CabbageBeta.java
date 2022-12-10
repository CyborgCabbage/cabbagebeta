package cyborgcabbage.cabbagebeta;

import cyborgcabbage.cabbagebeta.gen.beta.BetaChunkGenerator;
import cyborgcabbage.cabbagebeta.gen.beta.BetaNetherChunkGenerator;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.WorldPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CabbageBeta implements ModInitializer {
	public static final String MOD_ID = "cabbagebeta";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//Beta biomes
	public static RegistryKey<Biome> BETA_RAINFOREST;
	public static RegistryKey<Biome> BETA_SWAMPLAND;
	public static RegistryKey<Biome> BETA_SEASONAL_FOREST;
	public static RegistryKey<Biome> BETA_FOREST;
	public static RegistryKey<Biome> BETA_SAVANNA;
	public static RegistryKey<Biome> BETA_SHRUBLAND;
	public static RegistryKey<Biome> BETA_TAIGA;
	public static RegistryKey<Biome> BETA_DESERT;
	public static RegistryKey<Biome> BETA_PLAINS;
	public static RegistryKey<Biome> BETA_ICE_DESERT;
	public static RegistryKey<Biome> BETA_TUNDRA;
	public static RegistryKey<Biome> BETA_HELL;
	public static RegistryKey<Biome> BETA_SKY;

	public static Map<BiomeGenBase, RegistryKey<Biome>> BETA_TO_MODERN_BIOME = new HashMap<>();
	public static Map<BiomeGenBase, RegistryKey<Biome>> BETA_TO_SUBSTITUTE_BIOME = new HashMap<>();

	//Beta Preset
	public static final RegistryKey<WorldPreset> BETA_PRESET = RegistryKey.of(Registry.WORLD_PRESET_KEY, id("beta"));
	@Override
	public void onInitialize() {
		Registry.register(Registry.CHUNK_GENERATOR, id("beta"), BetaChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, id("beta_nether"), BetaNetherChunkGenerator.CODEC);
		BETA_RAINFOREST = registerBetaBiome("beta_rainforest", BiomeGenBase.rainforest, BiomeKeys.JUNGLE);
		BETA_SWAMPLAND = registerBetaBiome("beta_swampland", BiomeGenBase.swampland, BiomeKeys.SWAMP);
		BETA_SEASONAL_FOREST = registerBetaBiome("beta_seasonal_forest", BiomeGenBase.seasonalForest, BiomeKeys.FLOWER_FOREST);
		BETA_FOREST = registerBetaBiome("beta_forest", BiomeGenBase.forest, BiomeKeys.FOREST);
		BETA_SAVANNA = registerBetaBiome("beta_savanna", BiomeGenBase.savanna, BiomeKeys.SAVANNA);
		BETA_SHRUBLAND = registerBetaBiome("beta_shrubland", BiomeGenBase.shrubland, BiomeKeys.SUNFLOWER_PLAINS);
		BETA_TAIGA = registerBetaBiome("beta_taiga", BiomeGenBase.taiga, BiomeKeys.SNOWY_TAIGA);
		BETA_DESERT = registerBetaBiome("beta_desert", BiomeGenBase.desert, BiomeKeys.DESERT);
		BETA_PLAINS = registerBetaBiome("beta_plains", BiomeGenBase.plains, BiomeKeys.PLAINS);
		BETA_ICE_DESERT = registerBetaBiome("beta_ice_desert", BiomeGenBase.iceDesert, BiomeKeys.PLAINS);
		BETA_TUNDRA = registerBetaBiome("beta_tundra", BiomeGenBase.tundra, BiomeKeys.SNOWY_PLAINS);
		BETA_HELL = registerBetaBiome("beta_hell", BiomeGenBase.hell, BiomeKeys.NETHER_WASTES);
		BETA_SKY = registerBetaBiome("beta_sky", BiomeGenBase.sky, BiomeKeys.WINDSWEPT_HILLS);
	}
	private static RegistryKey<Biome> registerBetaBiome(String modernId, BiomeGenBase betaBiome, RegistryKey<Biome> substituteKey){
		RegistryKey<Biome> modernKey = RegistryKey.of(Registry.BIOME_KEY, id(modernId));
		Registry.register(BuiltinRegistries.BIOME, modernKey, betaBiome.createModernBiome());
		BETA_TO_MODERN_BIOME.put(betaBiome, modernKey);
		BETA_TO_SUBSTITUTE_BIOME.put(betaBiome, substituteKey);
		return modernKey;
	}

	public static Identifier id(String s){
		return new Identifier(MOD_ID, s);
	}
}
