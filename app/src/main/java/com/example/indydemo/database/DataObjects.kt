package com.example.indydemo.database

object ServiceProvider {
    const val serviceProvider = "Indy Demo Corp."
    const val purpose = "Creating an Identity-Certificate on Hyperledger Indy"
    const val serviceProviderInfo = "Hyperledger Indy"
    const val validity = "20.01.2020 - 31.12.2021"
    const val issuer = "Demo Issuer"
}

object IdentityCredential {
    const val document = "Passport"
    const val issuer = "Government"

    const val attribute1_name = "Family name"
    const val attribute1_value = "Garcia"

    const val attribute2_name = "Given name"
    const val attribute2_value = "Alice"

    const val attribute3_name = "Date of birth"
    const val attribute3_value = "1981-08-12"

    const val attribute4_name = "Address"
    const val attribute4_value = "Sample street 12, 12059 Berlin"

    const val attribute5_name = "Nationality"
    const val attribute5_value = "German"
}


object DegreeCredential {
    const val document = "Bachelor-Certificate"
    const val issuer = "University"

    const val attribute1_name = "Specialization"
    const val attribute1_value = "Computer science"

    const val attribute2_name = "Degree"
    const val attribute2_value = "Bachelor of Science"

    const val attribute3_name = "Status"
    const val attribute3_value = "Graduated"

    const val attribute4_name = "Year"
    const val attribute4_value = "2020"

    const val attribute5_name = "Average"
    const val attribute5_value = "1.7"
}


object JobCredential {
    const val document = "Job-Certificate"
    const val issuer = "Company"

    const val attribute1_name = "Given name"
    const val attribute1_value = "Alice"

    const val attribute2_name = "Family name"
    const val attribute2_value = "Garcia"

    const val attribute3_name = "Employee status"
    const val attribute3_value = "Permanent"

    const val attribute4_name = "Salary"
    const val attribute4_value = "2400"

    const val attribute5_name = "Experience"
    const val attribute5_value = "10"
}