package testbukkit

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.block.Chest
import org.bukkit.block.Furnace
import org.bukkit.block.data.BlockData
import org.bukkit.inventory.FurnaceInventory
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.mockito.Mockito
import java.util.HashMap
import kotlin.math.min
import org.bukkit.block.data.type.Furnace as FurnaceData

abstract class TestBlock : Block {
	var blockName: String? = null
	private lateinit var world: World
	lateinit var position: BlockPosition
	lateinit var matType: Material
	lateinit var blockState: BlockState
	lateinit var testBlockData: TestBlockData

	override fun getWorld(): World = world
	override fun getX(): Int = position.x
	override fun getY(): Int = position.y
	override fun getZ(): Int = position.z
	override fun getType(): Material = matType
	override fun getLocation(): Location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
	override fun getState(): BlockState = blockState
	override fun getBlockData(): BlockData = this.testBlockData
	override fun getRelative(face: BlockFace): Block {
		return when (face) {
			BlockFace.NORTH -> world.getBlockAt(position.x, position.y, position.z + 1)
			BlockFace.EAST -> world.getBlockAt(position.x + 1, position.y, position.z)
			BlockFace.SOUTH -> world.getBlockAt(position.x, position.y, position.z - 1)
			BlockFace.WEST -> world.getBlockAt(position.x - 1, position.y, position.z)
			BlockFace.UP -> world.getBlockAt(position.x, position.y + 1, position.z)
			BlockFace.DOWN -> world.getBlockAt(position.x, position.y - 1, position.z)
			BlockFace.SELF -> world.getBlockAt(position.x, position.y, position.z)
			else -> TODO()
		}
	}

	override fun getFace(block: Block): BlockFace? {
		return when {
			this.location == block.location -> BlockFace.SELF
			block.z - this.z == 1 -> BlockFace.NORTH
			this.z - block.z == 1 -> BlockFace.SOUTH
			block.x - this.x == 1 -> BlockFace.EAST
			this.x - block.x == 1 -> BlockFace.WEST
			block.y - this.y == 1 -> BlockFace.UP
			this.y - block.y == 1 -> BlockFace.DOWN
			else -> null
		}
	}


	fun setWorld(world: World) { this.world = world }
}

abstract class TestChestBlock : TestBlock() {
	lateinit var chestState: TestChestState

	override fun getState(): BlockState = chestState

	fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
		return chestState.chestInventory.addItem(*items)
	}

	fun getNumberOf(material: Material): Int {
		return chestState.chestInventory.getNumberOf(material)
	}
}

abstract class TestFurnaceBlock : TestBlock() {
	lateinit var furnaceState: TestFurnaceState

	override fun getState(): BlockState = furnaceState

	fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
		return furnaceState.furnaceInventory.addItem(*items)
	}
}

abstract class TestBlockState : BlockState {
	lateinit var testBlock: TestBlock
	lateinit var testData: MaterialData
	var testLightLevel: Byte = 0

	override fun getBlockData(): BlockData {
		return testBlock.blockData
	}
}

abstract class TestFurnaceState : TestBlockState(), Furnace {
	lateinit var furnaceInventory: TestFurnaceInventory

	override fun getInventory(): FurnaceInventory = furnaceInventory
}

abstract class TestChestState : TestBlockState(), Chest {
	lateinit var chestInventory: TestInventory

	override fun getInventory(): Inventory = chestInventory
}

abstract class TestInventory : Inventory {
	lateinit var inventoryLocation: Location
	lateinit var chestHolder: InventoryHolder
	var itemStacks = mutableMapOf<Int, ItemStack>()

	override fun getSize(): Int = 27
	override fun getMaxStackSize(): Int = 64
	override fun getHolder(): InventoryHolder = chestHolder
	override fun getLocation(): Location = inventoryLocation
	override fun getItem(index: Int): ItemStack? = itemStacks[index]

	override fun addItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
		val result = mutableMapOf<Int, ItemStack>()

		items.toList().forEachIndexed { index, itemStack ->
			var added = false
			for (i in 1..size) {
				if (itemStacks[i] == null) {
					itemStacks[i] = itemStack
					added = true
					break
				}
			}
			if (!added) {
				result[index] = itemStack
			}
		}
		return HashMap(result)
	}

	override fun getContents(): Array<ItemStack> {
		return itemStacks.values.toTypedArray()
	}

	override fun getStorageContents(): Array<ItemStack> {
		return itemStacks.values.toTypedArray()
	}

	override fun remove(item: ItemStack) {
		val keys = itemStacks.entries.filter { it.value == item }.map { it.key }
		keys.forEach { itemStacks.remove(it) }
	}

	override fun removeItem(vararg items: ItemStack): HashMap<Int, ItemStack> {
		val result = mutableMapOf<Int, ItemStack>()

		items.toList().forEachIndexed { index, itemStack ->
			var removed = false
			for (i in 1..size) {
				val invStack = itemStacks[i]
				if (invStack != null && invStack.isSimilar(itemStack)) {
					val toRemove = min(invStack.amount, itemStack.amount)
					invStack.amount = invStack.amount - toRemove
					itemStack.amount = itemStack.amount - toRemove
					if (itemStack.amount == 0) {
						removed = true
						break
					}
				}
			}
			if (!removed) {
				result[index] = itemStack
			}
		}
		return HashMap(result)
	}

	fun getNumberOf(material: Material): Int {
		return itemStacks.values.filter { it.type == material }.map { it.amount }.reduce { acc, i -> acc + i }
	}
}

