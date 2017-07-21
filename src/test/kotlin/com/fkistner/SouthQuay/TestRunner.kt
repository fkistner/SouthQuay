package com.fkistner.SouthQuay
import org.junit.extensions.cpsuite.*
import org.junit.runner.JUnitCore
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val searchInJars = true
    val classFilter = arrayOf("com.fkistner.SouthQuay.*")
    val suiteTypes = arrayOf(SuiteType.TEST_CLASSES)
    val baseClasses = arrayOf(Any::class.java)
    val excludedBaseClasses = emptyArray<Class<*>>()
    val classpathPropertyName = "java.class.path"

    val testClasses = ClasspathFinderFactory()
            .create(searchInJars, classFilter, suiteTypes, baseClasses, excludedBaseClasses, classpathPropertyName)
            .find().toTypedArray()

    val result = JUnitCore().run(*testClasses)
    println("Tests ignored: %4d".format(result.ignoreCount))
    println("Tests run:     %4d".format(result.runCount))
    println("Tests failed:  %4d".format(result.failureCount))
    for (failure in result.failures) println("${failure.testHeader}: ${failure.exception.toFullString()}")

    println("Run time:      %4.1f s".format(result.runTime / 1000.0))
    exitProcess(if (result.wasSuccessful()) 0 else 1)
}
