package io.github.tropheusj.stonecutter_recipe_tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

public class StonecutterRecipeTagManager {
	public static final Identifier SYNC_STONECUTTER_RECIPE_TAGS_PACKET_ID = Utils.asId("sync_stonecutter_recipe_tags");

	private static final Map<Identifier, TagKey<Item>> recipeTags = new HashMap<>();
	private static final Object2IntMap<Item> fullBlockAmounts = new Object2IntOpenHashMap<>();
	private static final List<ToIntFunction<Item>> fullBlockAmountProviders = new ArrayList<>();

	static {
		registerFullBlockAmountProvider(StonecutterRecipeTagManager::defaultAmountProvider);
		setAmountForFullBlock(Items.SNOW, 8);
	}

	/**
	 * Registers or gets an already registered tag for the provided id.<br>
	 * Registered tags will be used as stonecutter recipes.<br>
	 * Tags registered manually will not persist through resource reloads.
	 * @param id The id of the tag
	 * @return The registered tag
	 */
	public static TagKey<Item> registerOrGet(Identifier id) {
		TagKey<Item> tag = getRegisteredTag(id);
		if (tag == null) {
			tag = Utils.getItemTag(id);
			register(tag);
		}
		return tag;
	}

	/**
	 * Registers an already existing tag if a tag with the same id has not already been registered.<br>
	 * Registered tags will be used as stonecutter recipes.<br>
	 * Tags registered manually will not persist through resource reloads.
	 * @param tag The tag to register
	 */
	public static void register(TagKey<Item> tag) {
		recipeTags.putIfAbsent(tag.id(), tag);
	}

	/**
	 * Gets a tag from an id that was previously used for registration.
	 * @param id The id of the registered tag
	 * @return The registered tag, or null if no tag for the specified id exists
	 */
	@Nullable
	public static TagKey<Item> getRegisteredTag(Identifier id) {
		return recipeTags.get(id);
	}

