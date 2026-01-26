package edu.gva.es.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        
        // anyHost() cannot be used with allowCredentials = true
        // We must specify the exact hosts
        allowHost("0.0.0.0:8080", schemes = listOf("http"))
        allowHost("localhost:8080", schemes = listOf("http"))
        allowHost("127.0.0.1:8080", schemes = listOf("http"))
        
        // For local files (file://), origin is usually "null" or empty.
        // It's tricky to support 'file://' with credentials in some browsers.
        // Ideally, the user should serve the client via a local server (e.g. VS Code Live Server).
        // If the user opens index.html as a file, the Origin might be diff.
        // Let's try to add a wildcard logic or just specific hosts.
        
        // Allow typical Live Server ports just in case user uses them
        allowHost("127.0.0.1:5500", schemes = listOf("http"))
        allowHost("localhost:5500", schemes = listOf("http"))
        
        // If opened directly from file system
        // allowNonSimpleContentTypes = true // this is implicit usually
 
        
        allowCredentials = true
    }
}
