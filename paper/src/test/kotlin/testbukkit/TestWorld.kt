package testbukkit

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.*

abstract class TestWorld : World {
	lateinit var worldName: String
	lateinit var uuid: UUID
	lateinit var env: World.Environment
	lateinit var blocks: MutableMap<BlockPosition, TestBlock>
	lateinit var testPlayers: List<TestPlayer>

	override fun getName(): String = worldName
	override fun getUID(): UUID = uuid
	override fun getEnvironment(): World.Environment = env

	fun addBlock(block: TestBlock) {
		this.blocks[block.position] = block
	}

	override fun getBlockAt(x: Int, y: Int, z: Int): Block {
		return blocks[BlockPosition(x, y, z)] ?: TestBlockBuilder().also { it.world = this }.build()
	}

	override fun getBlockAt(l: Location): Block {
		return blocks[BlockPosition(l.blockX, l.blockY, l.blockZ)] ?: TestBlockBuilder().also { it.world = this }.build()
	}

	override fun getPlayers(): List<Player> {
		return testPlayers.map { it }
	}

	fun getTestBlockAt(x: Int, y: Int, z: Int): TestBlock? {
		return blocks[BlockPosition(x, y, z)]
	}

	fun getBlock(name: String): TestBlock {
		return blocks.values.first { it.blockName == name }
	}

	inline fun <reified T : TestBlock> getTypedBlock(name: String): T {
		return blocks.values.filterIsInstance<T>().first { it.blockName == name }
	}
}

class TestWorldBuilder {
	var worldName: String = "World"
	var uuid: UUID = UUID.randomUUID()
	var env: World.Environment = World.Environment.NORMAL
	var blocks = mutableSetOf<TestBlockBuilder>()
	var players = mutableListOf<TestPlayerBuilder>()

	fun addBlock(init: TestBlockBuilder.() -> Unit) = apply {
		blocks.add(TestBlockBuilder().also(init))
	}

	@JvmOverloads
	fun addPlayer(init: TestPlayerBuilder.() -> Unit = { }) = apply {
		players.add(TestPlayerBuilder().also(init))
	}

	fun build(): TestWorld {
		return mock(TestWorld::class.java, Mockito.CALLS_REAL_METHODS).also { world ->
			world.worldName = "World"
			world.uuid = uuid
			world.env = env
			blocks.forEach { it.world = world }
			world.blocks = blocks.map { it.build() }.associateBy { it.position }.toMutableMap()
			world.testPlayers = players.map { it.build() }
		}
	}
}
