package com.example.loginsignup.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loginsignup.data.db.StockAppDatabase
import com.example.loginsignup.data.db.StockAppRepository
import com.example.loginsignup.data.db.entity.User
import com.example.loginsignup.data.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(password.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }

}

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: StockAppRepository

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    init {
        val userDao = StockAppDatabase.getDatabase(application).userDao()
        val watchListDao = StockAppDatabase.getDatabase(application).watchListDao()
        val stockDao = StockAppDatabase.getDatabase(application).stockDao()
        val transactionDao = StockAppDatabase.getDatabase(application).transactionDao()
        val portfolioDao = StockAppDatabase.getDatabase(application).portfolioDao()

        repository = StockAppRepository(userDao, watchListDao, stockDao, transactionDao, portfolioDao)
        readAllData = repository.readAllData
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val hashedPassword = hashPassword(password)
            val user = repository.getUserByEmailAndPassword(email, hashedPassword)

            if (user != null) {
                SessionManager.startSession(user.id)

                _loginResult.postValue(true)
            } else {
                _loginResult.postValue(false)
            }

        }
    }

    fun signUpUser(user: User, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val emailTaken = repository.isEmailTaken(user.email)
            if (!emailTaken) {
                val hashedUser = user.copy(password = hashPassword(user.password))
                repository.addUser(hashedUser)
            }
            withContext(Dispatchers.Main) {
                onResult(!emailTaken)
            }
        }
    }


}