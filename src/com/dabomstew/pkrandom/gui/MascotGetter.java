package com.dabomstew.pkrandom.gui;

import com.dabomstew.pkrandom.romhandlers.Abstract3DSRomHandler;
import com.dabomstew.pkrandom.romhandlers.AbstractDSRomHandler;
import com.dabomstew.pkrandom.romhandlers.PokemonImageGetter;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

import java.awt.image.BufferedImage;
import java.util.Random;

public class MascotGetter {

    private final Random random;

    public MascotGetter(Random random) {
        this.random = random;
    }

    public BufferedImage getMascotImage(RomHandler romHandler) {
        if (romHandler.hasPokemonImageGetter()) {
            return getMascotUsingPIG(romHandler);
        } else if (romHandler instanceof Abstract3DSRomHandler ab3DSRomHandler) {
            return getMascotIcon(ab3DSRomHandler);
        }
        return null;
    }

    private BufferedImage getMascotUsingPIG(RomHandler romHandler) {
        PokemonImageGetter pig = romHandler.createPokemonImageGetter(romHandler.getPokemonSet().getRandom(random))
                .setTransparentBackground(true);
        if (romHandler.generationOfPokemon() != 1) {
            pig = pig.setShiny(random.nextInt(10) == 0);
        }
        if (pig instanceof AbstractDSRomHandler.DSPokemonImageGetter dsPig) {
            pig = dsPig.setGender(random.nextInt(2));
        }
        return pig.get();
    }

    private BufferedImage getMascotIcon(Abstract3DSRomHandler romHandler) {
        // ideally the 3DS games would have a PokemonImageGetter, but they don't, so we use this somewhat hacky
        // method instead to get a random Pokemon icon for the mascot.
        int iconIndex = random.nextInt(romHandler.getIconGARCSize() - 1) + 1;
        return romHandler.getPokemonIcon(iconIndex);
    }
}
