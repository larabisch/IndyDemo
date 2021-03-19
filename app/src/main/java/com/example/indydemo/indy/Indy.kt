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

    private val myWalletConfig = JSONObject()
            .put("id", "myWallet").toString()
    private val myWalletCredentials = JSONObject()
            .put("key", "my_wallet_key").toString()


    private lateinit var myMasterSecretId : String

    private var universityDid = "V4SGRU86Z58d6TV7PBUe6f"
    private val companyDid = "Th7MpTaRZVRYnPiabds81Y"
    private var governmentDid = "NcYxiDXkpYi6ov5FcYDi1e"
    private var myDid = "Qx2KARvFsPcWZJaqF3FbHu"


    private lateinit var identityCredOffer : String
    private lateinit var degreeCredOffer : String
    private lateinit var jobCredOffer : String

    private lateinit var jobApplicationProofRequestJson : String
    private lateinit var degreeRequestProofRequestJson : String
    private lateinit var loanApplicationProofRequestJson : String

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


        // Create My Wallet
        try {
            Wallet.deleteWallet(myWalletConfig, myWalletCredentials).get()
        } catch (e: ExecutionException) {
            Log.d(TAG, e.toString())
        } catch (e: WalletNotFoundException) {
            Log.d(TAG, e.toString())
        }
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get()
        Log.d(TAG, "Indy: My Wallet was created")

        // Open My Wallet and generate Master Secret
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
        myMasterSecretId = Anoncreds.proverCreateMasterSecret(myWallet, null).get()
        Log.d(TAG, "Indy: My Master Secret was created")
        myWallet.closeWallet()


        Log.d(TAG, "Indy: WALLET INITIALISATION DONE")
    }



    fun createIdentityCredOffer() {

        Log.d(TAG, "Indy: CREATE IDENTITY CREDENTIAL OFFER STARTED")


        // 1. Open Government Wallet
        val governmentWallet = Wallet.openWallet(governmentWalletConfig, governmentWalletCredentials).get()
        Log.d(TAG, "Indy: Government Wallet opened")


        // 3. Government Creates Credential Schema
        val identityCertificateSchemaName = "Identity-Certificate"
        val identityCertificateSchemaVersion = "1.0"
        val identityCertificateSchemaAttributes = JSONArray()
            .put("family_name")
            //.put("birth_name")
            .put("given_names")
            //.put("doctoral_degree")
            .put("date_of_birth")
            .put("place_of_birth")
            .put("address")
            .put("nationality")
            .put("document_type")
            .put("valid_until")
            .put("issuing_country")
            //.put("artistic_name")
            //.put("pseudonym")
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


        // 4. Government creates Credential Definition
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


        // 5. Government creates Credential Offer
        identityCredOffer = Anoncreds.issuerCreateCredentialOffer(
            governmentWallet,
            identityCertificateCredDefId
        ).get()

        Log.d(TAG, "Indy: Government created Credential Offer for Identity-Certificate")


        // 9. Close Government wallet
        governmentWallet.closeWallet().get()
        Log.d(TAG, "Indy: Government Wallet was closed")

        Log.d(TAG, "Indy: CREATE IDENTITY CREDENTIAL OFFER DONE")

    }



    fun createDegreeCredOffer() {

        Log.d(TAG, "Indy: CREATE DEGREE CREDENTIAL OFFER STARTED")


        // 1. Open University Wallet
        val universityWallet = Wallet.openWallet(universityWalletConfig, universityWalletCredentials).get()
        Log.d(TAG, "Indy: University Wallet was opened")


        // 3. University Creates Credential Schema
        val bachelorCertificateSchemaName = "Degree-Certificate"
        val bachelorCertificateSchemaVersion = "1.0"
        val bachelorCertificateSchemaAttributes = JSONArray()
            .put("given_names")
            .put("family_name")
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


        // 4. University create Credential Definition
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


        // 5. University Creates Credential Offer
        degreeCredOffer = Anoncreds.issuerCreateCredentialOffer(
            universityWallet,
            degreeCertificateCredDefId
        ).get()

        Log.d(TAG, "Indy: University created Credential Offer for Degree-Certificate")

        // 10. Close Government wallet
        universityWallet.closeWallet().get()
        Log.d(TAG, "Indy: University Wallet was closed")

        Log.d(TAG, "Indy: CREATE DEGREE CREDENTIAL OFFER DONE")

    }



    fun createJobCredOffer() {

        Log.d(TAG, "Indy: CREATE JOB CREDENTIAL OFFER STARTED")

        // 1. Open Company Wallet
        val companyWallet = Wallet.openWallet(companyWalletConfig, companyWalletCredentials).get()
        Log.d(TAG, "Indy: Company Wallet was opened")


        // 3. Company Creates Credential Schema
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


        // 4. Company create Credential Definition
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


        // 5. Company Creates Credential Offer
        jobCredOffer = Anoncreds.issuerCreateCredentialOffer(
                companyWallet,
                jobCertificateCredDefId
        ).get()

        Log.d(TAG, "Indy: Company created Credential Offer for Job-Certificate")

        // 9. Close Company wallet
        companyWallet.closeWallet().get()
        Log.d(TAG, "Indy: Company Wallet was closed")


        Log.d(TAG, "Indy: CREATE JOB CREDENTIAL OFFER DONE")


    }


    fun createProofRequestDegreeCertificate() {

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST DEGREE-CERTIFICATE STARTED")


        // 2. I Get Credentials for Proof Request
        val degreeRequestNonce = Anoncreds.generateNonce().get()

        degreeRequestProofRequestJson = JSONObject()
            .put("nonce", degreeRequestNonce)
            .put("name", "Degree-Request")
            .put("version", "0.1")
            .put("requested_attributes", JSONObject()
                .put("attr1_referent", JSONObject().put("name", "given_names"))
                .put("attr2_referent", JSONObject().put("name", "family_name"))
                .put("attr3_referent", JSONObject().put("name", "date_of_birth"))
                .put("attr4_referent", JSONObject().put("name", "place_of_birth"))
            )
            .put("requested_predicates", JSONObject()
                .put("predicate1_referent", JSONObject()
                    .put("name", "valid_until")
                    .put("p_type", ">=")
                    .put("p_value", 20210211)
                )
            )
            .toString()

        Log.d(TAG, "Indy: JSON Creation for Proof-Request for Degree-Certificate")

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST DEGREE-CERTIFICATE DONE")

    }

    fun createProofRequestJobApplication() {

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST JOB-APPLICATION STARTED")


        // 2. Prover Gets Credentials for Proof Request
        val jobApplicationNonce = Anoncreds.generateNonce().get()

        jobApplicationProofRequestJson = JSONObject()
            .put("nonce", jobApplicationNonce)
            .put("name", "Job-Application")
            .put("version", "0.1")
            .put("requested_attributes", JSONObject()
                .put("attr1_referent", JSONObject().put("name", "given_names"))
                .put("attr2_referent", JSONObject().put("name", "family_name"))
                .put("attr3_referent", JSONObject().put("name", "degree"))
                .put("attr4_referent", JSONObject().put("name", "status"))
                .put("attr5_referent", JSONObject().put("name", "phone_number"))

            )
            .put("requested_predicates", JSONObject()
                .put("predicate1_referent", JSONObject()
                    .put("name", "average")
                    .put("p_type", ">=")
                    .put("p_value", 4)
                )
            )
            .toString()

        Log.d(TAG, "Indy: JSON Creation for Proof-Request for Job-Application")

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST JOB-APPLICATION DONE")

    }

    fun createProofRequestLoanApplication () {

        Log.d(TAG, "Indy: CREATE PROOF-REQUEST LOAN-APPLICATION STARTED")


        // 2. I Get Credentials for Proof Request
        val loanApplicationNonce = Anoncreds.generateNonce().get()

        loanApplicationProofRequestJson = JSONObject()
            .put("nonce", loanApplicationNonce)
            .put("name", "Job-Application")
            .put("version", "0.1")
            .put("requested_attributes", JSONObject()
                .put("attr1_referent", JSONObject().put("name", "employee_status"))

            )
            .put("requested_predicates", JSONObject()
                .put("predicate1_referent", JSONObject()
                    .put("name", "salary")
                    .put("p_type", ">=")
                    .put("p_value", 2000)
                )
            )
            .toString()

        Log.d(TAG, "Indy: JSON Creation for Proof-Request for Loan-Application")


        Log.d(TAG, "Indy: CREATE PROOF-REQUEST LOAN-APPLICATION DONE")

    }





    fun requestIdentityCertificate() {

        Log.d(TAG, "Indy: REQUEST IDENTITY CERTIFICATE STARTED")


        // 1. Open Government Wallet
        val governmentWallet = Wallet.openWallet(governmentWalletConfig, governmentWalletCredentials).get()
        Log.d(TAG, "Indy: Government Wallet opened")

        // 2. Open My Wallet
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
        Log.d(TAG, "Indy: My Wallet opened")

        // 6. I Create Credential Request
        val identityCreateCredReqResult = Anoncreds.proverCreateCredentialReq(
            myWallet,
            myDid,
            identityCredOffer,
            identityCertificateCredDefJson,
            myMasterSecretId
        ).get()
        val identityCredReqJson = identityCreateCredReqResult.credentialRequestJson
        val identityCredReqMetadataJson = identityCreateCredReqResult.credentialRequestMetadataJson
        Log.d(TAG, "Indy: I created Credential Request for Identity-Certificate")


        // 7. Government creates Credential
        val identityCredValuesJson = JSONObject()
            .put("family_name", JSONObject()
                    .put("raw", "Garcia")
                    .put("encoded", "946510486021438157324488086511345775493242887916"))
            //.put("birth_name", JSONObject().put("raw", "").put("encoded", ""))
            .put("given_names", JSONObject()
                    .put("raw", "Alice")
                    .put("encoded", "303680606261854555853193981910306662446219611633"))
            //.put("doctoral_degree", JSONObject().put("raw", "").put("encoded", ""))
            .put("date_of_birth", JSONObject()
                    .put("raw", "1964-08-12")
                    .put("encoded", "19640812"))
            .put("place_of_birth", JSONObject()
                    .put("raw", "Berlin")
                    .put("encoded", "35213462824075338563596693498506032610897976097"))
            .put("address", JSONObject()
                    .put("raw", "Sample street 12, 12059 Berlin")
                    .put("encoded", "947397614802691795330029495488188401319414724640"))
            .put("nationality", JSONObject()
                    .put("raw", "German")
                    .put("encoded", "1247798522173559676754790998791794996059419723869"))
            .put("document_type", JSONObject()
                    .put("raw", "Identity card")
                    .put("encoded", "144992736518735056036859271803104200336197691206"))
            .put("valid_until", JSONObject()
                    .put("raw", "20291031")
                    .put("encoded", "20291031"))
            .put("issuing_country", JSONObject()
                    .put("raw", "Germany")
                    .put("encoded", "136062252323357910523596751724311663715246877932"))
            //.put("artistic_name", JSONObject().put("raw", "").put("encoded", ""))
            //.put("pseudonym", JSONObject().put("raw", "").put("encoded", ""))
            .toString()

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


        // 8. Prover Stores Credential
        Anoncreds.proverStoreCredential(
            myWallet,
            null,
            identityCredReqMetadataJson,
            identityCredential,
            identityCertificateCredDefJson,
            null
        ).get()

        Log.d(TAG, "Indy: I store the Credential")

        // 9. Close Government wallet
        governmentWallet.closeWallet().get()
        Log.d(TAG, "Indy: Government Wallet was closed")

        // 10. Close My wallet
        myWallet.closeWallet().get()
        Log.d(TAG, "Indy: My Wallet was closed")


        Log.d(TAG, "Indy: REQUEST IDENTITY CERTIFICATE DONE")

    }




    fun proofRequestDegreeCertificate() {

        Log.d(TAG, "Indy: PROOF-REQUEST FOR DEGREE-CERTIFICATE STARTED")


        // 1. Open My Wallet
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
        Log.d(TAG, "Indy: My Wallet was opened")


        // 3. I Search for all requested Credentials
        val degreeRequestCredentialsSearch = CredentialsSearchForProofReq
                .open(myWallet, degreeRequestProofRequestJson, null).get()

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

        Log.d(TAG, "Indy: I searched for all attributes for Proof-Request for Degree-Certificate")

        val degreeRequestCredentialsForPredicate = JSONArray(degreeRequestCredentialsSearch
                .fetchNextCredentials("predicate1_referent", 100).get())
        val degreeRequestCredentialIdForPredicate = degreeRequestCredentialsForPredicate
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")
        Log.d(TAG, "Indy: Search for Predicate: $degreeRequestCredentialIdForPredicate")

        Log.d(TAG, "Indy: Prover searched for all Predicates of Proof-Request for Degree-Certificate")

        degreeRequestCredentialsSearch.close()
        Log.d(TAG, "Indy: I searched for all requested Credentials of Proof-Request for Degree-Certificate")


        // 4. I create Proof
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

        Log.d(TAG, "Indy: JSON for Proof-Request for Degree-Certificate finished")


        val degreeRequestSchemas: String = JSONObject()
                .put(identityCertificateSchemaId, JSONObject(identityCertificateSchemaJson)).toString()
        val degreeRequestCredentialDefs: String = JSONObject()
                .put(identityCertificateCredDefId, JSONObject(identityCertificateCredDefJson)).toString()
        val degreeRequestRevocStates = JSONObject().toString()

        var degreeRequestProofJson = ""
        try {
            degreeRequestProofJson = Anoncreds.proverCreateProof(
                myWallet,
                degreeRequestProofRequestJson,
                degreeRequestedCredentialsJson,
                myMasterSecretId,
                degreeRequestSchemas,
                degreeRequestCredentialDefs,
                degreeRequestRevocStates
            ).get()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        val degreeRequestProof = JSONObject(degreeRequestProofJson)

        Log.d(TAG, "Indy: Proof Creation of Proof-Request for Degree-Certificate done")


        // 5. Verifier verifies Proof
        val revealedAttr1 = degreeRequestProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr1_referent")

        Log.d(TAG, "Indy: Revealed attribute 1: $revealedAttr1")
        Log.d(TAG, "Indy: Revealed attribute 1: " + revealedAttr1.getString("raw"))

        val revealedAttr2 = degreeRequestProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr2_referent")

        Log.d(TAG, "Indy: Revealed attribute 2: $revealedAttr2")
        Log.d(TAG, "Indy: Revealed attribute 2: " + revealedAttr2.getString("raw"))

        val revealedAttr3 = degreeRequestProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr3_referent")

        Log.d(TAG, "Indy: Revealed attribute 3: $revealedAttr3")
        Log.d(TAG, "Indy: Revealed attribute 3: " + revealedAttr3.getString("raw"))

        val revealedAttr4 = degreeRequestProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr4_referent")

        Log.d(TAG, "Indy: Revealed attribute 4: $revealedAttr4")
        Log.d(TAG, "Indy: Revealed attribute 4: " + revealedAttr4.getString("raw"))


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


        // 6. Close my wallet
        myWallet.closeWallet().get()
        Log.d(TAG, "Indy: My Wallet was closed")


        Log.d(TAG, "Indy: PROOF-REQUEST FOR DEGREE-CERTIFICATE DONE")

    }



    fun requestDegreeCertificate() {

        Log.d(TAG, "Indy: CREDENTIAL FOR DEGREE-CERTIFICATE STARTED")


        // 1. Open University Wallet
        val universityWallet = Wallet.openWallet(universityWalletConfig, universityWalletCredentials).get()
        Log.d(TAG, "Indy: University Wallet was opened")

        // 2. Open My Wallet
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
        Log.d(TAG, "Indy: My Wallet was opened")


        // 6. I Create Credential Request
        val bachelorCreateCredReqResult = Anoncreds.proverCreateCredentialReq(
            myWallet,
            myDid,
            degreeCredOffer,
            degreeCertificateCredDefJson,
            myMasterSecretId
        ).get()

        val bachelorCredReqJson = bachelorCreateCredReqResult.credentialRequestJson
        val bachelorCredReqMetadataJson = bachelorCreateCredReqResult.credentialRequestMetadataJson

        Log.d(TAG, "Indy: I create Credential Request for Degree-Certificate")


        // 7. University create Credential
        val bachelorCredValuesJson = JSONObject()
            .put("given_names", JSONObject()
                    .put("raw", "Alice")
                    .put("encoded", "1139481716457488690172217916278103335"))
            .put("family_name", JSONObject()
                    .put("raw", "Garcia")
                    .put("encoded", "5321642780241790123587902456789123452"))
            .put("degree", JSONObject()
                    .put("raw", "Bachelor of Science, Marketing")
                    .put("encoded", "12434523576212321"))
            .put("status", JSONObject()
                    .put("raw", "graduated")
                    .put("encoded", "2213454313412354"))
            .put("year", JSONObject()
                    .put("raw", "2020")
                    .put("encoded", "2020"))
            .put("average", JSONObject()
                    .put("raw", "5")
                    .put("encoded", "5"))
            .toString()

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


        // 8. Prover Stores Credential
        Anoncreds.proverStoreCredential(
            myWallet,
            null,
            bachelorCredReqMetadataJson,
            bachelorCredential,
            degreeCertificateCredDefJson,
            null
        ).get()

        Log.d(TAG, "Indy: I store Credential for Degree-Certificate")

        // 10. Close Government wallet
        universityWallet.closeWallet().get()
        Log.d(TAG, "Indy: University Wallet was closed")

        // 11. Close my wallet
        myWallet.closeWallet().get()
        Log.d(TAG, "Indy: My Wallet was closed")


        Log.d(TAG, "Indy: CREDENTIAL FOR DEGREE-CERTIFICATE DONE")

    }



    fun proofRequestApplicationJob() {

        Log.d(TAG, "Indy: PROOF-REQUEST FOR JOB-APPLICATION STARTED")


        // 1. Open My Wallet
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
        Log.d(TAG, "Indy: My Wallet was opened")


        // 3. I Search for all requested Credentials

        val jobApplicationCredentialsSearch = CredentialsSearchForProofReq
                .open(myWallet, jobApplicationProofRequestJson, null).get()

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

        Log.d(TAG, "Indy: Search for Attribute 5: $jobApplicationCredentialsForAttribute5")

        Log.d(TAG, "Indy: Prover searched for all attributes of Proof-Request for Job-Application")


        val jobApplicationCredentialsForPredicate = JSONArray(jobApplicationCredentialsSearch
                .fetchNextCredentials("predicate1_referent", 100).get())
        val jobApplicationCredentialIdForPredicate = jobApplicationCredentialsForPredicate
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Predicate: $jobApplicationCredentialIdForPredicate")

        Log.d(TAG, "Indy: Prover searched for all Predicates of Proof-Request for Job-Application")


        jobApplicationCredentialsSearch.close()
        Log.d(TAG, "Indy: I searched for all requested Credentials of Proof-Request for Job-Application")


        // 4. I Create Proof
        val jobApplicationSelfAttestedValue = "01234567890"
        val jobApplicationRequestedCredentialsJson = JSONObject()
            .put("self_attested_attributes", JSONObject()
                    .put("attr5_referent", jobApplicationSelfAttestedValue)
            )
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
            )
            .put("requested_predicates", JSONObject()
                .put("predicate1_referent", JSONObject()
                    .put("cred_id", jobApplicationCredentialIdForPredicate)
                )
            )
            .toString()

        Log.d(TAG, "Indy: JSON for Proof-Request for Job-Application finished")


        val jobApplicationSchemas: String = JSONObject()
                .put(degreeCertificateSchemaId, JSONObject(degreeCertificateSchemaJson)).toString()
        val jobApplicationCredentialDefs: String = JSONObject()
                .put(degreeCertificateCredDefId, JSONObject(degreeCertificateCredDefJson)).toString()

        val jobApplicationRevocStates = JSONObject().toString()

        var jobApplicationProofJson = ""
        try {
            jobApplicationProofJson = Anoncreds.proverCreateProof(
                myWallet,
                jobApplicationProofRequestJson,
                jobApplicationRequestedCredentialsJson,
                myMasterSecretId,
                jobApplicationSchemas,
                jobApplicationCredentialDefs,
                jobApplicationRevocStates
            ).get()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        val jobApplicationProof = JSONObject(jobApplicationProofJson)

        Log.d(TAG, "Indy: Proof Creation of Proof-Request for Job-Application done")


        // 5. Verifier verifies Proof
        val revealedAttr1 = jobApplicationProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr1_referent")

        Log.d(TAG, "Indy: Revealed attribute 1: $revealedAttr1")
        Log.d(TAG, "Indy: Revealed attribute 1: " + revealedAttr1.getString("raw"))
        Log.d(TAG, "Indy: (should be not null) " + jobApplicationProof
                .getJSONObject("requested_proof")
                .getJSONObject("unrevealed_attrs")
                .getJSONObject("attr2_referent")
                .getInt("sub_proof_index"))
        Log.d(TAG, "Indy: selfAttestedValue: $jobApplicationSelfAttestedValue should be: "
                + jobApplicationProof.getJSONObject("requested_proof")
                .getJSONObject("self_attested_attrs")
                .getString("attr5_referent"))


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


        // 6. Close my wallet
        myWallet.closeWallet().get()
        Log.d(TAG, "Indy: My Wallet Closed")


        Log.d(TAG, "Indy: PROOF-REQUEST FOR JOB-APPLICATION DONE")

    }


    fun requestJobCertificate() {

        Log.d(TAG, "Indy: CREDENTIAL FOR JOB-APPLICATION STARTED")


        // 1. Open Company Wallet
        val companyWallet = Wallet.openWallet(companyWalletConfig, companyWalletCredentials).get()
        Log.d(TAG, "Indy: Company Wallet was opened")

        // 2. Open My Wallet
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
        Log.d(TAG, "Indy: My Wallet was opened")


        // 6. I Create Credential Request
        val jobCreateCredReqResult = Anoncreds.proverCreateCredentialReq(
            myWallet,
            myDid,
            jobCredOffer,
            jobCertificateCredDefJson,
            myMasterSecretId
        ).get()
        val jobCredReqJson = jobCreateCredReqResult.credentialRequestJson
        val jobCredReqMetadataJson = jobCreateCredReqResult.credentialRequestMetadataJson
        Log.d(TAG, "Indy: I created Credential Request for Job-Certificate")


        // 7. Company create Credential
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


        // 8. Prover Stores Credential
        Anoncreds.proverStoreCredential(
            myWallet,
            null,
            jobCredReqMetadataJson,
            jobCredential,
            jobCertificateCredDefJson,
            null
        ).get()

        Log.d(TAG, "Indy: Prover stores Credential")

        // 9. Close Company wallet
        companyWallet.closeWallet().get()
        Log.d(TAG, "Indy: Company Wallet was closed")

        // 10. Close My wallet
        myWallet.closeWallet().get()
        Log.d(TAG, "Indy: My Wallet was closed")


        Log.d(TAG, "Indy: CREDENTIAL FOR JOB-APPLICATION DONE")

    }




    fun proofRequestApplicationLoan() {

        Log.d(TAG, "Indy: PROOF-REQUEST FOR LOAN-APPLICATION STARTED")


        // 1. Open My Wallet
        val myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get()
        Log.d(TAG, "Indy: My Wallet was opened")


        // 3. I search for all Credentials
        val loanApplicationCredentialsSearch = CredentialsSearchForProofReq
                .open(myWallet, loanApplicationProofRequestJson, null).get()

        val loanApplicationCredentialsForAttribute = JSONArray(loanApplicationCredentialsSearch
                .fetchNextCredentials("attr1_referent", 100).get())
        val loanApplicationCredentialIdForAttribute = loanApplicationCredentialsForAttribute
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Attribute 1: $loanApplicationCredentialsForAttribute")


        Log.d(TAG, "Indy: Prover searched for all attributes of Proof-Request for Loan-Application")

        val loanApplicationCredentialsForPredicate = JSONArray(loanApplicationCredentialsSearch
                .fetchNextCredentials("predicate1_referent", 100).get())
        val loanApplicationCredentialIdForPredicate = loanApplicationCredentialsForPredicate
                .getJSONObject(0).getJSONObject("cred_info").getString("referent")

        Log.d(TAG, "Indy: Search for Predicate: $loanApplicationCredentialIdForPredicate")

        Log.d(TAG, "Indy: Prover searched for all Predicates of Proof-Request for Loan-Application")

        loanApplicationCredentialsSearch.close()
        Log.d(TAG, "Indy: I searched for all requested Credentials of Proof-Request for Job-Application")


        // 4. I Create Proof
        val loanApplicationRequestedCredentialsJson = JSONObject()
            .put("self_attested_attributes", JSONObject())
            .put("requested_attributes", JSONObject()
                .put("attr1_referent", JSONObject()
                    .put("cred_id", loanApplicationCredentialIdForAttribute)
                    .put("revealed", true)
                )
            )
            .put("requested_predicates", JSONObject()
                .put("predicate1_referent", JSONObject()
                    .put("cred_id", loanApplicationCredentialIdForPredicate)
                )
            )
            .toString()

        Log.d(TAG, "Indy: JSON for Proof-Request for Loan-Application finished")


        val loanApplicationSchemas: String = JSONObject()
                .put(jobCertificateSchemaId, JSONObject(jobCertificateSchemaJson)).toString()
        val loanApplicationCredentialDefs: String = JSONObject()
                .put(jobCertificateCredDefId, JSONObject(jobCertificateCredDefJson)).toString()

        val loanApplicationRevocStates = JSONObject().toString()

        var loanApplicationProofJson = ""
        try {
            loanApplicationProofJson = Anoncreds.proverCreateProof(
                myWallet,
                loanApplicationProofRequestJson,
                loanApplicationRequestedCredentialsJson,
                myMasterSecretId,
                loanApplicationSchemas,
                loanApplicationCredentialDefs,
                loanApplicationRevocStates
            ).get()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }

        val loanApplicationProof = JSONObject(loanApplicationProofJson)

        Log.d(TAG, "Indy: Proof Creation of Proof-Request for Loan-Application done")


        // 5. Verifier verifies Proof
        val revealedAttr1 = loanApplicationProof.getJSONObject("requested_proof")
                .getJSONObject("revealed_attrs").getJSONObject("attr1_referent")

        Log.d(TAG, "Indy: Revealed attribute 1: $revealedAttr1")
        Log.d(TAG, "Indy: Revealed attribute 1: " + revealedAttr1.getString("raw"))

        val revocRegDefs = JSONObject().toString()
        val revocRegs = JSONObject().toString()

        val valid = Anoncreds.verifierVerifyProof(
            loanApplicationProofRequestJson,
            loanApplicationProofJson,
            loanApplicationSchemas,
            loanApplicationCredentialDefs,
            revocRegDefs,
            revocRegs
        ).get()

        Log.d(TAG, "Indy: Validity of Proof: $valid")


        // 6. Close my wallet
        myWallet.closeWallet().get()
        Log.d(TAG, "Indy: My Wallet was closed")


        Log.d(TAG, "Indy: PROOF-REQUEST FOR LOAN-APPLICATION DONE")

    }

}