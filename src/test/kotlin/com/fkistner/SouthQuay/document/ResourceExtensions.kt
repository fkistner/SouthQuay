package com.fkistner.SouthQuay.document

import java.net.URI
import java.nio.file.*
import java.nio.file.spi.FileSystemProvider
import kotlin.io.FileAlreadyExistsException


private fun resourceUriAsPath(uri: URI): Path? {
    var provider = FileSystemProvider.installedProviders()
            .firstOrNull { it.scheme.equals(uri.scheme, true) } ?: return null

    while (true) {
        try {
            return provider.getPath(uri)
        } catch (e: FileSystemNotFoundException) {
            println("Registering file system for $uri")
            try {
                provider = provider.newFileSystem(uri, mapOf("create".to("false"))).provider()
            } catch (e: FileAlreadyExistsException) {}
        }
    }
}

fun Path.readBytes(): ByteArray = Files.readAllBytes(this)
fun Path.readText() = String(readBytes())
fun ClassLoader.getResourceAsPath(name: String): Path? = resourceUriAsPath(getResource(name).toURI())
fun Any.getResourceAsPath(name: String) = resourceUriAsPath(javaClass.getResource(name).toURI())
fun getResourceAsPath(name: String) = Thread.currentThread().contextClassLoader.getResourceAsPath(name)
