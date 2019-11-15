package com.mybusiness.model

import com.mybusiness.api.AddressEntity
import com.mybusiness.api.ContactEntity
import com.mybusiness.api.NameEntity
import com.mybusiness.api.PhoneEntity

data class Contact(
    val id: String,
    val name: Name,
    val addresses: List<Address>,
    val phones: List<Phone>
) {
    val fullName = "${name.lastName} ${name.firstName}"
}

fun ContactEntity.toModel() = Contact(id, name.toModel(), addresses.map { it.toModel() }, phones.map { it.toModel() })

data class Name(
    val firstName: String,
    val lastName: String
)

fun NameEntity.toModel() = Name(firstName, lastName)

data class Address(
    val type: Type,
    val street: String,
    val postalCode: String,
    val city: String,
    val country: String
) {
    enum class Type { HOME, WORK, OTHER }
}

fun AddressEntity.toModel() = Address(type, street, postalCode, city, country)

data class Phone(
    val type: Type,
    val number: String
) {
    enum class Type { HOME, WORK, MOBILE, OTHER }
}

fun PhoneEntity.toModel() = Phone(type, number)