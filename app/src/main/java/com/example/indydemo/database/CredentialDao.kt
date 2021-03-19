package com.example.indydemo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CredentialDao {

    @Insert
    suspend fun insert(night: Credential)

    @Update
    suspend fun update(night: Credential)

    @Query("SELECT * from credential_table WHERE credentialId = :key")
    suspend fun get(key: Int): Credential?

    @Query("DELETE FROM credential_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM credential_table ORDER BY credentialId DESC")
    fun getAllCredentials(): LiveData<List<Credential>>

    @Query("SELECT * FROM credential_table ORDER BY credentialId DESC LIMIT 1")
    suspend fun getCredential(): Credential?

    @Query("SELECT * from credential_table WHERE credentialId = :key")
    fun getCredentialWithId(key: Int): LiveData<Credential>
}