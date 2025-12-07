package com.example.loginsignup.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.loginsignup.data.db.entity.User

// This interface defines the Data Access Object (DAO) for the User entity.
@Dao
interface UserDao {

    // This function inserts a user into the database, ignoring any conflicts.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    // This function retrieves all users from the database, ordered by ID.
    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    // This function retrieves a user from the database by email and password.
    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password Limit 1")
    fun getUserByEmailAndPassword(email: String, password: String): User?

    // This function retrieves a user from the database by email.
    @Query("""
        SELECT * FROM user_table 
        WHERE TRIM(LOWER(email)) = TRIM(LOWER(:email)) 
        LIMIT 1
    """)
    suspend fun getUserByEmail(email: String): User?

    // This function checks if an email exists in the database.
    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM user_table 
            WHERE TRIM(LOWER(email)) = TRIM(LOWER(:email)) 
            LIMIT 1
        )
    """)
    suspend fun emailExists(email: String): Boolean

    // This function resets the password for a user.
    @Query("""
        UPDATE user_table
        SET password = :newPassword
        WHERE email = :email
    """)
    suspend fun resetPassword(email: String, newPassword: String): Int
}

