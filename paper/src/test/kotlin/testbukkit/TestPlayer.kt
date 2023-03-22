package testbukkit

import org.bukkit.entity.Player
import org.mockito.Mockito

abstract class TestPlayer : Player {
	lateinit var playerName: String

	override fun getName(): String = playerName
}

class TestPlayerBuilder {
	var playerName: String = "ttk2"

	fun build(): TestPlayer {
		return Mockito.mock(TestPlayer::class.java, Mockito.CALLS_REAL_METHODS).also {
			it.playerName = playerName
		}
	}
}
