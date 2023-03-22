package com.github.igotyou.FactoryMod

import com.github.igotyou.FactoryMod.factories.FurnCraftChestFactory
import com.github.igotyou.FactoryMod.interactionManager.IInteractionManager
import com.github.igotyou.FactoryMod.powerManager.FurnacePowerManager
import com.github.igotyou.FactoryMod.recipes.ProductionRecipe
import com.github.igotyou.FactoryMod.recipes.RepairRecipe
import com.github.igotyou.FactoryMod.recipes.scaling.ProductionRecipeModifier
import com.github.igotyou.FactoryMod.repairManager.PercentageHealthRepairManager
import com.github.igotyou.FactoryMod.structures.FurnCraftChestStructure
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.UnsafeValues
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.BeforeAll
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import testbukkit.*
import vg.civcraft.mc.civmodcore.inventory.items.ItemMap
import java.util.logging.Logger
import kotlin.test.*

class FurnCraftChestFactoryTest {

	lateinit var tb: TestBukkit
	lateinit var player: TestPlayer
	lateinit var furnace: TestFurnaceBlock
	lateinit var craftingTable: TestBlock
	lateinit var chest: TestChestBlock
	lateinit var structure: FurnCraftChestStructure
	lateinit var transmutationRecipe: ProductionRecipe
	lateinit var repairRecipe: RepairRecipe
	lateinit var ipm: FurnacePowerManager
	lateinit var rm: PercentageHealthRepairManager

	val im = mock<IInteractionManager>()

	// The Bukkit server can only be defined once
	companion object {
		lateinit var server: Server

		@BeforeAll
		@JvmStatic
		fun setupSuite() {
			val unsafeValues = mock<UnsafeValues> {
				on { getTranslationKey(isA<ItemStack>()) } doReturn "mockTranslation"
			}
			server = mock {
				on { logger } doReturn Logger.getGlobal()
				on { itemFactory } doReturn TestItemFactory.create()
				on { pluginManager } doReturn mock()
				on { scheduler } doReturn mock()
				on { unsafe } doReturn unsafeValues
			}
			Bukkit.setServer(server)
		}
	}

	@BeforeTest
	fun setup() {
		FactoryMod.plugin = mock {
			on { server } doReturn server
		}
		tb = testBukkit() {
			worldBuilder.addPlayer()
			worldBuilder.addBlock {
				named("furnace")
				matType = Material.FURNACE
				at(0, 0)
				facing(BlockFace.WEST)
			}
			worldBuilder.addBlock {
				named("table")
				matType = Material.CRAFTING_TABLE
				at(0, 1)
			}
			worldBuilder.addBlock {
				named("chest")
				matType = Material.CHEST
				at(0, 2)
			}
		}
		player = tb.world.testPlayers[0]
		furnace = tb.world.getTypedBlock("furnace")
		craftingTable = tb.world.getBlock("table")
		chest = tb.world.getTypedBlock("chest")
		structure = FurnCraftChestStructure(craftingTable)
		transmutationRecipe = ProductionRecipe(
			"transmutation",
			"transmutation",
			1,
			ItemMap(ItemStack(Material.LEAD)),
			ItemMap(ItemStack(Material.GOLD_INGOT)),
			ItemStack(Material.GOLD_INGOT),
			ProductionRecipeModifier(),
		)
		repairRecipe = RepairRecipe(
			"repair",
			"repair",
			1,
			ItemMap(ItemStack(Material.DIAMOND)),
			10,
		)
		ipm = FurnacePowerManager(furnace, ItemStack(Material.CHARCOAL, 1), 100)
		rm = PercentageHealthRepairManager(100, 100, 100, 100, 100)
	}

	@Test
	fun `activate auto-factory production recipe with fuel and ingredients`() {
		// Arrange - Auto-select enabled
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(transmutationRecipe)
		factory.isAutoSelect = true

		// Add input materials and fuel
		chest.addItem(ItemStack(Material.LEAD, 64))
		furnace.addItem(ItemStack(Material.CHARCOAL, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)
		assertTrue { factory.isActive }

		// Run another iteration - The output is produced
		factory.run()
		assertEquals(63, chest.getNumberOf(Material.LEAD))
		assertEquals(1, chest.getNumberOf(Material.GOLD_INGOT))
		assertTrue { factory.isActive }
	}

	@Test
	fun `activate factory production recipe with fuel and ingredients`() {
		// Arrange - Auto-select enabled
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(transmutationRecipe)

		// Add input materials and fuel
		chest.addItem(ItemStack(Material.LEAD, 64))
		furnace.addItem(ItemStack(Material.CHARCOAL, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)
		assertTrue { factory.isActive }

		// Run another iteration - The output is produced
		factory.run()
		assertEquals(63, chest.getNumberOf(Material.LEAD))
		assertEquals(1, chest.getNumberOf(Material.GOLD_INGOT))
		assertTrue { factory.isActive }
	}

	@Test
	fun `activate auto-factory when in disrepair`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(transmutationRecipe)
		factory.isAutoSelect = true
		rm.setHealth(0)

		// Add input materials and fuel
		chest.addItem(ItemStack(Material.LEAD, 64))
		furnace.addItem(ItemStack(Material.CHARCOAL, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}

	@Test
	fun `activate factory when in disrepair`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(transmutationRecipe)
		rm.setHealth(0)

		// Add input materials and fuel
		chest.addItem(ItemStack(Material.LEAD, 64))
		furnace.addItem(ItemStack(Material.CHARCOAL, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}

	@Test
	fun `activate auto-factory when no fuel`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(transmutationRecipe)
		factory.isAutoSelect = true

		// Add input materials and no fuel
		chest.addItem(ItemStack(Material.LEAD, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}

	@Test
	fun `activate factory when no fuel`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(transmutationRecipe)

		// Add input materials and no fuel
		chest.addItem(ItemStack(Material.LEAD, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}

	@Test
	fun `activate auto-factory repair recipe when no ingredients`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(repairRecipe)
		factory.isAutoSelect = true

		// Add input materials and no fuel
		chest.addItem(ItemStack(Material.DIRT, 64))
		furnace.addItem(ItemStack(Material.CHARCOAL, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}

	@Test
	fun `activate factory repair recipe when no ingredients`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(repairRecipe)

		// Add input materials and no fuel
		chest.addItem(ItemStack(Material.DIRT, 64))
		furnace.addItem(ItemStack(Material.CHARCOAL, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}

	@Test
	fun `activate auto-factory repair recipe when no fuel`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(repairRecipe)
		factory.isAutoSelect = true

		// Add input materials and no fuel
		chest.addItem(ItemStack(Material.DIAMOND, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}

	@Test
	fun `activate factory repair recipe when no fuel`() {
		// Arrange - Furnace is in disrepair
		val factory = FurnCraftChestFactory(im, rm, ipm, structure, 1, "testFactory", listOf(transmutationRecipe, repairRecipe), 1.0)
		factory.setRecipe(repairRecipe)

		// Add input materials and no fuel
		chest.addItem(ItemStack(Material.DIAMOND, 64))

		// Activate the furnace
		factory.attemptToActivate(player, false)

		// The factory should not turn on
		assertFalse { factory.isActive }
	}
}
