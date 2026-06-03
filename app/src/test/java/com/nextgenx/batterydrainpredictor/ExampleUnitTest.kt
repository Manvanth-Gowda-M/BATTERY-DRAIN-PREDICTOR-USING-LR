package com.nextgenx.batterydrainpredictor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `predictDailyDrain returns positive value for normal input`() {
        val prediction = BatteryDrainPredictor.predictDailyDrainMAh(
            BatteryDrainPredictor.PredictionInput(
                appUsageMinutes = 320.0,
                screenTimeHours = 5.3,
                numApps = 120.0,
                dataUsageMb = 950.0,
                age = 23.0,
                behaviorClass = 3.0,
                genderIsMale = 1.0,
                osIsIos = 0.0
            )
        )

        assertTrue(prediction > 0.0)
    }

    @Test
    fun `predictDailyDrain clamps negative outputs to zero`() {
        val prediction = BatteryDrainPredictor.predictDailyDrainMAh(
            BatteryDrainPredictor.PredictionInput(
                appUsageMinutes = 0.0,
                screenTimeHours = 0.0,
                numApps = 0.0,
                dataUsageMb = 0.0,
                age = 120.0,
                behaviorClass = 0.0,
                genderIsMale = 0.0,
                osIsIos = 0.0
            )
        )

        assertEquals(0.0, prediction, 0.0)
    }

    @Test
    fun `estimateRemainingHours returns zero for invalid inputs`() {
        assertEquals(0.0, BatteryDrainPredictor.estimateRemainingHours(0f, 1000.0, 4000.0), 0.0)
        assertEquals(0.0, BatteryDrainPredictor.estimateRemainingHours(0.5f, 0.0, 4000.0), 0.0)
        assertEquals(0.0, BatteryDrainPredictor.estimateRemainingHours(0.5f, 1000.0, 0.0), 0.0)
    }
}