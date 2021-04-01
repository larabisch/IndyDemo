package com.example.indydemo.indy

import android.system.ErrnoException
import android.system.Os
import android.util.Log
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.wallet.Wallet
import org.hyperledger.indy.sdk.wallet.WalletNotFoundException
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ExecutionException

class Indy {

    private val TAG = "InitializeDemoLogs"

    private val universityWalletConfig = JSONObject()
            .put("id", "universityWallet").toString()
    private val universityWalletCredentials = JSONObject()
            .put("key", "university_wallet_key").toString()

    private val companyWalletConfig = JSONObject()
            .put("id", "companyWallet").toString()
    private val companyWalletCredentials = JSONObject()
            .put("key", "company_wallet_key").toString()

    private val governmentWalletConfig = JSONObject()
            .put("id", "governmentWallet").toString()
    private val governmentWalletCredentials = JSONObject()
            .put("key", "government_wallet_key").toString()

    private val aliceWalletConfig = JSONObject()
            .put("id", "aliceWallet").toString()
    private val aliceWalletCredentials = JSONObject()
            .put("key", "alice_wallet_key").toString()



    private var universityDid = "V4SGRU86Z58d6TV7PBUe6f"
    private val companyDid = "Th7MpTaRZVRYnPiabds81Y"
    private var governmentDid = "NcYxiDXkpYi6ov5FcYDi1e"
    private var aliceDid = "Qx2KARvFsPcWZJaqF3FbHu"
    private lateinit var aliceMasterSecretId : String


    private lateinit var identityCredOffer : String
    private lateinit var degreeCredOffer : String
    private lateinit var jobCredOffer : String

    private lateinit var jobApplicationProofRequestJson : String
    private lateinit var degreeRequestProofRequestJson : String

    private lateinit var identityCertificateSchemaId : String
    private lateinit var identityCertificateSchemaJson : String
    private lateinit var identityCertificateCredDefId : String
    private lateinit var identityCertificateCredDefJson : String

    private lateinit var degreeCertificateSchemaId : String
    private lateinit var degreeCertificateSchemaJson : String
    private lateinit var degreeCertificateCredDefId : String
    private lateinit var degreeCertificateCredDefJson : String

    private lateinit var jobCertificateSchemaId : String
    private lateinit var jobCertificateSchemaJson : String
    private lateinit var jobCertificateCredDefId : String
    private lateinit var jobCertificateCredDefJson : String


    fun loadLibrary(environmentPath: String) {
        try {
            Os.setenv("EXTERNAL_STORAGE", environmentPath, true)
            System.loadLibrary("indy")
            Log.d(TAG, "Indy: LOAD LIBRARY DONE")
        } catch (e: ErrnoException) {
            e.printStackTrace()
        }
    }


    fun initializeWallets() {

        // Create Government Wallet
        try {
            Wallet.deleteWallet(governmentWalletConfig, governmentWalletCredentials).get()
        } catch (e: ExecutionException) {
            Log.d(TAG, e.toString())
        } catch (e: WalletNotFoundException) {
            Log.d(TAG, e.toString())
        }
        Wallet.createWallet(governmentWalletConfig, governmentWalletCredentials).get()
        Log.d(TAG, "Indy: Government Wallet was created")


        // Create University Wallet
        try {
            Wallet.deleteWallet(universityWalletConfig, universityWalletCredentials).get()
        } catch (e: ExecutionException) {
            Log.d(TAG, e.toString())
        } catch (e: WalletNotFoundException) {
            Log.d(TAG, e.toString())
        }
        Wallet.createWallet(universityWalletConfig, universityWalletCredentials).get()
        Log.d(TAG, "Indy: University Wallet was created")


        // Create Company Wallet
        try {
            Wallet.deleteWallet(companyWalletConfig, companyWalletCredentials).get()
        } catch (e: ExecutionException) {
            Log.d(TAG, e.toString())
        } catch (e: WalletNotFoundException) {
            Log.d(TAG, e.toString())
        }
        Wallet.createWallet(companyWalletConfig, companyWalletCredentials).get()
        Log.d(TAG, "Indy: Company Wallet was created")


        // Create Alice Wallet
        try {
            Wallet.deleteWallet(aliceWalletConfig, aliceWalletCredentials).get()
        } catch (e: ExecutionException) {
            Log.d(TAG, e.toString())
        } catch (e: WalletNotFoundException) {
            Log.d(TAG, e.toString())
        }
        Wallet.createWallet(aliceWalletConfig, aliceWalletCredentials).get()
        Log.d(TAG, "Indy: Alice Wallet was created")

        // Open Alice Wallet and generate Master Secret
        val aliceWallet = Wallet.openWallet(aliceWalletConfig, aliceWalletCredentials).get()
        aliceMasterSecretId = Anoncreds.proverCreateMasterSecret(aliceWallet, null).get()
        Log.d(TAG, "Indy: Alice Master Secret was created")
        aliceWallet.closeWallet()

        Log.d(TAG, "Indy: WALLET INITIALISATION DONE")
    }



