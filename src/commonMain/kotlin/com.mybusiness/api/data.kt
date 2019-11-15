package com.mybusiness.api

import com.mybusiness.model.*
import com.mybusiness.model.toModel
import kotlinx.serialization.Serializable

@Serializable
data class ContactEntity(
    val id: String = "-1",
    val name: NameEntity,
    val addresses: List<AddressEntity> = mutableListOf(),
    val phones: List<PhoneEntity> = mutableListOf()
) {
    val fullName: String
        get() = "${name.lastName} ${name.firstName}"
}

fun Contact.toEntity() = ContactEntity(id, name.toEntity(), addresses.map { it.toEntity() }, phones.map { it.toEntity() })

@Serializable
data class NameEntity(
    val firstName: String,
    val lastName: String
)

fun Name.toEntity() = NameEntity(firstName, lastName)

@Serializable
data class AddressEntity(
    val type: Address.Type,
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String
)

fun Address.toEntity() = AddressEntity(type, street, postalCode, city, country)

@Serializable
data class PhoneEntity(
    val type: Phone.Type,
    val number: String
)

fun Phone.toEntity() = PhoneEntity(type, number)