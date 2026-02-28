package com.nextgenx.batterydrainpredictor

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText

class OnboardingActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var spinnerGender: Spinner
    
    private lateinit var cardPermission: MaterialCardView
    private lateinit var btnGrantPermission: Button
    private lateinit var btnGetStarted: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sharedPreferences = getSharedPreferences("BatteryDrainPrefs", Context.MODE_PRIVATE)

        // If user already completed onboarding, skip directly to Dashboard
        if (sharedPreferences.getBoolean("onboarding_complete", false) && hasUsageStatsPermission()) {
            goToDashboard()
            return
        }

        setContentView(R.layout.activity_onboarding)

        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        spinnerGender = findViewById(R.id.spinnerGender)
        cardPermission = findViewById(R.id.cardPermission)
        btnGrantPermission = findViewById(R.id.btnGrantPermission)
        btnGetStarted = findViewById(R.id.btnGetStarted)

        setupGenderSpinner()

        btnGrantPermission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        btnGetStarted.setOnClickListener {
            completeOnboarding()
        }

        // Add text listeners to dynamically enable/disable the Start button
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkCompletionStatus()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etName.addTextChangedListener(textWatcher)
        etAge.addTextChangedListener(textWatcher)
    }

    override fun onResume() {
        super.onResume()
        checkCompletionStatus()
    }

    private fun setupGenderSpinner() {
        val genders = arrayOf("Male", "Female")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        spinnerGender.adapter = genderAdapter
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun checkCompletionStatus() {
        val hasPermission = hasUsageStatsPermission()
        val hasName = etName.text.toString().trim().isNotEmpty()
        val hasAge = etAge.text.toString().trim().isNotEmpty()

        if (hasPermission) {
            cardPermission.visibility = View.GONE
        } else {
            cardPermission.visibility = View.VISIBLE
        }

        btnGetStarted.isEnabled = hasPermission && hasName && hasAge
    }

    private fun completeOnboarding() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val isMale = spinnerGender.selectedItem.toString() == "Male"

        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("age", age)
        editor.putBoolean("isMale", isMale)
        editor.putBoolean("onboarding_complete", true)
        editor.apply()

        goToDashboard()
    }

    private fun goToDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Prevent going back to onboarding
    }
}