    fun createIdentityCredOffer() {

        Log.d(TAG, "Indy: CREATE IDENTITY CREDENTIAL OFFER STARTED")

        // Open Government Wallet
        val governmentWallet = Wallet.openWallet(governmentWalletConfig, governmentWalletCredentials).get()
        Log.d(TAG, "Indy: Government Wallet opened")


        // Government Creates Credential Schema
        val identityCertificateSchemaName = "Identity-Certificate"
        val identityCertificateSchemaVersion = "1.0"
        val identityCertificateSchemaAttributes = JSONArray()
            .put("family_name")
            .put("given_names")
            .put("date_of_birth")
            .put("address")
            .put("nationality")
            .put("document_type")
            .toString()

        val identityCertificateSchemaResult = Anoncreds.issuerCreateSchema(
            governmentDid,
            identityCertificateSchemaName,
            identityCertificateSchemaVersion,
            identityCertificateSchemaAttributes
        ).get()

        identityCertificateSchemaId = identityCertificateSchemaResult.schemaId
        identityCertificateSchemaJson = identityCertificateSchemaResult.schemaJson

        Log.d(TAG, "Indy: Government created Credential Schema for Identity-Certificate")


        // Government creates Credential Definition
        val identityCertificateCredDefTag = "Tag1"
        val identityCertificateCredDefConfigJson = JSONObject()
                .put("support_revocation", false).toString()

        val identityCertificateCreateCredDefResult = Anoncreds.issuerCreateAndStoreCredentialDef(
            governmentWallet,
            governmentDid,
            identityCertificateSchemaJson,
            identityCertificateCredDefTag,
            null,
            identityCertificateCredDefConfigJson
        ).get()

        identityCertificateCredDefId = identityCertificateCreateCredDefResult.credDefId
        identityCertificateCredDefJson = identityCertificateCreateCredDefResult.credDefJson

        Log.d(TAG, "Indy: Government created Credential Definition for Identity-Certificate")


        // Government creates Credential Offer
        identityCredOffer = Anoncreds.issuerCreateCredentialOffer(
            governmentWallet,
            identityCertificateCredDefId
        ).get()

        Log.d(TAG, "Indy: Government created Credential Offer for Identity-Certificate")


        // Close Government wallet
        governmentWallet.closeWallet().get()
        Log.d(TAG, "Indy: Government Wallet was closed")

        Log.d(TAG, "Indy: CREATE IDENTITY CREDENTIAL OFFER DONE")

    }



    fun createDegreeCredOffer() {

        Log.d(TAG, "Indy: CREATE DEGREE CREDENTIAL OFFER STARTED")

        // Open University Wallet
        val universityWallet = Wallet.openWallet(universityWalletConfig, universityWalletCredentials).get()
        Log.d(TAG, "Indy: University Wallet was opened")


        // University Creates Credential Schema
        val bachelorCertificateSchemaName = "Degree-Certificate"
        val bachelorCertificateSchemaVersion = "1.0"
        val bachelorCertificateSchemaAttributes = JSONArray()
            .put("given_names")
            .put("family_name")
            .put("specialization")
            .put("degree")
            .put("status")
            .put("year")
            .put("average")
            .toString()

        val bachelorCertificateSchemaResult = Anoncreds.issuerCreateSchema(
            universityDid,
            bachelorCertificateSchemaName,
            bachelorCertificateSchemaVersion,
            bachelorCertificateSchemaAttributes
        ).get()

        degreeCertificateSchemaId = bachelorCertificateSchemaResult.schemaId
        degreeCertificateSchemaJson = bachelorCertificateSchemaResult.schemaJson

        Log.d(TAG, "Indy: University created Credential Schema for Degree-Certificate")


        // University create Credential Definition
        val bachelorCertificateCredDefTag = "Tag1"
        val bachelorCertificateCredDefConfigJson = JSONObject()
                .put("support_revocation", false).toString()

        val bachelorCertificateCreateCredDefResult = Anoncreds.issuerCreateAndStoreCredentialDef(
            universityWallet,
            universityDid,
            degreeCertificateSchemaJson,
            bachelorCertificateCredDefTag,
            null,
            bachelorCertificateCredDefConfigJson
        ).get()

        degreeCertificateCredDefId = bachelorCertificateCreateCredDefResult.credDefId
        degreeCertificateCredDefJson = bachelorCertificateCreateCredDefResult.credDefJson

        Log.d(TAG, "Indy: University created Credential Definition for Degree-Certificate")


        // University Creates Credential Offer
        degreeCredOffer = Anoncreds.issuerCreateCredentialOffer(
            universityWallet,
            degreeCertificateCredDefId
        ).get()

        Log.d(TAG, "Indy: University created Credential Offer for Degree-Certificate")


        // Close Government wallet
        universityWallet.closeWallet().get()
        Log.d(TAG, "Indy: University Wallet was closed")

        Log.d(TAG, "Indy: CREATE DEGREE CREDENTIAL OFFER DONE")

    }



