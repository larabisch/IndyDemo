package com.example.indydemo.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.indydemo.R
import com.example.indydemo.database.Credential

@BindingAdapter("documentFormatted")
fun TextView.setDocumentFormatted(item: Credential?) {
    item?.let {
        text = item.document
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("issuerFormatted")
fun TextView.setIssuerFormatted(item: Credential?) {
    item?.let {
        text = "Issuer: ${item.issuer}"
    }
}

@BindingAdapter("documentImage")
fun ImageView.setDocumentImage(item: Credential?) {
    setImageResource(when (item?.document) {
        "Passport" -> R.drawable.ic_baseline_account_balance_24
        else -> R.drawable.ic_baseline_account_balance_24
    })
}