package com.mybusiness.api

import com.mybusiness.model.Contact
import com.mybusiness.model.toModel

class ContactService(private val contactApi: ContactApi) {
    suspend fun getContactList(): List<Contact> {
        return contactApi.getAllContacts().map { it.toModel() }
    }
    suspend fun getContactById(contactId: String): Contact {
        return contactApi.getContactById(contactId).toModel()
    }
    suspend fun createContract(contact: Contact) {
        contactApi.putContact(contact.toEntity())
    }
    suspend fun updateContract(contact: Contact): Boolean {
        return contactApi.postContact(contact.toEntity())
    }
}