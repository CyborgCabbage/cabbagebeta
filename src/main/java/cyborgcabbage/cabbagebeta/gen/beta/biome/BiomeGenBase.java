package cyborgcabbage.cabbagebeta.gen.beta.biome;

import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenBigTree;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenTrees;
import cyborgcabbage.cabbagebeta.gen.beta.worldgen.WorldGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.*;

import java.awt.*;
import java.util.Random;

public class BiomeGenBase {
	public static final BiomeGenBase rainforest = (new BiomeGenRainforest())
			.setColor(0x8fa36)
			.setBiomeName("Rainforest")
			.setOtherColor(0x1ff458)
			.setTemperature(0.99f)
			.setHumidity(0.98f)
			.setTrees(true, 5)
			.setShrubs(10);
	public static final BiomeGenBase swampland = (new BiomeGenSwamp())
			.setColor(0x7f9b2)
			.setBiomeName("Swampland")
			.setOtherColor(0x8baf48)
			.setTemperature(0.64f)
			.setHumidity(0.93f);
	public static final BiomeGenBase seasonalForest = (new BiomeGenBase())
			.setColor(0x9be023)
			.setBiomeName("Seasonal Forest")
			.setTemperature(0.99f)
			.setHumidity(0.68f)
			.setTrees(true, 2)
			.setDandelions(4)
			.setShrubs(2);
	public static final BiomeGenBase forest = (new BiomeGenForest())
			.setColor(0x56621)
			.setBiomeName("Forest")
			.setOtherColor(0x4eba31)
			.setTemperature(0.87f)
			.setHumidity(0.71f)
			.setTrees(true, 5)
			.setDandelions(2)
			.setShrubs(2);
	public static final BiomeGenBase savanna = (new BiomeGenDesert())
			.setColor(0xd9e023)
			.setBiomeName("Savanna")
			.setTemperature(0.77f)
			.setHumidity(0.11f);
	public static final BiomeGenBase shrubland = (new BiomeGenBase())
			.setColor(0xa1ad20)
			.setBiomeName("Shrubland")
			.setTemperature(0.80f)
			.setHumidity(0.37f);
	public static final BiomeGenBase taiga = (new BiomeGenTaiga())
			.setColor(0x2eb153)
			.setBiomeName("Taiga")
			.setEnableSnow()
			.setOtherColor(0x7bb731)
			.setTemperature(0.41f)
			.setHumidity(0.78f)
			.setTrees(true, 5)
			.setDandelions(2)
			.setShrubs(1);
	public static final BiomeGenBase desert = (new BiomeGenDesert())
			.setColor(0xfa9418)
			.setBiomeName("Desert")
			.setDisableRain()
			.setTemperature(0.98f)
			.setHumidity(0.08f)
			.setTrees(false, -20);
	public static final BiomeGenBase plains = (new BiomeGenDesert())
			.setColor(0xffd910)
			.setBiomeName("Plains")
			.setTemperature(0.99f)
			.setHumidity(0.34f)
			.setTrees(false, -20)
			.setDandelions(3)
			.setShrubs(10);
	public static final BiomeGenBase iceDesert = (new BiomeGenDesert())
			.setColor(0xffed93)
			.setBiomeName("Ice Desert")
			.setEnableSnow()
			.setDisableRain()
			.setOtherColor(0xc4d339)
			.setTemperature(0.40f)
			.setHumidity(0.20f);
	public static final BiomeGenBase tundra = (new BiomeGenBase())
			.setColor(0x57ebf9)
			.setBiomeName("Tundra")
			.setEnableSnow()
			.setOtherColor(0xc4d339)
			.setTemperature(0.26f)
			.setHumidity(0.37f)
			.setTrees(false, -20);
	public static final BiomeGenBase hell = (new BiomeGenHell())
			.setColor(0xff0000)
			.setBiomeName("Hell")
			.setDisableRain()
			.setTemperature(1.f)
			.setHumidity(0.f);
	public static final BiomeGenBase sky = (new BiomeGenSky())
			.setColor(0x8080ff)
			.setBiomeName("Sky")
			.setDisableRain()
			.setTemperature(0.99f)
			.setHumidity(0.34f);

	public String biomeName;
	public int color;
	public BlockState topBlock = Blocks.GRASS_BLOCK.getDefaultState();
	public BlockState fillerBlock = Blocks.DIRT.getDefaultState();
	public int otherColor = 0x4ee031;
	private boolean enableSnow;
	private boolean enableRain = true;
	private static final BiomeGenBase[] biomeLookupTable = new BiomeGenBase[4096];
	public int id;
	public static int biomeCount = 0;
	private float temperature = 0.5f;
	private float humidity = 0.5f;
	public boolean addExtraTrees = false;
	public int treeCount = 0;
	public int dandelionCount = 0;
	public int shrubCount = 0;
	protected BiomeGenBase() {
		id = biomeCount;
		biomeCount++;
		/*this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10));
		this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 12));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8));
		this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10));*/
	}

