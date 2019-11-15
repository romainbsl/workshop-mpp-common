package com.mybusiness.presentation

import com.mybusiness.ApplicationDispatcher
import com.mybusiness.UIDispatcher
import com.mybusiness.model.Contact
import com.mybusiness.api.ContactService
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ContactCreationUpdate {
    interface Presenter {
        fun updateOrCreateContact(contact: Contact)
    }
    interface View {
        fun updateOrCreationSucceed()
        fun updateOrCreationFails()
    }
}

class ContactCreationUpdatePresenterImpl(
    private val contactService: ContactService,
    coroutineContext: CoroutineContext = ApplicationDispatcher
) : ContactCreationUpdate.Presenter, BasePresenter<ContactCreationUpdate.View>(coroutineContext) {
    override fun updateOrCreateContact(contact: Contact) {
        scope.launch {
            try {
                if (contact.id == "-1") contactService.createContract(contact)
                else contactService.updateContract(contact)

                launch(UIDispatcher) { view?.updateOrCreationSucceed() }
            } catch (e: Exception) {
                launch(UIDispatcher) { view?.updateOrCreationFails() }
            }
        }
    }
}