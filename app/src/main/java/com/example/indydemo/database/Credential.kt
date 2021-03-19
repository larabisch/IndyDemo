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

        @ColumnInfo(name = "attribute1_name")
        val attribute1_name: String,

        @ColumnInfo(name = "attribute1_value")
        val attribute1_value: String,

        @ColumnInfo(name = "attribute2_name")
        val attribute2_name: String,

        @ColumnInfo(name = "attribute2_value")
        val attribute2_value: String,

        @ColumnInfo(name = "attribute3_name")
        val attribute3_name: String,

        @ColumnInfo(name = "attribute3_value")
        val attribute3_value: String,

        @ColumnInfo(name = "attribute4_name")
        val attribute4_name: String,

        @ColumnInfo(name = "attribute4_value")
        val attribute4_value: String,

        @ColumnInfo(name = "attribute5_name")
        val attribute5_name: String,

        @ColumnInfo(name = "attribute5_value")
        val attribute5_value: String,

        )