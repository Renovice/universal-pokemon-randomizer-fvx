package com.dabomstew.pkrandom.graphics.packs;

import com.dabomstew.pkrandom.romhandlers.romentries.Gen1RomEntry;
import com.dabomstew.pkrandom.romhandlers.romentries.IniEntry;
import com.dabomstew.pkrandom.romhandlers.romentries.IniEntryReader;

import java.io.FileNotFoundException;
import java.util.List;

public class GraphicsPackEntry extends IniEntry {

    public static class GraphicsPackEntryReader extends IniEntryReader<GraphicsPackEntry> {

        private final String path;

        protected GraphicsPackEntryReader(String path) {
            super(DefaultReadMode.STRING);
            this.path = path;
            putSpecialKeyMethod("Description", GraphicsPackEntry::setDescription);
            putSpecialKeyMethod("From", GraphicsPackEntry::setFrom);
            putSpecialKeyMethod("Creator", GraphicsPackEntry::setOriginalCreator);
            putSpecialKeyMethod("Adapter", GraphicsPackEntry::setAdapter);
        }

        /**
         * Initiates a RomEntry of this class, since RomEntryReader can't do it on its own.<br>
         * MUST be overridden by any subclass.
         *
         * @param name The name of the RomEntry
         */
        @Override
        protected GraphicsPackEntry initiateEntry(String name) {
            return new GraphicsPackEntry(name, path);
        }
    }

    public static List<GraphicsPackEntry> readAllFromFolder(String folderPath) throws FileNotFoundException {
        GraphicsPackEntryReader reader = new GraphicsPackEntryReader(folderPath);
        return reader.readEntriesFromFile(folderPath + "/info.ini");
    }

    private enum Category {
        POKEMON, GAMES, OTHER
    }

    private final String path;
    private String description;
    private String from;
    private String originalCreator;
    private String adapter;

    public GraphicsPackEntry(String name, String path) {
        super(name);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public String getFrom() {
        return from;
    }

    private void setFrom(String from) {
        this.from = from;
    }

    public String getOriginalCreator() {
        return originalCreator;
    }

    private void setOriginalCreator(String originalCreator) {
        this.originalCreator = originalCreator;
    }

    public String getAdapter() {
        return adapter;
    }

    private void setAdapter(String adapter) {
        this.adapter = adapter;
    }

}
