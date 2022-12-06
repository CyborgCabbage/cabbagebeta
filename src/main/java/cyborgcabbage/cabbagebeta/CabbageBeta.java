package cyborgcabbage.cabbagebeta;

import cyborgcabbage.cabbagebeta.gen.beta.BetaBiomes;
import cyborgcabbage.cabbagebeta.gen.beta.BetaChunkGenerator;
import cyborgcabbage.cabbagebeta.gen.beta.biome.BiomeGenBase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CabbageBeta implements ModInitializer {
	public static final String MOD_ID = "cabbagebeta";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//Beta biomes
	public static final RegistryKey<Biome> BETA_RAINFOREST = RegistryKey.of(Registry.BIOME_KEY, id("beta_rainforest"));
	public static final RegistryKey<Biome> BETA_SWAMPLAND = RegistryKey.of(Registry.BIOME_KEY, id("beta_swampland"));
	public static final RegistryKey<Biome> BETA_SEASONAL_FOREST = RegistryKey.of(Registry.BIOME_KEY, id("beta_seasonal_forest"));
	public static final RegistryKey<Biome> BETA_FOREST = RegistryKey.of(Registry.BIOME_KEY, id("beta_forest"));
	public static final RegistryKey<Biome> BETA_SAVANNA = RegistryKey.of(Registry.BIOME_KEY, id("beta_savanna"));
	public static final RegistryKey<Biome> BETA_SHRUBLAND = RegistryKey.of(Registry.BIOME_KEY, id("beta_shrubland"));
	public static final RegistryKey<Biome> BETA_TAIGA = RegistryKey.of(Registry.BIOME_KEY, id("beta_taiga"));
	public static final RegistryKey<Biome> BETA_DESERT = RegistryKey.of(Registry.BIOME_KEY, id("beta_desert"));
	public static final RegistryKey<Biome> BETA_PLAINS = RegistryKey.of(Registry.BIOME_KEY, id("beta_plains"));
	public static final RegistryKey<Biome> BETA_ICE_DESERT = RegistryKey.of(Registry.BIOME_KEY, id("beta_ice_desert"));
	public static final RegistryKey<Biome> BETA_TUNDRA = RegistryKey.of(Registry.BIOME_KEY, id("beta_tundra"));
	public static final RegistryKey<Biome> BETA_HELL = RegistryKey.of(Registry.BIOME_KEY, id("beta_hell"));
	public static final RegistryKey<Biome> BETA_SKY = RegistryKey.of(Registry.BIOME_KEY, id("beta_sky"));

	public static final BetaBiomes BETA_BIOMES = new BetaBiomes(BetaChunkGenerator.toSeed("Glacier"));

	@Override
	public void onInitialize() {
		Registry.register(Registry.CHUNK_GENERATOR, id("beta"), BetaChunkGenerator.CODEC);

		Registry.register(BuiltinRegistries.BIOME, BETA_RAINFOREST, BiomeGenBase.rainforest.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_SWAMPLAND, BiomeGenBase.swampland.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_SEASONAL_FOREST, BiomeGenBase.seasonalForest.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_FOREST, BiomeGenBase.forest.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_SAVANNA, BiomeGenBase.savanna.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_SHRUBLAND, BiomeGenBase.shrubland.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_TAIGA, BiomeGenBase.taiga.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_DESERT, BiomeGenBase.desert.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_PLAINS, BiomeGenBase.plains.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_ICE_DESERT, BiomeGenBase.iceDesert.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_TUNDRA, BiomeGenBase.tundra.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_HELL, BiomeGenBase.hell.createModernBiome());
		Registry.register(BuiltinRegistries.BIOME, BETA_SKY, BiomeGenBase.sky.createModernBiome());
	}
	public static Identifier id(String s){
		return new Identifier(MOD_ID, s);
	}
}
