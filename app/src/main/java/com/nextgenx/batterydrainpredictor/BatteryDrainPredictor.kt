package com.nextgenx.batterydrainpredictor

object BatteryDrainPredictor {

    data class PredictionInput(
        val appUsageMinutes: Double,
        val screenTimeHours: Double,
        val numApps: Double,
        val dataUsageMb: Double,
        val age: Double,
        val behaviorClass: Double,
        val genderIsMale: Double,
        val osIsIos: Double
    )

    private const val INTERCEPT = -112.1245825400506
    private const val B_APP_USAGE = 0.4728849997647
    private const val B_SCREEN_TIME = 13.911041935832795
    private const val B_NUM_APPS = 1.353342790122157
    private const val B_DATA_USAGE = 0.02938806991774966
    private const val B_AGE = -0.21161813843346342
    private const val B_BEHAVIOR_CLASS = 471.19718393690164
    private const val B_GENDER_MALE = 3.586155014525511
    private const val B_OS_IOS = 7.853044095674283

    fun predictDailyDrainMAh(input: PredictionInput): Double {
        val raw = INTERCEPT +
                (B_APP_USAGE * input.appUsageMinutes) +
                (B_SCREEN_TIME * input.screenTimeHours) +
                (B_NUM_APPS * input.numApps) +
                (B_DATA_USAGE * input.dataUsageMb) +
                (B_AGE * input.age) +
                (B_BEHAVIOR_CLASS * input.behaviorClass) +
                (B_GENDER_MALE * input.genderIsMale) +
                (B_OS_IOS * input.osIsIos)

        return raw.coerceAtLeast(0.0)
    }

    fun estimateRemainingHours(
        batteryPercentage: Float,
        predictedDailyDrainMAh: Double,
        batteryCapacityMAh: Double
    ): Double {
        if (batteryPercentage <= 0f || batteryCapacityMAh <= 0.0 || predictedDailyDrainMAh <= 0.0) {
            return 0.0
        }
        val currentCapacity = batteryCapacityMAh * batteryPercentage
        val drainPerHour = predictedDailyDrainMAh / 24.0
        return if (drainPerHour > 0.0) currentCapacity / drainPerHour else 0.0
    }
}
