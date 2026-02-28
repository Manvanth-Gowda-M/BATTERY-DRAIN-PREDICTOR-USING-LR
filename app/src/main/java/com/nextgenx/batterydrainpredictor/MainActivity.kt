package com.nextgenx.batterydrainpredictor

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvCurrentBattery: TextView
    private lateinit var tvRemainingTime: TextView
    private lateinit var tvDailyDrain: TextView
    private lateinit var tvEstimatedLabel: TextView

    private lateinit var heroCard: MaterialCardView
    private lateinit var cardAppUsage: MaterialCardView
    private lateinit var cardScreenTime: MaterialCardView
    private lateinit var cardNumApps: MaterialCardView
    private lateinit var cardDataUsage: MaterialCardView

    private lateinit var tvAppUsage: TextView
    private lateinit var tvScreenTime: TextView
    private lateinit var tvNumApps: TextView
    private lateinit var tvDataUsage: TextView
    private lateinit var fabRefresh: FloatingActionButton

    private lateinit var sharedPreferences: SharedPreferences

    // Fetched values
    private var fetchedAppUsage = 0.0
    private var fetchedScreenTime = 0.0
    private var fetchedNumApps = 0.0
    private var fetchedDataUsage = 0.0
    
    private var isFirstLoad = true
    
    // Default assumed battery capacity if not retrievable (mAh)
    private val DEFAULT_BATTERY_CAPACITY_MAH = 4000.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("BatteryDrainPrefs", Context.MODE_PRIVATE)

        // Initialize UI components
        tvGreeting = findViewById(R.id.tvGreeting)
        tvCurrentBattery = findViewById(R.id.tvCurrentBattery)
        tvRemainingTime = findViewById(R.id.tvRemainingTime)
        tvDailyDrain = findViewById(R.id.tvDailyDrain)
        tvEstimatedLabel = findViewById(R.id.tvEstimatedLabel)
        
        heroCard = findViewById(R.id.heroCard)
        cardAppUsage = findViewById(R.id.cardAppUsage)
        cardScreenTime = findViewById(R.id.cardScreenTime)
        cardNumApps = findViewById(R.id.cardNumApps)
        cardDataUsage = findViewById(R.id.cardDataUsage)

        // Hide cards initially to prevent flashing before animation
        heroCard.visibility = View.INVISIBLE
        cardAppUsage.visibility = View.INVISIBLE
        cardScreenTime.visibility = View.INVISIBLE
        cardNumApps.visibility = View.INVISIBLE
        cardDataUsage.visibility = View.INVISIBLE
        
        tvAppUsage = findViewById(R.id.tvAppUsage)
        tvScreenTime = findViewById(R.id.tvScreenTime)
        tvNumApps = findViewById(R.id.tvNumApps)
        tvDataUsage = findViewById(R.id.tvDataUsage)
        fabRefresh = findViewById(R.id.fabRefresh)

        setupGreeting()

        fabRefresh.setOnClickListener {
            isFirstLoad = true // Force animation on explicit refresh
            refreshDashboard()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasUsageStatsPermission()) {
            Toast.makeText(this, "Usage Access is required to get stats.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            return
        }
        refreshDashboard()
    }

    private fun setupGreeting() {
        val name = sharedPreferences.getString("name", "User")
        tvGreeting.text = "Hello, $name"
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

    private fun refreshDashboard() {
        fetchDeviceStats()
        val currentBatteryPct = fetchCurrentBatteryPercentage()
        tvCurrentBattery.text = "${(currentBatteryPct * 100).toInt()}%"
        
        performPredictionAndEstimate(currentBatteryPct)

        if (isFirstLoad) {
            animateDashboardCards()
            isFirstLoad = false
        } else {
            // Already animated, just make sure they remain visible
            heroCard.visibility = View.VISIBLE
            cardAppUsage.visibility = View.VISIBLE
            cardScreenTime.visibility = View.VISIBLE
            cardNumApps.visibility = View.VISIBLE
            cardDataUsage.visibility = View.VISIBLE
        }
    }

    private fun animateDashboardCards() {
        // ... (Keep existing animate code if possible, or just animate the new views too)
        val cards = listOf(heroCard, cardAppUsage, cardScreenTime, cardNumApps, cardDataUsage)
        cards.forEachIndexed { index, view ->
            view.postDelayed({
                view.visibility = View.VISIBLE
                val anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up)
                view.startAnimation(anim)
            }, index * 80L)
        }
    }

    private fun fetchCurrentBatteryPercentage(): Float {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            this.registerReceiver(null, ifilter)
        }
        
        // --- NEW: Hardware Health & Charging Status ---
        if (batteryStatus != null) {
            val status: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                      status == BatteryManager.BATTERY_STATUS_FULL

            val temp: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            val voltage: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            val healthInfo: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)

            // Update UI 
            findViewById<TextView>(R.id.tvTemp).text = "${temp / 10.0} °C"
            findViewById<TextView>(R.id.tvVoltage).text = "$voltage mV"

            val healthString = when (healthInfo) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Volt"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failed"
                BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                else -> "Unknown"
            }
            findViewById<TextView>(R.id.tvHealth).text = healthString

            // Modify predict output if charging
            if (isCharging) {
                findViewById<TextView>(R.id.tvDailyDrain).text = "Device is Charging ⚡"
                val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                return if (level == -1 || scale == -1) 0.5f else level / scale.toFloat()
            }
        }
        // ----------------------------------------------

        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        
        return if (level == -1 || scale == -1) 0.5f else level / scale.toFloat()
    }

    private fun fetchDeviceStats() {
        // Fetch Usage Stats
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        var totalForegroundTimeMS = 0L
        if (stats != null) {
            for (usageStats in stats) {
                totalForegroundTimeMS += usageStats.totalTimeInForeground
            }
        }
        
        fetchedAppUsage = totalForegroundTimeMS / (1000.0 * 60.0) // minutes
        fetchedScreenTime = totalForegroundTimeMS / (1000.0 * 60.0 * 60.0) // hours

        tvAppUsage.text = "%.1f h".format(fetchedScreenTime) // format nicely
        tvScreenTime.text = "%.1f h".format(fetchedScreenTime)
        
        if (fetchedAppUsage > 60) {
            tvAppUsage.text = "%.1f h".format(fetchedAppUsage / 60.0)
        } else {
            tvAppUsage.text = "%.0f m".format(fetchedAppUsage)
        }

        // Fetch Num Apps
        fetchedNumApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA).size.toDouble()
        tvNumApps.text = "%.0f".format(fetchedNumApps)

        // Fetch Data Usage Approximation since boot
        val totalBytes = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()
        fetchedDataUsage = totalBytes / (1024.0 * 1024.0) // MB
        
        if (fetchedDataUsage > 1024) {
            tvDataUsage.text = "%.1f GB".format(fetchedDataUsage / 1024.0)
        } else {
            tvDataUsage.text = "%.0f MB".format(fetchedDataUsage)
        }
    }

    private fun performPredictionAndEstimate(currentBatteryPct: Float) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            this.registerReceiver(null, ifilter)
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                  status == BatteryManager.BATTERY_STATUS_FULL

        if (isCharging) {
            // --- SMART CHARGING STATE ---
            tvEstimatedLabel.text = "Time Until Full"
            findViewById<TextView>(R.id.tvDailyDrain).text = "Charging ⚡"
            
            // Just a rough estimation for charging (assuming standard 15W/20W charging takes ~1.5 hours to full)
            // Real devices have deeply complex charging curves, so we approximate linearly for the remaining %
            val pctRemainingToFull = 1.0f - currentBatteryPct
            val totalHoursToChargeFromZero = 2.0 // Assume 2 hours 0-100%
            val hoursRemaining = totalHoursToChargeFromZero * pctRemainingToFull

            if (hoursRemaining > 0) {
                val hours = hoursRemaining.toInt()
                val minutes = ((hoursRemaining - hours) * 60).toInt()
                tvRemainingTime.text = "${hours}h ${minutes}m"
            } else {
                tvRemainingTime.text = "Full"
            }
            
            // To be thorough, change the hero label
            // We need to change the standard label
        } else {
            // --- STANDARD DRAIN PREDICTION ---
            tvEstimatedLabel.text = "Estimated Remaining Time"
            val ageStr = sharedPreferences.getString("age", "25")
            val age = ageStr?.toDoubleOrNull() ?: 25.0
            val isMale = sharedPreferences.getBoolean("isMale", true)
            
            val genderIsMale = if (isMale) 1.0 else 0.0
            val defaultBehaviorClass = 3.0 // Using moderate behavior by default
            val osIsIos = 0.0 // Android app, so this is always 0.0

            val predictedDailyDrainMAh = predictDrain(
                appUsage = fetchedAppUsage,
                screenTime = fetchedScreenTime,
                numApps = fetchedNumApps,
                dataUsage = fetchedDataUsage,
                age = age,
                behaviorClass = defaultBehaviorClass,
                genderIsMale = genderIsMale,
                osIsIos = osIsIos
            )

            tvDailyDrain.text = "Predicted Drain: %.0f mAh/day".format(predictedDailyDrainMAh)

            // Calculate Remaining Time
            // Formula: (Current_Battery_Capacity / Drain_Per_Hour)
            val currentCapacityMAh = DEFAULT_BATTERY_CAPACITY_MAH * currentBatteryPct
            val drainPerHourMAh = predictedDailyDrainMAh / 24.0
            
            val hoursRemaining = if (drainPerHourMAh > 0) currentCapacityMAh / drainPerHourMAh else 0.0
            
            if (hoursRemaining > 0 && hoursRemaining < 100) { // arbitrary cap to avoid layout break
                val hours = hoursRemaining.toInt()
                val minutes = ((hoursRemaining - hours) * 60).toInt()
                tvRemainingTime.text = "${hours}h ${minutes}m"
            } else {
                tvRemainingTime.text = "--h --m"
            }
        }
        
        // Call Top Apps list generation
        populateTopAppsList()
    }

    private fun populateTopAppsList() {
        val container = findViewById<android.widget.LinearLayout>(R.id.llTopAppsContainer)
        container.removeAllViews()

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        // Query stats for the last 24 hours
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        
        if (stats == null || stats.isEmpty()) {
            return
        }

        // Filter and map down to package name -> total foreground time
        val packageUsageMap = mutableMapOf<String, Long>()
        for (usage in stats) {
            val packageName = usage.packageName
            val time = usage.totalTimeInForeground
            if (time > 0) {
                // Accumulate time in case of multiple entries for same package
                packageUsageMap[packageName] = packageUsageMap.getOrDefault(packageName, 0L) + time
            }
        }

        // Sort descending by time and take top 5
        val sortedUsage = packageUsageMap.toList()
            .sortedByDescending { (_, time) -> time }
            .take(5)

        val pm = packageManager
        var addedCount = 0

        for ((packageName, timeMs) in sortedUsage) {
            // Filter out self and system UI if possible
            if (packageName == this.packageName || packageName.contains("com.android.systemui")) continue
            if (addedCount >= 3) break // We strictly just want top 3 user apps

            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val appName = pm.getApplicationLabel(appInfo).toString()
                val appIcon = pm.getApplicationIcon(appInfo)

                // Layout inflation
                val view = layoutInflater.inflate(R.layout.item_top_app, container, false)
                
                val ivIcon = view.findViewById<android.widget.ImageView>(R.id.ivAppIcon)
                val tvName = view.findViewById<TextView>(R.id.tvAppName)
                val tvTime = view.findViewById<TextView>(R.id.tvAppUsageTime)

                ivIcon.setImageDrawable(appIcon)
                tvName.text = appName

                // Formulate string
                val totalMinutes = timeMs / (1000 * 60)
                if (totalMinutes > 60) {
                    val h = totalMinutes / 60
                    val m = totalMinutes % 60
                    tvTime.text = "${h}h ${m}m"
                } else {
                    tvTime.text = "${totalMinutes}m"
                }

                container.addView(view)
                addedCount++
            } catch (e: PackageManager.NameNotFoundException) {
                // App not found, skip
            }
        }
    }

    private fun predictDrain(
        appUsage: Double,
        screenTime: Double,
        numApps: Double,
        dataUsage: Double,
        age: Double,
        behaviorClass: Double,
        genderIsMale: Double,
        osIsIos: Double
    ): Double {
        val intercept = -112.1245825400506
        val bAppUsage = 0.4728849997647
        val bScreenTime = 13.911041935832795
        val bNumApps = 1.353342790122157
        val bDataUsage = 0.02938806991774966
        val bAge = -0.21161813843346342
        val bBehaviorClass = 471.19718393690164
        val bGenderMale = 3.586155014525511
        val bOsIos = 7.853044095674283

        return intercept +
               (bAppUsage * appUsage) +
               (bScreenTime * screenTime) +
               (bNumApps * numApps) +
               (bDataUsage * dataUsage) +
               (bAge * age) +
               (bBehaviorClass * behaviorClass) +
               (bGenderMale * genderIsMale) +
               (bOsIos * osIsIos)
    }
}
