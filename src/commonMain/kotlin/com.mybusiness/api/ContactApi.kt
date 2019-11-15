package com.mybusiness.api

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import kotlinx.serialization.map

const val LOCALHOST = "127.0.0.1"
expect fun apiBaseUrl(): String

/**
 * Contact API Service
 */
class ContactApi {
    // MPP HttpClient
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    suspend fun getAllContacts(): List<ContactEntity> {
        return Json.parse(ContactEntity.serializer().list,
            client.get {
                apiUrl()
            }
        )
    }

    suspend fun getContactById(contactId: String): ContactEntity {
        return Json.parse(ContactEntity.serializer(),
            client.get {
                apiUrl("/$contactId")
            }
        )
    }

    suspend fun putContact(contactEntity: ContactEntity): String {
        return Json.parse(
            (StringSerializer to StringSerializer).map,
            client.put {
                apiUrl()
                method = HttpMethod.Put
                body = TextContent(Json.stringify(ContactEntity.serializer(), contactEntity),
                    contentType = ContentType.Application.Json)
            }
        ).values.first()
    }

    suspend fun postContact(contactEntity: ContactEntity): Boolean {
        val response = client.call {
            apiUrl("/${contactEntity.id}")
            method = HttpMethod.Post
            body = TextContent(Json.stringify(ContactEntity.serializer(), contactEntity),
                contentType = ContentType.Application.Json)
        }.response

        return response.status == HttpStatusCode.OK
    }

    private fun HttpRequestBuilder.apiUrl(path: String = "/") {
        url {
            host = apiBaseUrl()
            port = 8042
            protocol = URLProtocol.HTTP
            encodedPath = "/api/contacts$path"
        }
    }
}