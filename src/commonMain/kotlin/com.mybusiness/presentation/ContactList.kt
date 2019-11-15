package com.mybusiness.presentation

import com.mybusiness.ApplicationDispatcher
import com.mybusiness.UIDispatcher
import com.mybusiness.model.Contact
import com.mybusiness.api.ContactService
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ContactList {
    interface Presenter {
        fun getContactList()
    }

    interface View {
        fun displayContactList(contactList: List<Contact>)
    }
}

class ContactListPresenterImpl(
    private val contactService: ContactService,
    coroutineContext: CoroutineContext = ApplicationDispatcher
) : ContactList.Presenter, BasePresenter<ContactList.View>(coroutineContext) {
    override fun getContactList() {
        scope.launch {
            val contactList = contactService.getContactList()
            launch(UIDispatcher) {
                view?.displayContactList(contactList)
            }
        }


    }
}