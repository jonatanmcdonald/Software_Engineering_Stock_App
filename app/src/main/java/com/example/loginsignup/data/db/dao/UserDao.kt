package com.example.loginsignup.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.loginsignup.data.db.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password Limit 1")
    fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("""
        SELECT * FROM user_table 
        WHERE TRIM(LOWER(email)) = TRIM(LOWER(:email)) 
        LIMIT 1
    """)
    suspend fun getUserByEmail(email: String): User?

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM user_table 
            WHERE TRIM(LOWER(email)) = TRIM(LOWER(:email)) 
            LIMIT 1
        )
    """)
    suspend fun emailExists(email: String): Boolean

    @Query("""
        UPDATE user_table
        SET password = :newPassword
        WHERE email = :email
    """)
    suspend fun resetPassword(email: String, newPassword: String): Int
}

