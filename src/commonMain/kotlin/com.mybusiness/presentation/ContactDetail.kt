package com.mybusiness.presentation

import com.mybusiness.ApplicationDispatcher
import com.mybusiness.UIDispatcher
import com.mybusiness.model.Contact
import com.mybusiness.api.ContactService
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ContactDetail {
    interface Presenter {
        fun getContract(contactId: String)
    }
    interface View {
        fun displayContact(contact: Contact)
    }
}

class ContactDetailPresenterImpl(
    private val contactService: ContactService,
    coroutineContext: CoroutineContext = ApplicationDispatcher
) : ContactDetail.Presenter, BasePresenter<ContactDetail.View>(coroutineContext) {
    override fun getContract(contactId: String) {
        scope.launch {
            val contact = contactService.getContactById(contactId)
            launch(UIDispatcher) {
                view?.displayContact(contact)
            }
        }
    }
}