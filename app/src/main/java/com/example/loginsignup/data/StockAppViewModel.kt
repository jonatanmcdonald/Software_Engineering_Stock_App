package com.example.loginsignup.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(password.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }

}

class StockAppViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: StockAppRepository

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    init {
        val userDao = StockAppDatabase.getDatabase(application).userDao()
        val watchListDao = StockAppDatabase.getDatabase(application).watchListDao()
        repository = StockAppRepository(userDao, watchListDao)
        readAllData = repository.readAllData
    }

    //Not really used anymore
    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByEmailAndPassword(email, password)
            _loginResult.postValue(user != null)
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




    // Make mutating ops suspend so callers can await results
    suspend fun upsertByUserAndStock(item: WatchList): UpsertResult {
        // Try UPDATE first (idempotent if exists)
        val updated = repository.updateWatchListItem(item)
        if (updated > 0) return UpsertResult.Updated

        // Not found → INSERT (ignore in case of race)
        val rowId = repository.addWatchListItem(item)
        return if (rowId != -1L) UpsertResult.Inserted else UpsertResult.AlreadyExists
    }


    suspend fun existsForUser(userId: String, stock: String): Boolean
    {
        return repository.existsForUser(userId, stock)
    }

    suspend fun updateWatchList( item: WatchList): Int {
        return repository.updateWatchListItem(item)
    }

    suspend fun deleteWatchListItem(userId: String, itemId: String): Int {
       return repository.deleteWatchListItem(userId, itemId)
    }

    fun getAllForUser(id: String): LiveData<List<WatchList>>
    {
            return repository.getAllForUser(id)
    }

    fun getWatchListItem(id: String, itemId: String) : LiveData<WatchList?>
    {
            return repository.getWatchListItem(id, itemId)
    }

    enum class UpsertResult { Inserted, Updated, AlreadyExists }

}