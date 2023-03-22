package testbukkit

class TestBukkit(
	val world: TestWorld,
)

class TestBukkitBuilder() {
	val worldBuilder: TestWorldBuilder = TestWorldBuilder()

	fun build(): TestBukkit {
		return TestBukkit(
			world = worldBuilder.build(),
		)
	}
}

fun testBukkit(init: TestBukkitBuilder.() -> Unit = { }): TestBukkit {
	val builder = TestBukkitBuilder()
	builder.init()
	return builder.build()
}
