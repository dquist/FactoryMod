plugins {
	id("io.papermc.paperweight.userdev")
	kotlin("jvm") version "1.8.10"
}

dependencies {
	paperDevBundle("1.18.2-R0.1-SNAPSHOT")

	compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
	compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
	compileOnly("net.civmc.civmodcore:civmodcore-paper:2.3.5:dev-all")
	compileOnly("net.civmc.namelayer:namelayer-paper:3.0.3:dev")
	compileOnly("net.civmc.citadel:citadel-paper:5.1.2:dev")

	testImplementation(kotlin("test"))
	testImplementation("org.mockito:mockito-core:5.2.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
	testImplementation("net.civmc.civmodcore:civmodcore-paper:2.3.5")
}

tasks {
	test {
		useJUnitPlatform()
	}
}

kotlin {
	jvmToolchain(11)
}
