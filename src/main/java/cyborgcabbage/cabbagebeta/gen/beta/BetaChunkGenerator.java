package cyborgcabbage.cabbagebeta.gen.beta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cyborgcabbage.cabbagebeta.gen.BetaProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BetaChunkGenerator extends ChunkGenerator implements BetaProperties {
    public static final Codec<BetaChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter(chunkGenerator -> chunkGenerator.structureSetRegistry),
            RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter(generator -> generator.biomeRegistry),
            Codec.STRING.fieldOf("dimension_mode").orElse("").forGetter(BetaChunkGenerator::getDimensionMode),
            Codec.BOOL.fieldOf("use_full_height").orElse(false).forGetter(BetaChunkGenerator::getUseFullHeight),
            Codec.INT.fieldOf("sea_level").orElse(64).forGetter(BetaChunkGenerator::getSeaLevel),
            Codec.FLOAT.fieldOf("factor").orElse(12.0f).forGetter(BetaChunkGenerator::getFactor),
            Codec.INT.fieldOf("ground_level").orElse(68).forGetter(BetaChunkGenerator::getGroundLevel),
            Codec.INT.fieldOf("cave_lava_level").orElse(10).forGetter(BetaChunkGenerator::getCaveLavaLevel),
            Codec.FLOAT.fieldOf("mixing").orElse(1.0f).forGetter(BetaChunkGenerator::getMixing),
            Codec.BOOL.fieldOf("fixes").orElse(false).forGetter(BetaChunkGenerator::getFixes)
    ).apply(instance, instance.stable(BetaChunkGenerator::new)));
    private final Registry<Biome> biomeRegistry;
    //Content Creators
    private final BetaSeed SEED_YOGSCAST = new BetaSeed(4090136037452000329L);
    private final BetaSeed SEED_STAMPY = new BetaSeed(6644803604819148923L);
    private final BetaSeed SEED_ANTVENOM = new BetaSeed(3811868026651017821L);
    //Reverse Engineering
    private final BetaSeed SEED_PACK_PNG = new BetaSeed(3257840388504953787L);//Population differences
    private final BetaSeed SEED_PANORAMA = new BetaSeed(2151901553968352745L);

    private String dimensionMode = "";
    public String getDimensionMode() {
        return dimensionMode;
    }


    public static long toSeed(String seedString){
        if(seedString != null && !seedString.isEmpty()) {
            try {
                return Long.parseLong(seedString);
            } catch (NumberFormatException numberFormatException7) {
                return seedString.hashCode();
            }
        }
        return (new Random()).nextLong();
    }

    //minecraftseeds.info (internet archive)
    /*private final String[] SEED_ARRAY = {
        "177907495",
        "1385327417",
        "MODDED",
        "Vevelstad",
        "Elfen Lied",
        "729",
        "-8388746566455332234",
        "4238342445668208996",
        "965334902297122527",
        "5515274009531393841",
        "Archespore",
        "turnofthetides",
        "worstseedever",
        "beagle bagle",
        "pokeylucky",
        "curtis dent",
        "1420013959",
        "1474776471",
        "1961263745",
        "1541961902",
        "Quesadila",
        "-6362184493185806144",
        "5944220116861330522",
        "-780636540",
        "Roughsauce",
        "Wolf",
        "Diamonds, diamonds everywhere!",
        "Aether Collab",
        "-2608611364321170322",
        "-01556767897",
        "-1293644106920865080",
        "Werewolf",
        "459722261485094655",
        "1363181899730807241",
        "5677344492879191995",
        "5682930821",
        "72164122",
        "2409838883250561605",
        "Wave Race 64",
        "-115144210771600827",
        "-9028489474908844496",
        "9000.1",
        "Dead Mau5",
        "Ausm",
        "-2945350671081178213",
        "Invinsible",
        "3666440496532277820",
        "-442650539972332399",
        "Timestamp: 2011-03-02 06:55:36 U",
        "4042531831790214307",
        "-6555642694674147910",
        "gargamel",
        "-1784338777788894343",
        "Glacier",
        "404"
    };*/

    protected BetaChunkProvider generator;

    private final boolean useFullHeight;
    private final int seaLevel;
    private final float factor;
    private final int groundLevel;
    private final int caveLavaLevel;
    private final float mixing;
    private final boolean fixes;

    /*
    Variables:
    Use Full Height
    Sea Level
    Factor
    Ground Level
    Cave Lava Level
    Mixing
    Fixes

    Faithful:
    No
    64
    12.0
    68
    10
    ?
    No


    */

    public BetaChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> biomeRegistry, String dimensionMode, boolean useFullHeight, int seaLevel, float factor, int groundLevel, int caveLavaLevel, float mixing, boolean fixes) {
        super(structureSetRegistry, Optional.empty(), new BetaOverworldBiomeSource(biomeRegistry));
        this.biomeRegistry = biomeRegistry;
        this.dimensionMode = dimensionMode;
        this.useFullHeight = useFullHeight;
        this.seaLevel = seaLevel;
        this.factor = factor;
        this.groundLevel = groundLevel;
        this.caveLavaLevel = caveLavaLevel;
        this.mixing = mixing;
        this.fixes = fixes;
        generator = Objects.equals(this.dimensionMode, "nether") ? new ChunkProviderHell() : new ChunkProviderGenerate(useFullHeight, seaLevel, factor, groundLevel, caveLavaLevel, mixing, fixes);
        if(biomeSource instanceof BetaOverworldBiomeSource bobs){
            bobs.setGenerator(generator);
        }
    }

    public boolean getUseFullHeight() {
        return useFullHeight;
    }

    @Override
    public int getSeaLevel() {
        return seaLevel;
    }

    public float getFactor() {
        return factor;
    }

    public int getGroundLevel() {
        return groundLevel;
    }

    public int getCaveLavaLevel() {
        return caveLavaLevel;
    }

    public float getMixing() {
        return mixing;
    }

    public boolean getFixes() {
        return fixes;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        //super.generateFeatures(world, chunk, structureAccessor);
        generator.populate(world, chunk, world.getSeed());

    }

    @Override
    public void setStructureStarts(DynamicRegistryManager registryManager, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk, StructureTemplateManager structureTemplateManager, long seed) {
        generator.init(seed);
        super.setStructureStarts(registryManager, noiseConfig, structureAccessor, chunk, structureTemplateManager, seed);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return 256;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        generator.fillChunk(chunk, noiseConfig.getLegacyWorldSeed());
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 64;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(world.getBottomY(), Collections.nCopies(64, Blocks.STONE.getDefaultState()).toArray(new BlockState[]{}));
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }
}
