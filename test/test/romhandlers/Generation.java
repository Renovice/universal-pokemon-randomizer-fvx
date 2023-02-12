package test.romhandlers;

import com.dabomstew.pkrandom.romhandlers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A class solely for speeding up loading, in cases where the game's name is already known, but a RomHandler Factory
 * and a file name is needed. I.e. during testing.
 */
public class Generation {

    private static final Generation GEN1 = new Generation(Gen1RomHandler.Factory::new, ".gb");
    private static final Generation GEN2 = new Generation(Gen2RomHandler.Factory::new, ".gbc");
    private static final Generation GEN3 = new Generation(Gen3RomHandler.Factory::new, ".gba");
    private static final Generation GEN4 = new Generation(Gen4RomHandler.Factory::new, ".nds");
    private static final Generation GEN5 = new Generation(Gen5RomHandler.Factory::new, ".nds");
    private static final Generation GEN6 = new Generation(Gen6RomHandler.Factory::new, ".3ds");
    private static final Generation GEN7 = new Generation(Gen7RomHandler.Factory::new, ".3ds");

    public static final Map<String, Generation> GAME_TO_GENERATION = initGenerationMap();

    private static Map<String, Generation> initGenerationMap() {
        Map<String, Generation> generationMap = new HashMap<>();
        generationMap.put("Red", GEN1);
        generationMap.put("Green", GEN1);
        generationMap.put("Blue", GEN1);
        generationMap.put("Yellow", GEN1);
        generationMap.put("Gold", GEN2);
        generationMap.put("Silver", GEN2);
        generationMap.put("Crystal", GEN2);
        generationMap.put("Ruby", GEN3);
        generationMap.put("Sapphire", GEN3);
        generationMap.put("Fire Red", GEN3);
        generationMap.put("Leaf Green", GEN3);
        generationMap.put("Emerald", GEN3);
        generationMap.put("Diamond", GEN4);
        generationMap.put("Pearl", GEN4);
        generationMap.put("Platinum", GEN4);
        generationMap.put("HeartGold", GEN4);
        generationMap.put("SoulSilver", GEN4);
        generationMap.put("Black", GEN5);
        generationMap.put("White", GEN5);
        generationMap.put("Black 2", GEN5);
        generationMap.put("White 2", GEN5);
        generationMap.put("X", GEN6);
        generationMap.put("Y", GEN6);
        generationMap.put("Omega Ruby", GEN6);
        generationMap.put("Alpha Sapphire", GEN6);
        generationMap.put("Sun", GEN7);
        generationMap.put("Moon", GEN7);
        generationMap.put("Ultra Sun", GEN7);
        generationMap.put("Ultra Moon", GEN7);
        return generationMap;
    }

    private final Supplier<RomHandler.Factory> factorySupplier;
    private final String fileSuffix;

    private Generation(Supplier<RomHandler.Factory> factorySupplier, String fileSuffix) {
        this.factorySupplier = factorySupplier;
        this.fileSuffix = fileSuffix;
    }

    public RomHandler.Factory createFactory() {
        return factorySupplier.get();
    }

    public String getFileSuffix() {
        return fileSuffix;
    }
}