    fun createJobCredOffer() {

        Log.d(TAG, "Indy: CREATE JOB CREDENTIAL OFFER STARTED")

        // Open Company Wallet
        val companyWallet = Wallet.openWallet(companyWalletConfig, companyWalletCredentials).get()
        Log.d(TAG, "Indy: Company Wallet was opened")


        // Company Creates Credential Schema
        val jobCertificateSchemaName = "Job-Certificate"
        val jobCertificateSchemaVersion = "1.0"
        val jobCertificateSchemaAttributes = JSONArray()
            .put("given_names")
            .put("family_name")
            .put("employee_status")
            .put("salary")
            .put("experience")
            .toString()

        val jobCertificateSchemaResult = Anoncreds.issuerCreateSchema(
            companyDid,
            jobCertificateSchemaName,
            jobCertificateSchemaVersion,
            jobCertificateSchemaAttributes
        ).get()

        jobCertificateSchemaId = jobCertificateSchemaResult.schemaId
        jobCertificateSchemaJson = jobCertificateSchemaResult.schemaJson

        Log.d(TAG, "Indy: Company created Credential Schema for Job-Certificate")


        // Company create Credential Definition
        val jobCertificateCredDefTag = "Tag1"
        val jobCertificateCredDefConfigJson = JSONObject()
                .put("support_revocation", false).toString()

        val jobCertificateCreateCredDefResult = Anoncreds.issuerCreateAndStoreCredentialDef(
            companyWallet,
            companyDid,
            jobCertificateSchemaJson,
            jobCertificateCredDefTag,
            null,
            jobCertificateCredDefConfigJson
        ).get()

        jobCertificateCredDefId = jobCertificateCreateCredDefResult.credDefId
        jobCertificateCredDefJson = jobCertificateCreateCredDefResult.credDefJson

        Log.d(TAG, "Indy: Company created Credential Definition for Job-Certificate")


        // Company Creates Credential Offer
        jobCredOffer = Anoncreds.issuerCreateCredentialOffer(
                companyWallet,
                jobCertificateCredDefId
        ).get()

        Log.d(TAG, "Indy: Company created Credential Offer for Job-Certificate")

        // Close Company wallet
        companyWallet.closeWallet().get()
        Log.d(TAG, "Indy: Company Wallet was closed")


        Log.d(TAG, "Indy: CREATE JOB CREDENTIAL OFFER DONE")

    }


    fun createProofRequestDegreeCertificate() {

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST DEGREE-CERTIFICATE STARTED")

        // Get Credentials for Proof Request
        val degreeRequestNonce = Anoncreds.generateNonce().get()

        degreeRequestProofRequestJson = JSONObject()
                .put("nonce", degreeRequestNonce)
                .put("name", "Degree-Request")
                .put("version", "0.1")
                .put("requested_attributes", JSONObject()
                        .put("attr1_referent", JSONObject().put("name", "given_names"))
                        .put("attr2_referent", JSONObject().put("name", "family_name"))
                        .put("attr3_referent", JSONObject().put("name", "address"))
                        .put("attr4_referent", JSONObject().put("name", "nationality"))
                )
                .put("requested_predicates", JSONObject()
                        .put("predicate1_referent", JSONObject()
                                .put("name", "date_of_birth")
                                .put("p_type", "<=")
                                .put("p_value", 20030320)
                        )
                )
                .toString()

        Log.d(TAG, "Indy: JSON Creation for Proof-Request for Degree-Certificate")

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST DEGREE-CERTIFICATE DONE")

    }

