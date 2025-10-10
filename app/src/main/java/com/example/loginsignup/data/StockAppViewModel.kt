package com.example.loginsignup.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockAppViewModel(application: Application): AndroidViewModel(application) {

    private val readAllData: LiveData<List<User>>
    private val repository: StockAppRepository

    init {
        val userDao = StockAppDatabase.getDatabase(application).userDao()
        val watchListDao = StockAppDatabase.getDatabase(application).watchListDao()
        repository = StockAppRepository(userDao, watchListDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
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