abstract class TestFurnaceInventory : TestInventory(), FurnaceInventory {
	lateinit var furnaceHolder: TestFurnaceState
	override fun getHolder(): Furnace = furnaceHolder
}

data class BlockPosition(
	val x: Int,
	val y: Int,
	val z: Int,
)

abstract class TestBlockData : BlockData {
	lateinit var testBlock: TestBlock
}

abstract class TestFurnaceData : TestBlockData(), FurnaceData {
	lateinit var facingBlock: BlockFace

	override fun getFacing(): BlockFace = facingBlock
}

class TestBlockBuilder {
	var blockName: String? = null
	lateinit var world: World
	var posX: Int = 0
	var posZ: Int = 0
	var posY: Int = 100
	var matType: Material = Material.AIR
	var lightLevel: Byte = 0
	var facing: BlockFace? = null // Directional only
	var inventory: TestInventory? = null // Chest only
	var furnaceInventory: TestFurnaceInventory? = null // Furnace only

	fun named(blockName: String) = apply { this.blockName = blockName }
	fun world(world: World) = apply { this.world = world }
	fun x(x: Int) = apply { posX = x }
	fun y(y: Int) = apply { posY = y }
	fun z(z: Int) = apply { posZ = z }
	fun at(x: Int, z: Int, y: Int) = apply { x(x); z(z); y(y); }
	fun at(x: Int, z: Int) = apply { x(x); z(z) }
	fun type(type: Material) = apply { this.matType = type }
	fun lightLevel(lightLevel: Byte) = apply { this.lightLevel = lightLevel }
	fun facing(blockFace: BlockFace) = apply { this.facing = blockFace }
	fun inventory(inventory: TestInventory) = apply { this.inventory = inventory }
	fun furnaceInventory(furnaceInventory: TestFurnaceInventory) = apply { this.furnaceInventory = furnaceInventory }

	fun build(): TestBlock {
		return when (matType) {
			Material.CHEST -> {
				Mockito.mock(TestChestBlock::class.java, Mockito.CALLS_REAL_METHODS).also {
					it.blockName = blockName
					it.world = world
					it.position = BlockPosition(posX, posY, posZ)
					it.matType = matType
					it.chestState = buildChestState(it)
					it.testBlockData = buildBlockData(it)
				}
			}
			Material.FURNACE -> {
				Mockito.mock(TestFurnaceBlock::class.java, Mockito.CALLS_REAL_METHODS).also {
					it.blockName = blockName
					it.world = world
					it.position = BlockPosition(posX, posY, posZ)
					it.matType = matType
					it.furnaceState = buildFurnaceState(it)
					it.testBlockData = buildBlockData(it)
				}
			}
			else -> Mockito.mock(TestBlock::class.java, Mockito.CALLS_REAL_METHODS).also {
				it.blockName = blockName
				it.world = world
				it.position = BlockPosition(posX, posY, posZ)
				it.matType = matType
				it.blockState = buildState(it)
				it.testBlockData = buildBlockData(it)
			}
		}
	}

	private fun buildState(block: TestBlock): BlockState {
		return Mockito.mock(TestBlockState::class.java, Mockito.CALLS_REAL_METHODS).also {
			it.testBlock = block
			it.testData = MaterialData(block.type)
			it.testLightLevel = lightLevel
		}
	}

	private fun buildFurnaceState(block: TestFurnaceBlock): TestFurnaceState {
		return Mockito.mock(TestFurnaceState::class.java, Mockito.CALLS_REAL_METHODS).also {
			it.testBlock = block
			it.testData = MaterialData(block.type)
			it.testLightLevel = lightLevel
			it.furnaceInventory = furnaceInventory ?: buildFurnaceInventory(it)
		}
	}

	private fun buildChestState(block: TestChestBlock): TestChestState {
		return Mockito.mock(TestChestState::class.java, Mockito.CALLS_REAL_METHODS).also {
			it.testBlock = block
			it.testData = MaterialData(block.type)
			it.testLightLevel = lightLevel
			it.chestInventory = inventory ?: buildChestInventory(it)
		}
	}

	private fun buildBlockData(block: TestBlock): TestBlockData {
		val blockData = if (block.type == Material.FURNACE) {
			Mockito.mock(TestFurnaceData::class.java, Mockito.CALLS_REAL_METHODS).also {
				it.facingBlock = facing ?: BlockFace.WEST
			}
		} else {
			Mockito.mock(TestBlockData::class.java, Mockito.CALLS_REAL_METHODS)
		}
		return blockData.also {
			it.testBlock = block
		}
	}

	fun buildChestInventory(chest: TestChestState): TestInventory {
		return Mockito.mock(TestInventory::class.java, Mockito.CALLS_REAL_METHODS).also {
			it.inventoryLocation = chest.testBlock.location
			it.chestHolder = chest
			it.itemStacks = mutableMapOf()
		}
	}

	fun buildFurnaceInventory(chest: TestFurnaceState): TestFurnaceInventory {
		return Mockito.mock(TestFurnaceInventory::class.java, Mockito.CALLS_REAL_METHODS).also {
			it.inventoryLocation = chest.testBlock.location
			it.furnaceHolder = chest
			it.itemStacks = mutableMapOf()
		}
	}
}