    fun createProofRequestJobApplication() {

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST JOB-APPLICATION STARTED")

        // Get Credentials for Proof Request
        val jobApplicationNonce = Anoncreds.generateNonce().get()

        jobApplicationProofRequestJson = JSONObject()
            .put("nonce", jobApplicationNonce)
            .put("name", "Job-Application")
            .put("version", "0.1")
            .put("requested_attributes", JSONObject()
                    .put("attr1_referent", JSONObject().put("name", "given_names"))
                    .put("attr2_referent", JSONObject().put("name", "family_name"))
                    .put("attr3_referent", JSONObject().put("name", "specialization"))
                    .put("attr4_referent", JSONObject().put("name", "degree"))
                    .put("attr5_referent", JSONObject().put("name", "status"))
            )
            .put("requested_predicates", JSONObject()
                    .put("predicate1_referent", JSONObject()
                            .put("name", "average")
                            .put("p_type", "<=")
                            .put("p_value", 30)
                    )
            )
            .toString()

        Log.d(TAG, "Indy: JSON Creation for Proof-Request for Job-Application")

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST JOB-APPLICATION DONE")

    }



    fun requestIdentityCertificate() {

        Log.d(TAG, "Indy: REQUEST IDENTITY CERTIFICATE STARTED")

        // Open Government Wallet
        val governmentWallet = Wallet.openWallet(governmentWalletConfig, governmentWalletCredentials).get()
        Log.d(TAG, "Indy: Government Wallet opened")

        // Open Alice Wallet
        val aliceWallet = Wallet.openWallet(aliceWalletConfig, aliceWalletCredentials).get()
        Log.d(TAG, "Indy: Alice Wallet opened")


        // Create Credential Request
        val identityCreateCredReqResult = Anoncreds.proverCreateCredentialReq(
            aliceWallet,
            aliceDid,
            identityCredOffer,
            identityCertificateCredDefJson,
            aliceMasterSecretId
        ).get()
        val identityCredReqJson = identityCreateCredReqResult.credentialRequestJson
        val identityCredReqMetadataJson = identityCreateCredReqResult.credentialRequestMetadataJson
        Log.d(TAG, "Indy: Create Credential Request for Identity-Certificate")


        // Government creates Credential
        val identityCredValuesJson = JSONObject()
            .put("family_name", JSONObject()
                    .put("raw", "Garcia")
                    .put("encoded", "946510486021438157324488086511345775493242887916"))
            .put("given_names", JSONObject()
                    .put("raw", "Alice")
                    .put("encoded", "303680606261854555853193981910306662446219611633"))
            .put("date_of_birth", JSONObject()
                    .put("raw", "19810812")
                    .put("encoded", "19810812"))
            .put("address", JSONObject()
                    .put("raw", "Sample street 12, 12059 Berlin")
                    .put("encoded", "947397614802691795330029495488188401319414724640"))
            .put("nationality", JSONObject()
                    .put("raw", "German")
                    .put("encoded", "1247798522173559676754790998791794996059419723869"))
            .put("document_type", JSONObject()
                    .put("raw", "Identity card")
                    .put("encoded", "144992736518735056036859271803104200336197691206"))
            .toString()

        Log.d(TAG, "Indy: JSON for Credential of Identity-Certificate finished")


        val identityCertificateCreateCredentialResult = Anoncreds.issuerCreateCredential(
            governmentWallet,
            identityCredOffer,
            identityCredReqJson,
            identityCredValuesJson,
            null,
            -1
        ).get()

        val identityCredential = identityCertificateCreateCredentialResult.credentialJson

        Log.d(TAG, "Indy: Government created Credential for Identity-Certificate")


        // Store Credential
        Anoncreds.proverStoreCredential(
            aliceWallet,
            null,
            identityCredReqMetadataJson,
            identityCredential,
            identityCertificateCredDefJson,
            null
        ).get()

        Log.d(TAG, "Indy: Stored Credential")

        // Close Government wallet
        governmentWallet.closeWallet().get()
        Log.d(TAG, "Indy: Government Wallet was closed")

        // Close Alice wallet
        aliceWallet.closeWallet().get()
        Log.d(TAG, "Indy: Alice Wallet was closed")

        Log.d(TAG, "Indy: REQUEST IDENTITY CERTIFICATE DONE")

    }