	/**
	 * Gets all stonecutter recipe tags the given item is in.
	 * @param item The item to check
	 * @return All stonecutter recipe tags the item is in
	 */
	public static List<TagKey<Item>> getRecipeTags(Item item) {
		List<TagKey<Item>> tags = new ArrayList<>();
		for (TagKey<Item> tag : recipeTags.values()) {
			if (item.getRegistryEntry().isIn(tag)) {
				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * @see StonecutterRecipeTagManager#getItemCraftCount(Item)
	 */
	public static List<TagKey<Item>> getRecipeTags(ItemStack stack) {
		return getRecipeTags(stack.getItem());
	}

	/**
	 * Register a craft count for an item.<br>
	 * This should be used when an item requires a custom craft count.
	 * By default, all items have a count of one, except slabs, which have a count of two.<br>
	 * Example use case: quarter slabs
	 * @param item Item to register a count for
	 * @param count The count for this item
	 * @deprecated use setAmountForFullBlock
	 */
	@Deprecated(forRemoval = true)
	public static void registerItemCraftCount(Item item, int count) {
		setAmountForFullBlock(item, count);
	}

	/**
	 * Set the number of items needed to be equivalent to a full block.
	 * For example, slabs have a value of 2, while snow layers have a value of 8.
	 * @param item the item to associate this amount with
	 * @param amount the amount of this item needed to equal a full block
	 */
	public static void setAmountForFullBlock(Item item, int amount) {
		if (amount == 0)
			throw new IllegalArgumentException("Cannot add a full block amount of 0 for " + item);
		int old = fullBlockAmounts.put(item, amount);
		if (old != 0)
			throw new IllegalStateException("A full block amount (" + old + ") has already been registered for " + item);
	}

	/**
	 * Get the required amount of items needed to craft this item.<br>
	 * Amount is retrieved from {@link StonecutterRecipeTagManager#fullBlockAmounts}.
	 * If no value is found, slabs return 2, while all other items return 1.
	 * @deprecated getAmountForFullBlock
	 */
	@Deprecated(forRemoval = true)
	public static int getItemCraftCount(Item item) {
		return getAmountForFullBlock(item);
	}

	/**
	 * Get the amount of this item that is required to equal a full block.
	 * For example, slabs have a value of 2, while snow layers have a value of 8.
	 */
	public static int getAmountForFullBlock(Item item) {
		int amount = fullBlockAmounts.getInt(item);
		if (amount != 0) {
			return amount;
		}
		for (ToIntFunction<Item> provider : fullBlockAmountProviders) {
			amount = provider.applyAsInt(item);
			if (amount < 0) {
				throw new IllegalStateException("Full block provider [" + provider.getClass().getName() +
						"] returned an invalid negative value of " + amount);
			} else if (amount > 0) {
				return amount;
			}
		}
		return 1;
	}

	/**
	 * @deprecated getAmountForFullBlock
	 */
	@Deprecated(forRemoval = true)
	public static int getItemCraftCount(ItemStack stack) {
		return getAmountForFullBlock(stack);
	}

	/**
	 * @see StonecutterRecipeTagManager#getAmountForFullBlock(Item)
	 */
	public static int getAmountForFullBlock(ItemStack stack) {
		return getAmountForFullBlock(stack.getItem());
	}

	/**
	 * Register a provider for full block amounts. Can be used to provide amounts for a wide range of items.
	 * For example, this is used to provide an amount of 2 for all slab blocks.
	 * Providers should return 0 if they do not provide a value for a given item. Negative values are invalid.
	 */
	public static void registerFullBlockAmountProvider(ToIntFunction<Item> provider) {
		fullBlockAmountProviders.add(provider);
	}

	/**
	 * Returns the number of full blocks the given stacks provides based on it's size and amount for a full block.
	 */
	public static int fullBlocksProvidedBy(ItemStack stack) {
		int fullBlockAmount = getAmountForFullBlock(stack);
		return stack.getCount() / fullBlockAmount;
	}

	private static int defaultAmountProvider(Item item) {
		if (item instanceof BlockItem block && block.getBlock() instanceof SlabBlock)
			return 2;
		return 0;
	}

	@Internal
	public static List<FakeStonecuttingRecipe> makeFakeRecipes(ItemStack inputStack) {
		Item input = inputStack.getItem();
		int fullBlockAmount = getAmountForFullBlock(input);
		int fullBlocksProvided = fullBlocksProvidedBy(inputStack);
		List<FakeStonecuttingRecipe> recipes = new ArrayList<>();
		if (fullBlocksProvided <= 0) return recipes; // todo: partial crafting, ex. 1 slab <-> 1 slab instead of 2 <-> 2
		for (TagKey<Item> recipeTag : getRecipeTags(input)) {
			for (RegistryEntry<Item> entry : Registries.ITEM.iterateEntries(recipeTag)) {
				Item output = entry.value();
				if (output == input) continue; // don't allow crafting into self
				// each recipe only consumes the equivalent of 1 block
				ItemStack consumedInput = inputStack.copy();
				consumedInput.setCount(fullBlockAmount);
				ItemStack outputStack = output.getDefaultStack();
				int outputFullBlockAmount = getAmountForFullBlock(output);
				outputStack.setCount(outputFullBlockAmount);
				FakeStonecuttingRecipe recipe = new FakeStonecuttingRecipe(consumedInput, outputStack);
				recipes.add(recipe);
			}
		}
		return recipes;
	}

	@Internal
	public static void clearTags() {
		recipeTags.clear();
	}

	@Internal
	public static void toPacketBuf(PacketByteBuf buf) {
		buf.writeCollection(recipeTags.keySet(), (buf1, id) -> buf.writeIdentifier(id));
	}

	@Internal
	public static void fromPacketBuf(PacketByteBuf buf) {
		clearTags();
		List<Identifier> ids = buf.readCollection(ArrayList::new, PacketByteBuf::readIdentifier);
		for (Identifier id : ids) {
			registerOrGet(id);
		}
	}

	@Internal
	public static Packet<?> toSyncPacket() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		toPacketBuf(buf);
		return new CustomPayloadS2CPacket(SYNC_STONECUTTER_RECIPE_TAGS_PACKET_ID, buf);
	}
}