	public Biome createModernBiome() {
		return new Biome.Builder()
				.precipitation(enableSnow ? Biome.Precipitation.SNOW : (enableRain ? Biome.Precipitation.RAIN : Biome.Precipitation.NONE))
				.temperature(temperature)
				.downfall(humidity)
				.effects(getBiomeEffects().build())
				.spawnSettings(getSpawnSettings().build())
				.generationSettings(new GenerationSettings.Builder().build()).build();
	}

	protected BiomeEffects.Builder getBiomeEffects() {
		return new BiomeEffects.Builder()
				.waterColor(0x2F58ff)
				.waterFogColor(0x50533)
				.fogColor(0xC0D8FF)
				.skyColor(OverworldBiomeCreator.getSkyColor(temperature));
	}

	protected SpawnSettings.Builder getSpawnSettings() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		addBetaMonsters(builder);
		addBetaAnimals(builder);
		return builder;
	}

	private static void addBetaMonsters(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 100, 4, 4));
	}

	private static void addBetaAnimals(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PIG, 10, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.COW, 8, 4, 4));
		builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 1, 1, 4));
	}

	private BiomeGenBase setDandelions(int dandelionCount) {
		this.dandelionCount = dandelionCount;
		return this;
	}

	private BiomeGenBase setShrubs(int shrubCount) {
		this.shrubCount = shrubCount;
		return this;
	}

	private BiomeGenBase setTrees(boolean addExtra, int treeCount) {
		this.addExtraTrees = addExtra;
		this.treeCount = treeCount;
		return this;
	}

	private BiomeGenBase setTemperature(float temp){
		this.temperature = temp;
		return this;
	}

	private BiomeGenBase setHumidity(float humid){
		this.humidity = humid;
		return this;
	}

	private BiomeGenBase setDisableRain() {
		this.enableRain = false;
		return this;
	}

	public static void generateBiomeLookup() {
		for(int t = 0; t < 64; ++t) {
			for(int h = 0; h < 64; ++h) {
				biomeLookupTable[t + h * 64] = getBiome((float) t / 63.0F, (float) h / 63.0F);
			}
		}
		desert.topBlock = desert.fillerBlock = Blocks.SAND.getDefaultState();
		iceDesert.topBlock = iceDesert.fillerBlock = Blocks.SAND.getDefaultState();
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return random1.nextInt(10) == 0 ? new WorldGenBigTree() : new WorldGenTrees();
	}

	protected BiomeGenBase setEnableSnow() {
		this.enableSnow = true;
		return this;
	}

	protected BiomeGenBase setBiomeName(String string1) {
		this.biomeName = string1;
		return this;
	}

	protected BiomeGenBase setOtherColor(int i1) {
		this.otherColor = i1;
		return this;
	}

	protected BiomeGenBase setColor(int i1) {
		this.color = i1;
		return this;
	}

	public static BiomeGenBase getBiomeFromLookup(double d0, double d2) {
		int i4 = (int)(d0 * 63.0D);
		int i5 = (int)(d2 * 63.0D);
		return biomeLookupTable[i4 + i5 * 64];
	}

	public static BiomeGenBase getBiome(float f0, float f1) {
		f1 *= f0;
		return f0 < 0.1F ? tundra : (f1 < 0.2F ? (f0 < 0.5F ? tundra : (f0 < 0.95F ? savanna : desert)) : (f1 > 0.5F && f0 < 0.7F ? swampland : (f0 < 0.5F ? taiga : (f0 < 0.97F ? (f1 < 0.35F ? shrubland : forest) : (f1 < 0.45F ? plains : (f1 < 0.9F ? seasonalForest : rainforest))))));
	}

	public int getSkyColorByTemp(float f1) {
		f1 /= 3.0F;
		if(f1 < -1.0F) {
			f1 = -1.0F;
		}

		if(f1 > 1.0F) {
			f1 = 1.0F;
		}

		return Color.getHSBColor(0.62222224F - f1 * 0.05F, 0.5F + f1 * 0.1F, 1.0F).getRGB();
	}

	/*public List getSpawnableList(EnumCreatureType enumCreatureType1) {
		return enumCreatureType1 == EnumCreatureType.monster ? this.spawnableMonsterList : (enumCreatureType1 == EnumCreatureType.creature ? this.spawnableCreatureList : (enumCreatureType1 == EnumCreatureType.waterCreature ? this.spawnableWaterCreatureList : null));
	}*/

	public boolean getEnableSnow() {
		return this.enableSnow;
	}

	public boolean canSpawnLightningBolt() {
		return this.enableRain && !this.enableSnow;
	}

	static {
		generateBiomeLookup();
	}
}
