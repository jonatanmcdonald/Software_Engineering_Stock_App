// Package declaration for the view models
package com.example.loginsignup.viewModels

// Import statements for necessary classes and libraries
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

/**
 * Hashes a password using the SHA-256 algorithm.
 *
 * @param password The password to hash.
 * @return The hashed password.
 */
fun hashPassword(password: String): String {
    // Get an instance of the SHA-256 message digest.
    val md = MessageDigest.getInstance("SHA-256")
    // Digest the password and return the a hex string.
    val digest = md.digest(password.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }

}

// This class is the ViewModel for the User.
class UserViewModel(application: Application): AndroidViewModel(application) {

    // LiveData to hold all user data.
    private val readAllData: LiveData<List<User>>
    // The repository for accessing data.
    private val repository: StockAppRepository

    // A private mutable live data to hold the login result.
    private val _loginResult = MutableLiveData<Boolean>()
    // An immutable live data to expose the login result to the UI.
    val loginResult: LiveData<Boolean> = _loginResult

    init { // The initializer block for the ViewModel.
        // Get the DAOs from the database.
        val userDao = StockAppDatabase.getDatabase(application).userDao()
        val watchListDao = StockAppDatabase.getDatabase(application).watchListDao()
        val stockDao = StockAppDatabase.getDatabase(application).stockDao()
        val transactionDao = StockAppDatabase.getDatabase(application).transactionDao()
        val portfolioDao = StockAppDatabase.getDatabase(application).portfolioDao()
        val alertDao = StockAppDatabase.getDatabase(application).alertDao()
        val noteDao = StockAppDatabase.getDatabase(application).noteDao()


        // Initialize the repository with the DAOs.
        repository = StockAppRepository(userDao, watchListDao, stockDao, transactionDao, portfolioDao, alertDao, noteDao)
        // Read all user data from the repository.
        readAllData = repository.readAllData
    }

    /**
     * Logs in a user.
     *
     * @param email The user's email.
     * @param password The user's password.
     */
    fun login(email: String, password: String) {
        // Launch a coroutine in the IO dispatcher.
        viewModelScope.launch(Dispatchers.IO) {
            // Hash the password.
            val hashedPassword = hashPassword(password)
            // Get the user from the repository.
            val user = repository.getUserByEmailAndPassword(email, hashedPassword)

            // If the user exists, start a session and post true to the login result.
            if (user != null) {
                SessionManager.startSession(user.id)

                _loginResult.postValue(true)
            } else {
                // Otherwise, post false to the login result.
                _loginResult.postValue(false)
            }

        }
    }

    /**
     * Signs up a new user.
     *
     * @param user The user to sign up.
     * @param onResult A callback to be invoked with the result of the sign-up operation.
     */
    fun signUpUser(user: User, onResult: (Boolean) -> Unit) {
        // Launch a coroutine in the IO dispatcher.
        viewModelScope.launch(Dispatchers.IO) {
            // Check if the email is already taken.
            val emailTaken = repository.isEmailTaken(user.email)
            // If the email is not taken, hash the password and add the user to the repository.
            if (!emailTaken) {
                val hashedUser = user.copy(password = hashPassword(user.password))
                repository.addUser(hashedUser)
            }
            // Switch to the main dispatcher to invoke the onResult callback.
            withContext(Dispatchers.Main) {
                onResult(!emailTaken)
            }
        }
    }

    // This function checks if an email exists in the database.
    suspend fun checkEmailExists(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            repository.emailExists(email)
        }
    }

    // This function verifies the security answer for a user.
    suspend fun verifySecurityAnswer(email: String, answer: String): Boolean {
        return withContext(Dispatchers.IO) {
            // however you store it:
            val user = repository.getUserByEmail(email)
            user?.securityAnswer.equals(answer, ignoreCase = true)  // or normalized compare
        }
    }

    // This function resets the password for a user.
    suspend fun resetPassword(email: String, password: String): Boolean
    {
        val hashedPassword = hashPassword(password)
        return withContext(Dispatchers.IO) {
            repository.resetPassword(email, hashedPassword)
        }
    }

}