    fun proofDegreeCertificate() {

        Log.d(TAG, "Indy: PROOF-REQUEST FOR DEGREE-CERTIFICATE STARTED")

        // Open Alice Wallet
        val aliceWallet = Wallet.openWallet(aliceWalletConfig, aliceWalletCredentials).get()
        Log.d(TAG, "Indy: Alice Wallet was opened")


            // Search for all requested attributes for the Proof
        val degreeRequestCredentialsSearch = CredentialsSearchForProofReq
                .open(aliceWallet, degreeRequestProofRequestJson, null).get()

        val degreeRequestCredentialsForAttribute1 = JSONArray(degreeRequestCredentialsSearch
                .fetchNextCredentials("attr1_referent", 100).get())
        val degreeRequestCredentialIdForAttribute1 = degreeRequestCredentialsForAttribute1
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Attribute 1: $degreeRequestCredentialsForAttribute1")

        val degreeRequestCredentialsForAttribute2 = JSONArray(degreeRequestCredentialsSearch
                .fetchNextCredentials("attr2_referent", 100).get())
        val degreeRequestCredentialIdForAttribute2 = degreeRequestCredentialsForAttribute2
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Attribute 2: $degreeRequestCredentialsForAttribute2")

        val degreeRequestCredentialsForAttribute3 = JSONArray(degreeRequestCredentialsSearch
                .fetchNextCredentials("attr3_referent", 100).get())
        val degreeRequestCredentialIdForAttribute3 = degreeRequestCredentialsForAttribute3
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Attribute 3: $degreeRequestCredentialsForAttribute3")

        val degreeRequestCredentialsForAttribute4 = JSONArray(degreeRequestCredentialsSearch
                .fetchNextCredentials("attr4_referent", 100).get())
        val degreeRequestCredentialIdForAttribute4 = degreeRequestCredentialsForAttribute4
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Attribute 4: $degreeRequestCredentialsForAttribute4")


        // Search for all requested predicates for the Proof
        val degreeRequestCredentialsForPredicate = JSONArray(degreeRequestCredentialsSearch
                .fetchNextCredentials("predicate1_referent", 100).get())
        val degreeRequestCredentialIdForPredicate = degreeRequestCredentialsForPredicate
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Predicate: $degreeRequestCredentialIdForPredicate")

        Log.d(TAG, "Indy: Searched for all attributes for Proof-Request for Degree-Certificate")


        degreeRequestCredentialsSearch.close()
        Log.d(TAG, "Indy: Searched for all requested Credentials of Proof-Request for Degree-Certificate")


        // Create Proof
        val degreeRequestedCredentialsJson = JSONObject()
                .put("self_attested_attributes", JSONObject())
                .put("requested_attributes", JSONObject()
                        .put("attr1_referent", JSONObject()
                                .put("cred_id", degreeRequestCredentialIdForAttribute1)
                                .put("revealed", true)
                        )
                        .put("attr2_referent", JSONObject()
                                .put("cred_id", degreeRequestCredentialIdForAttribute2)
                                .put("revealed", true)
                        )
                        .put("attr3_referent", JSONObject()
                                .put("cred_id", degreeRequestCredentialIdForAttribute3)
                                .put("revealed", true)
                        )
                        .put("attr4_referent", JSONObject()
                                .put("cred_id", degreeRequestCredentialIdForAttribute4)
                                .put("revealed", true)
                        )
                )
                .put("requested_predicates", JSONObject()
                        .put("predicate1_referent", JSONObject()
                                .put("cred_id", degreeRequestCredentialIdForPredicate)
                        )
                )
                .toString()

        Log.d(TAG, "Indy: JSON for Proof for Degree-Certificate finished")


        val degreeRequestSchemas: String = JSONObject()
                .put(identityCertificateSchemaId, JSONObject(identityCertificateSchemaJson)).toString()
        val degreeRequestCredentialDefs: String = JSONObject()
                .put(identityCertificateCredDefId, JSONObject(identityCertificateCredDefJson)).toString()
        val degreeRequestRevocStates = JSONObject().toString()

        var degreeRequestProofJson = ""
        try {
            degreeRequestProofJson = Anoncreds.proverCreateProof(
                aliceWallet,
                degreeRequestProofRequestJson,
                degreeRequestedCredentialsJson,
                aliceMasterSecretId,
                degreeRequestSchemas,
                degreeRequestCredentialDefs,
                degreeRequestRevocStates
            ).get()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        val degreeRequestProof = JSONObject(degreeRequestProofJson)

        Log.d(TAG, "Indy: Proof Creation of Proof-Request for Degree-Certificate done")


        // Verifier verifies Proof
        val revealedAttr1 = degreeRequestProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr1_referent")
        Log.d(TAG, "Indy: Revealed attribute 1: $revealedAttr1")
        Log.d(TAG, "Indy: Revealed attribute 1: " + revealedAttr1.getString("raw"))


        val revocRegDefs = JSONObject().toString()
        val revocRegs = JSONObject().toString()

        val valid = Anoncreds.verifierVerifyProof(
            degreeRequestProofRequestJson,
            degreeRequestProofJson,
            degreeRequestSchemas,
            degreeRequestCredentialDefs,
            revocRegDefs,
            revocRegs
        ).get()

        Log.d(TAG, "Indy: Validity of Proof: $valid")


        // Close Alice wallet
        aliceWallet.closeWallet().get()
        Log.d(TAG, "Indy: Alice Wallet was closed")

        Log.d(TAG, "Indy: PROOF-REQUEST FOR DEGREE-CERTIFICATE DONE")

    }



    fun requestDegreeCertificate() {

        Log.d(TAG, "Indy: CREDENTIAL FOR DEGREE-CERTIFICATE STARTED")

        // Open University Wallet
        val universityWallet = Wallet.openWallet(universityWalletConfig, universityWalletCredentials).get()
        Log.d(TAG, "Indy: University Wallet was opened")

        // Open Alice Wallet
        val aliceWallet = Wallet.openWallet(aliceWalletConfig, aliceWalletCredentials).get()
        Log.d(TAG, "Indy: Alice Wallet was opened")


        // Create Credential Request
        val bachelorCreateCredReqResult = Anoncreds.proverCreateCredentialReq(
            aliceWallet,
            aliceDid,
            degreeCredOffer,
            degreeCertificateCredDefJson,
            aliceMasterSecretId
        ).get()

        val bachelorCredReqJson = bachelorCreateCredReqResult.credentialRequestJson
        val bachelorCredReqMetadataJson = bachelorCreateCredReqResult.credentialRequestMetadataJson

        Log.d(TAG, "Indy: Created Credential Request for Degree-Certificate")


        // University create Credential
        val bachelorCredValuesJson = JSONObject()
            .put("given_names", JSONObject()
                    .put("raw", "Alice")
                    .put("encoded", "1139481716457488690172217916278103335"))
            .put("family_name", JSONObject()
                    .put("raw", "Garcia")
                    .put("encoded", "5321642780241790123587902456789123452"))
            .put("specialization", JSONObject()
                    .put("raw", "Computer science")
                    .put("encoded", "341257452171890758595834870776995707090391141060"))
            .put("degree", JSONObject()
                    .put("raw", "Bachelor of Science")
                    .put("encoded", "1199049847391873865418403176279550347658772583658"))
            .put("status", JSONObject()
                    .put("raw", "Graduated")
                    .put("encoded", "1064541674305188056974032337435623050260045088296"))
            .put("year", JSONObject()
                    .put("raw", "2020")
                    .put("encoded", "2020"))
            .put("average", JSONObject()
                    .put("raw", "17")
                    .put("encoded", "17"))
            .toString()

        Log.d(TAG, "Indy: JSON for Credential of Degree-Certificate finished")


        val bachelorCertificateCreateCredentialResult = Anoncreds.issuerCreateCredential(
            universityWallet,
            degreeCredOffer,
            bachelorCredReqJson,
            bachelorCredValuesJson,
            null,
            -1
        ).get()

        val bachelorCredential = bachelorCertificateCreateCredentialResult.credentialJson

        Log.d(TAG, "Indy: University created Credential for Degree-Certificate")


        // Store Credential
        Anoncreds.proverStoreCredential(
            aliceWallet,
            null,
            bachelorCredReqMetadataJson,
            bachelorCredential,
            degreeCertificateCredDefJson,
            null
        ).get()

        Log.d(TAG, "Indy: Stored Credential for Degree-Certificate")

        // Close Government wallet
        universityWallet.closeWallet().get()
        Log.d(TAG, "Indy: University Wallet was closed")

        // Close Alice wallet
        aliceWallet.closeWallet().get()
        Log.d(TAG, "Indy: Alice Wallet was closed")

        Log.d(TAG, "Indy: CREDENTIAL FOR DEGREE-CERTIFICATE DONE")

    }



    fun proofApplicationJob() {

        Log.d(TAG, "Indy: PROOF-REQUEST FOR JOB-APPLICATION STARTED")

        // Open Alice Wallet
        val aliceWallet = Wallet.openWallet(aliceWalletConfig, aliceWalletCredentials).get()
        Log.d(TAG, "Indy: Alice Wallet was opened")


        // Search for all requested attributes for the Proof
        val jobApplicationCredentialsSearch = CredentialsSearchForProofReq
                .open(aliceWallet, jobApplicationProofRequestJson, null).get()

        val jobApplicationCredentialsForAttribute1 = JSONArray(jobApplicationCredentialsSearch
                .fetchNextCredentials("attr1_referent", 100).get())
        val jobApplicationCredentialIdForAttribute1 = jobApplicationCredentialsForAttribute1
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")
        Log.d(TAG, "Indy: Search for Attribute 1: $jobApplicationCredentialsForAttribute1")

        val jobApplicationCredentialsForAttribute2 = JSONArray(jobApplicationCredentialsSearch
                .fetchNextCredentials("attr2_referent", 100).get())
        val jobApplicationCredentialIdForAttribute2 = jobApplicationCredentialsForAttribute2
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")
        Log.d(TAG, "Indy: Search for Attribute 2: $jobApplicationCredentialsForAttribute2")

        val jobApplicationCredentialsForAttribute3 = JSONArray(jobApplicationCredentialsSearch
                .fetchNextCredentials("attr3_referent", 100).get())
        val jobApplicationCredentialIdForAttribute3 = jobApplicationCredentialsForAttribute3
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")
        Log.d(TAG, "Indy: Search for Attribute 3: $jobApplicationCredentialsForAttribute3")

        val jobApplicationCredentialsForAttribute4 = JSONArray(jobApplicationCredentialsSearch
                .fetchNextCredentials("attr4_referent", 100).get())
        val jobApplicationCredentialIdForAttribute4 = jobApplicationCredentialsForAttribute4
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")
        Log.d(TAG, "Indy: Search for Attribute 4: $jobApplicationCredentialsForAttribute4")

        val jobApplicationCredentialsForAttribute5 = JSONArray(jobApplicationCredentialsSearch
                .fetchNextCredentials("attr5_referent", 100).get())
        val jobApplicationCredentialIdForAttribute5 = jobApplicationCredentialsForAttribute5
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")
        Log.d(TAG, "Indy: Search for Attribute 5: $jobApplicationCredentialsForAttribute5")

        Log.d(TAG, "Indy: Searched for all attributes of Proof-Request for Job-Application")


        // Search for all requested predicates for the Proof
        val jobApplicationCredentialsForPredicate = JSONArray(jobApplicationCredentialsSearch
                .fetchNextCredentials("predicate1_referent", 100).get())
        val jobApplicationCredentialIdForPredicate = jobApplicationCredentialsForPredicate
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Predicate: $jobApplicationCredentialIdForPredicate")

        Log.d(TAG, "Indy: Searched for all predicates of Proof-Request for Job-Application")


        jobApplicationCredentialsSearch.close()
        Log.d(TAG, "Indy: Search closed")


        // Create Proof
        val jobApplicationRequestedCredentialsJson = JSONObject()
            .put("self_attested_attributes", JSONObject() )
            .put("requested_attributes", JSONObject()
                    .put("attr1_referent", JSONObject()
                            .put("cred_id", jobApplicationCredentialIdForAttribute1)
                            .put("revealed", true)
                    )
                    .put("attr2_referent", JSONObject()
                            .put("cred_id", jobApplicationCredentialIdForAttribute2)
                            .put("revealed", false)
                    )
                    .put("attr3_referent", JSONObject()
                            .put("cred_id", jobApplicationCredentialIdForAttribute3)
                            .put("revealed", false)
                    )
                    .put("attr4_referent", JSONObject()
                            .put("cred_id", jobApplicationCredentialIdForAttribute4)
                            .put("revealed", false)
                    )
                    .put("attr5_referent", JSONObject()
                            .put("cred_id", jobApplicationCredentialIdForAttribute5)
                            .put("revealed", false)
                    )
            )
            .put("requested_predicates", JSONObject()
                    .put("predicate1_referent", JSONObject()
                            .put("cred_id", jobApplicationCredentialIdForPredicate)
                    )
            )
            .toString()

        Log.d(TAG, "Indy: JSON for Proof for Job-Application finished")


        val jobApplicationSchemas: String = JSONObject()
                .put(degreeCertificateSchemaId, JSONObject(degreeCertificateSchemaJson)).toString()
        val jobApplicationCredentialDefs: String = JSONObject()
                .put(degreeCertificateCredDefId, JSONObject(degreeCertificateCredDefJson)).toString()

        val jobApplicationRevocStates = JSONObject().toString()

        var jobApplicationProofJson = ""
        try {
            jobApplicationProofJson = Anoncreds.proverCreateProof(
                aliceWallet,
                jobApplicationProofRequestJson,
                jobApplicationRequestedCredentialsJson,
                aliceMasterSecretId,
                jobApplicationSchemas,
                jobApplicationCredentialDefs,
                jobApplicationRevocStates
            ).get()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        val jobApplicationProof = JSONObject(jobApplicationProofJson)

        Log.d(TAG, "Indy: Proof Creation of Proof-Request for Job-Application done")


        // Verifier verifies Proof
        val revealedAttr1 = jobApplicationProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr1_referent")

        Log.d(TAG, "Indy: Revealed attribute 1: $revealedAttr1")
        Log.d(TAG, "Indy: Revealed attribute 1: " + revealedAttr1.getString("raw"))


        val revocRegDefs = JSONObject().toString()
        val revocRegs = JSONObject().toString()

        val valid = Anoncreds.verifierVerifyProof(
            jobApplicationProofRequestJson,
            jobApplicationProofJson,
            jobApplicationSchemas,
            jobApplicationCredentialDefs,
            revocRegDefs,
            revocRegs
        ).get()

        Log.d(TAG, "Indy: Validity of Proof: $valid")


        // Close Alice wallet
        aliceWallet.closeWallet().get()
        Log.d(TAG, "Indy: Alice Wallet Closed")

        Log.d(TAG, "Indy: PROOF-REQUEST FOR JOB-APPLICATION DONE")

    }


    fun requestJobCertificate() {

        Log.d(TAG, "Indy: CREDENTIAL FOR JOB-APPLICATION STARTED")

        // Open Company Wallet
        val companyWallet = Wallet.openWallet(companyWalletConfig, companyWalletCredentials).get()
        Log.d(TAG, "Indy: Company Wallet was opened")

        // Open Alice Wallet
        val aliceWallet = Wallet.openWallet(aliceWalletConfig, aliceWalletCredentials).get()
        Log.d(TAG, "Indy: Alice Wallet was opened")


        // Create Credential Request
        val jobCreateCredReqResult = Anoncreds.proverCreateCredentialReq(
            aliceWallet,
            aliceDid,
            jobCredOffer,
            jobCertificateCredDefJson,
            aliceMasterSecretId
        ).get()
        val jobCredReqJson = jobCreateCredReqResult.credentialRequestJson
        val jobCredReqMetadataJson = jobCreateCredReqResult.credentialRequestMetadataJson
        Log.d(TAG, "Indy: Created Credential Request for Job-Certificate")


        // Company create Credential
        val jobCredValuesJson = JSONObject()
            .put("given_names", JSONObject()
                    .put("raw", "Alice")
                    .put("encoded", "1139481716457488690172217916278103335"))
            .put("family_name", JSONObject()
                    .put("raw", "Garcia")
                    .put("encoded", "5321642780241790123587902456789123452"))
            .put("employee_status", JSONObject()
                    .put("raw", "Permanent")
                    .put("encoded", "2143135425425143112321314321"))
            .put("salary", JSONObject()
                    .put("raw", "2400")
                    .put("encoded", "2400"))
            .put("experience", JSONObject()
                    .put("raw", "10")
                    .put("encoded", "10"))
            .toString()

        val jobCertificateCreateCredentialResult = Anoncreds.issuerCreateCredential(
            companyWallet,
            jobCredOffer,
            jobCredReqJson,
            jobCredValuesJson,
            null,
            -1
        ).get()

        val jobCredential = jobCertificateCreateCredentialResult.credentialJson

        Log.d(TAG, "Indy: Company created Credential for Job-Certificate")


        // Store Credential
        Anoncreds.proverStoreCredential(
            aliceWallet,
            null,
            jobCredReqMetadataJson,
            jobCredential,
            jobCertificateCredDefJson,
            null
        ).get()

        Log.d(TAG, "Indy: Stored Credential")


        // Close Company wallet
        companyWallet.closeWallet().get()
        Log.d(TAG, "Indy: Company Wallet was closed")

        // Close Alice wallet
        aliceWallet.closeWallet().get()
        Log.d(TAG, "Indy: Alice Wallet was closed")

        Log.d(TAG, "Indy: CREDENTIAL FOR JOB-APPLICATION DONE")

    }

}