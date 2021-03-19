package com.example.indydemo

import com.example.indydemo.database.Credential
import com.example.indydemo.database.CredentialDao
import com.example.indydemo.database.CredentialDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException



@RunWith(AndroidJUnit4::class)
class CredentialDatabaseTest {

    lateinit var credentialDao: CredentialDao
    lateinit var db: CredentialDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, CredentialDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        credentialDao = db.credentialDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetCredential() = runBlocking {
        // Arrange
        val document = "Passport"
        val issuer = "Department of State"
        val attributes = 5
        val credential = Credential(null, document, issuer, attributes)
        // Act
        credentialDao.insert(credential)
        val databaseCredential = credentialDao.getCredential()
        // Assert
        assertEquals(databaseCredential?.document, document)
        assertEquals(databaseCredential?.issuer, issuer)
        assertEquals(databaseCredential?.attributes, attributes)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGet() = runBlocking {
        // Arrange
        val document = "Passport"
        val issuer = "Department of State"
        val attributes = 5
        val credential = Credential(null, document, issuer, attributes)
        // Act
        credentialDao.insert(credential)
        val databaseCredential = credentialDao.get(1)
        // Assert
        assertEquals(databaseCredential?.credentialId, 1)
        assertEquals(databaseCredential?.document, document)
        assertEquals(databaseCredential?.issuer, issuer)
        assertEquals(databaseCredential?.attributes, attributes)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDelete() = runBlocking {
        // Arrange
        val document = "Passport"
        val issuer = "Department of State"
        val attributes = 5
        val credential = Credential(null, document, issuer, attributes)
        // Act
        credentialDao.insert(credential)
        credentialDao.deleteCredential(1)
        val allCredential = credentialDao.getAllCredentials()
        // Assert

    }

}