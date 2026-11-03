package com.example

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun healthCheckAnswers() = testApplication {
        application { module() }
        val response = client.get("/healthz")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("ok\n", response.bodyAsText())
    }

    @Test
    fun landingPageShowsTheRuntime() = testApplication {
        application { module() }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.contentType()!!.match(ContentType.Text.Html))
        val body = response.bodyAsText()
        assertTrue(body.contains(KotlinVersion.CURRENT.toString()))
        assertTrue(!body.contains("{{"))
    }

    @Test
    fun assetsAreServed() = testApplication {
        application { module() }
        val response = client.get("/assets/styles.css")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.contentType()!!.match(ContentType.Text.CSS))
    }

    @Test
    fun statusIsJson() = testApplication {
        application { module() }
        val response = client.get("/api/status")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.contentType()!!.match(ContentType.Application.Json))
        assertTrue(response.bodyAsText().contains("\"status\":\"ok\""))
    }

    @Test
    fun unknownPathIsNotFound() = testApplication {
        application { module() }
        val response = client.get("/missing")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("{\"error\":\"not found\"}", response.bodyAsText())
    }

    @Test
    fun wrongMethodIsRejected() = testApplication {
        application { module() }
        val response = client.post("/api/status")
        assertEquals(HttpStatusCode.MethodNotAllowed, response.status)
    }

    @Test
    fun requestLoggingSkipsHealthAndInContainerChecks() {
        assertTrue(isLogged("/", "10.244.0.12", onWodby = true))
        assertTrue(!isLogged("/", "127.0.0.1", onWodby = true))
        assertTrue(!isLogged("/", "0:0:0:0:0:0:0:1", onWodby = true))
        assertTrue(isLogged("/", "127.0.0.1", onWodby = false))
        assertTrue(!isLogged("/healthz", "10.244.0.12", onWodby = true))
        assertTrue(!isLogged("/healthz", "127.0.0.1", onWodby = false))
    }
}
