package com.example.indydemo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credential_table")
data class Credential(

    @PrimaryKey(autoGenerate = true)
    val credentialId: Int?,

    @ColumnInfo(name = "document")
    val document: String,

    @ColumnInfo(name = "issuer")
    val issuer: String,

    @ColumnInfo(name = "attributes")
    val attributes: Int

)