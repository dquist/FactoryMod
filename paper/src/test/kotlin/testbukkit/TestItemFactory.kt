package testbukkit

import org.bukkit.Material
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.meta.*
import org.mockito.Mockito
import org.mockito.Mockito.mock

abstract class TestItemFactory : ItemFactory {

	companion object {
		fun create(): TestItemFactory {
			return mock(TestItemFactory::class.java, Mockito.CALLS_REAL_METHODS)
		}
	}

	// Note - does not contain all possible materials
	override fun getItemMeta(material: Material): ItemMeta? {
		return when (material) {
			Material.AIR -> null
			Material.WRITTEN_BOOK -> mock(BookMeta::class.java)
			Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION, Material.TIPPED_ARROW -> mock(PotionMeta::class.java)
			Material.MAP -> mock(PotionMeta::class.java)
			Material.ENCHANTED_BOOK -> mock(EnchantmentStorageMeta::class.java)
			Material.FURNACE, Material.CHEST, Material.TRAPPED_CHEST, Material.JUKEBOX, Material.DISPENSER, Material.DROPPER, Material.NOTE_BLOCK, Material.BEACON, Material.HOPPER -> TestBlockStateMeta(material)
			else -> TestDamageable(material)
		}
	}

	override fun equals(meta1: ItemMeta?, meta2: ItemMeta?): Boolean {
		return meta1?.equals(meta2) == true
	}
}
