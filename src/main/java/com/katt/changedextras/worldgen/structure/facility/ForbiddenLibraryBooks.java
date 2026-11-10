package com.katt.changedextras.worldgen.structure.facility;

import com.katt.changedextras.ChangedExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fills every chiseled bookshelf of the forbidden library, per slot:
 * 70% normal books, 29% enchanted books (weighted by enchantment rarity, how commonly
 * players use the enchantment, and inversely by enchantment level) and 1% written books
 * pulled from the structure files of this mod and of Changed Addon.
 */
public final class ForbiddenLibraryBooks {
    private ForbiddenLibraryBooks() {}

    /** Structure namespaces scanned for written books. */
    private static final Set<String> BOOK_SOURCE_NAMESPACES = Set.of("changedextras", "changed_addon");

    /** Enchantments players commonly seek out; these show up more often in the library. */
    private static final Set<String> COMMONLY_USED_ENCHANTMENTS = Set.of(
            "minecraft:protection", "minecraft:fire_protection", "minecraft:feather_falling",
            "minecraft:projectile_protection", "minecraft:respiration", "minecraft:aqua_affinity",
            "minecraft:depth_strider", "minecraft:sharpness", "minecraft:smite",
            "minecraft:bane_of_arthropods", "minecraft:fire_aspect", "minecraft:looting",
            "minecraft:efficiency", "minecraft:silk_touch", "minecraft:unbreaking",
            "minecraft:fortune", "minecraft:power", "minecraft:punch", "minecraft:flame",
            "minecraft:infinity", "minecraft:mending", "minecraft:luck_of_the_sea", "minecraft:lure");

    private static volatile List<ItemStack> cachedWrittenBooks = null;
    private static volatile List<WeightedEnchantment> cachedEnchantments = null;

    private record WeightedEnchantment(Enchantment enchantment, int weight) {}

    public static void fillBookshelves(WorldGenLevel level, BoundingBox pieceBox, BoundingBox chunkBox, RandomSource random) {
        int minX = Math.max(pieceBox.minX(), chunkBox.minX());
        int minY = Math.max(pieceBox.minY(), chunkBox.minY());
        int minZ = Math.max(pieceBox.minZ(), chunkBox.minZ());
        int maxX = Math.min(pieceBox.maxX(), chunkBox.maxX());
        int maxY = Math.min(pieceBox.maxY(), chunkBox.maxY());
        int maxZ = Math.min(pieceBox.maxZ(), chunkBox.maxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof ChiseledBookShelfBlock))
                        continue;
                    if (!(level.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity bookshelf))
                        continue;

                    NonNullList<ItemStack> books = NonNullList.withSize(
                            ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(), ItemStack.EMPTY);
                    for (int slot = 0; slot < books.size(); slot++)
                        books.set(slot, randomBook(level, random));

                    CompoundTag tag = new CompoundTag();
                    ContainerHelper.saveAllItems(tag, books);
                    bookshelf.load(tag); // vanilla load() does not refresh slot occupancy blockstate properties

                    BlockState filled = state;
                    for (BooleanProperty property : ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES)
                        filled = filled.setValue(property, Boolean.TRUE);
                    level.setBlock(pos, filled, 2);
                }
            }
        }
    }

    private static ItemStack randomBook(WorldGenLevel level, RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 70)
            return new ItemStack(Items.BOOK);
        if (roll < 99)
            return randomEnchantedBook(random);
        return randomWrittenBook(level, random);
    }

    private static ItemStack randomWrittenBook(WorldGenLevel level, RandomSource random) {
        List<ItemStack> pool = writtenBookPool(level.getLevel().getServer());
        if (pool.isEmpty())
            return new ItemStack(Items.BOOK);
        return pool.get(random.nextInt(pool.size())).copy();
    }

    private static ItemStack randomEnchantedBook(RandomSource random) {
        List<WeightedEnchantment> pool = enchantmentPool();
        int totalWeight = 0;
        for (WeightedEnchantment entry : pool)
            totalWeight += entry.weight();
        if (totalWeight <= 0)
            return new ItemStack(Items.BOOK);

        int roll = random.nextInt(totalWeight);
        Enchantment enchantment = pool.get(pool.size() - 1).enchantment();
        for (WeightedEnchantment entry : pool) {
            if (roll < entry.weight()) {
                enchantment = entry.enchantment();
                break;
            }
            roll -= entry.weight();
        }
        return EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(enchantment, randomLevel(enchantment, random)));
    }

    /** Picks an enchantment level between the minimum and maximum, favouring lower levels. */
    private static int randomLevel(Enchantment enchantment, RandomSource random) {
        int min = enchantment.getMinLevel();
        int max = enchantment.getMaxLevel();
        if (min >= max)
            return min;

        int totalWeight = 0;
        for (int level = min; level <= max; level++)
            totalWeight += max - level + 1;
        int roll = random.nextInt(totalWeight);
        for (int level = min; level <= max; level++) {
            if (roll < max - level + 1)
                return level;
            roll -= max - level + 1;
        }
        return max;
    }

    private static List<WeightedEnchantment> enchantmentPool() {
        List<WeightedEnchantment> pool = cachedEnchantments;
        if (pool == null) {
            pool = new ArrayList<>();
            for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
                ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                int weight = enchantment.getRarity().getWeight();
                if (id != null && COMMONLY_USED_ENCHANTMENTS.contains(id.toString()))
                    weight *= 3;
                pool.add(new WeightedEnchantment(enchantment, weight));
            }
            cachedEnchantments = pool;
        }
        return pool;
    }

    private static List<ItemStack> writtenBookPool(MinecraftServer server) {
        List<ItemStack> pool = cachedWrittenBooks;
        if (pool == null) {
            List<ItemStack> discovered = new ArrayList<>();
            server.getResourceManager().listResources("structures", id -> id.getPath().endsWith(".nbt"))
                    .forEach((id, resource) -> {
                        if (!BOOK_SOURCE_NAMESPACES.contains(id.getNamespace()))
                            return;
                        try (InputStream stream = resource.open()) {
                            collectWrittenBooks(NbtIo.readCompressed(stream), discovered);
                        } catch (IOException e) {
                            ChangedExtras.LOGGER.warn("Failed to read structure {} while collecting written books", id, e);
                        }
                    });
            ChangedExtras.LOGGER.info("Forbidden library written book pool: {} books", discovered.size());
            cachedWrittenBooks = discovered;
            pool = discovered;
        }
        return pool;
    }

    private static void collectWrittenBooks(CompoundTag tag, List<ItemStack> books) {
        for (String key : tag.getAllKeys())
            collectFromTag(tag.get(key), books);
    }

    private static void collectFromTag(Tag tag, List<ItemStack> books) {
        if (tag instanceof ListTag list) {
            for (Tag element : list)
                collectFromTag(element, books);
        } else if (tag instanceof CompoundTag compound) {
            if ("minecraft:written_book".equals(compound.getString("id")) && compound.contains("Count"))
                books.add(ItemStack.of(compound));
            else
                collectWrittenBooks(compound, books);
        }
    }
}
