package cyborgcabbage.cabbagebeta.gen.beta;

import java.util.Random;

public class BetaSeeds {
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
    private final String[] SEED_ARRAY = {
            "6644803604819148923",//stampy
            "3811868026651017821",//antvenom, hunt for the golden apple
            "3257840388504953787",//pack png
            "2151901553968352745",//panorama
            "4090136037452000329",//Yog Cave
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
    };
}
