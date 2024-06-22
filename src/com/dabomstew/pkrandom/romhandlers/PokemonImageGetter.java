package com.dabomstew.pkrandom.romhandlers;

import com.dabomstew.pkrandom.graphics.palettes.PaletteDescriptionTool;
import com.dabomstew.pkrandom.pokemon.Pokemon;

import java.awt.image.BufferedImage;

/**
 * A helping class for getting images of Pokemon out of the {@link RomHandler}, for purposes such as the mascot,
 * debugging, and peripheral tools (e.g. {@link PaletteDescriptionTool}).
 */
public abstract class PokemonImageGetter {
    protected Pokemon pk;
    protected boolean back;
    protected boolean shiny;
    protected boolean transparentBackground;
    protected boolean includePalette;
    protected int forme;

    public PokemonImageGetter(Pokemon pk) {
        this.pk = pk;
    }

    public PokemonImageGetter setBack(boolean back) {
        this.back = back;
        return this;
    }

    public PokemonImageGetter setShiny(boolean shiny) {
        this.shiny = shiny;
        return this;
    }

    /**
     * Decides which graphical forme should be gotten.
     * <br><br>
     * A graphical form is any variation in graphics, that is not due to gender or shininess.
     * Note that these might not correspond to alt formes loaded as {@link Pokemon} objects by the {@link RomHandler},
     * and that there are often <i>more</i> graphical formes, e.g. Unown and Arceus usually not getting Pokemon objects
     * for their formes.
     * <br><br>
     * If the given Pokemon is a base forme, this can be used to look through <i>all</i> graphical formes,
     * even those represented as alt forme Pokemon objects. In this case, 0 represents the base forme.
     * If the given Pokemon is an alt forme, 0 represents that alt forme, and no other value should be settable
     * (i.e. images will correspond to that form).
     * <br><br>
     * Throws an {@link IllegalArgumentException} if forme < 0, or if it's too high for the given {@link Pokemon};
     * see {@link #getGraphicalFormeAmount()}.
     */
    public PokemonImageGetter setGraphicalForme(int forme) {
        if (forme < 0) {
            throw new IllegalArgumentException("forme must be positive");
        }
        if (forme >= getGraphicalFormeAmount()) {
            throw new IllegalArgumentException("invalid/too high forme for this Pokemon, " + forme + " for " + pk.fullName());
        }
        this.forme = forme;
        return this;
    }

    public PokemonImageGetter setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
        return this;
    }

    public PokemonImageGetter setIncludePalette(boolean includePalette) {
        this.includePalette = includePalette;
        return this;
    }

    /**
     * Returns the amount of graphical formes for the {@link Pokemon} this PokemonImageGetter is set to work with.
     * I.e. returns the highest valid value for {@link #setGraphicalForme(int)}+1.
     */
    public int getGraphicalFormeAmount() {
        return 1;
    }

    public abstract BufferedImage get();

    /**
     * Returns a stitched together image of all the Pokemon's gettable images.
     */
    public abstract BufferedImage getFull();
}
