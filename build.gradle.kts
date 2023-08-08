plugins {
    `cpp-library`
}

library {
    source.from(file("src"))
    privateHeaders.from(file("src"))
    publicHeaders.from(listOf("src/include")) // We don't really need this but it's required by the cpp-library plugin

    targetMachines.set(listOf(machines.macOS.x86_64, machines.linux.x86_64))

    binaries.configureEach {
        compileTask.get().macros.put("PROFILER_VERSION", "\"${project.version}\"")

        val javaHome = System.getProperty("java.home");
        compileTask.get().includes.from(listOf(
            "${javaHome}/include",
            "${javaHome}/include/linux"
        ))

        compileTask.get().compilerArgs.addAll(listOf(
            "-fno-omit-frame-pointer",
            "-momit-leaf-frame-pointer",
            "-fvisibility=hidden",
            "-fdata-sections",
            "-ffunction-sections",
            "-std=c++11",
            "-I${project(":sentry-profiling:async-profiler-java").buildDir.absolutePath}/classes/java/main",
        ))
    }
}

tasks.withType<CppCompile>().configureEach {
    dependsOn(":sentry-profiling:async-profiler-java:classes")
}