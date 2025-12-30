package com.financialos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.financialos.data.auth.GoogleAuthClient
import com.financialos.data.repository.FinancialRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: FinancialRepository,
    val googleAuthClient: GoogleAuthClient
) : ViewModel() {

    private val _user = MutableStateFlow<GoogleSignInAccount?>(null)
    val user: StateFlow<GoogleSignInAccount?> = _user.asStateFlow()

    init {
        _user.value = googleAuthClient.getSignedInAccount()
        _user.value?.let { 
             repository.initializeServices(it.account?.name ?: "")
             // Trigger sync if we have a spreadsheet ID saved, 
             // for now we might need to ask the user for it or find it.
             // We'll skip auto-sync for now.
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            _user.value = account
            account?.let {
                repository.initializeServices(it.account?.name ?: "")
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }
    
    fun signOut() {
        googleAuthClient.signOut {
             _user.value = null
        }
    }
}

class MainViewModelFactory(
    private val application: FinancialApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application.repository, application.googleAuthClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
