package com.katt.changedextras.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ModJsonProvider implements DataProvider {
    private final PackOutput output;
    private final String name;
    private final List<Entry> entries = new ArrayList<>();

    protected ModJsonProvider(PackOutput output, String name) {
        this.output = output;
        this.name = name;
    }

    protected abstract void generate();

    protected void write(PackOutput.Target target, String directory, String id, JsonObject json) {
        this.entries.add(new Entry(target, directory, new ResourceLocation("changedextras", id), json));
    }

    protected void write(PackOutput.Target target, String directory, ResourceLocation id, JsonObject json) {
        this.entries.add(new Entry(target, directory, id, json));
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput cache) {
        this.entries.clear();
        this.generate();
        List<CompletableFuture<?>> futures = new ArrayList<>(this.entries.size());
        for (Entry entry : this.entries) {
            Path path = this.output.getOutputFolder(entry.target)
                    .resolve(entry.id.getNamespace())
                    .resolve(entry.directory)
                    .resolve(entry.id.getPath() + ".json");
            futures.add(DataProvider.saveStable(cache, entry.json, path));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public final String getName() {
        return this.name;
    }

    private record Entry(PackOutput.Target target, String directory, ResourceLocation id, JsonObject json) {
    }
}
