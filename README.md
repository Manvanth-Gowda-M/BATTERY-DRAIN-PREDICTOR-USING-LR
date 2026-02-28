# Battery Drain Predictor 🔋

An intelligent Android application that predicts your device's daily battery drain based on your specific usage patterns, screen time, and hardware health.

## 🌟 Features

*   **Smart Prediction Engine:** Uses a Machine Learning model (Linear Regression) trained on user behavior datasets to accurately forecast your battery consumption in mAh.
*   **Real-time Device Stats:**
    *   📱 **App Usage Tracking:** Monitors how long you actively use your apps throughout the day.
    *   ⏱️ **Screen Time:** Keeps track of your daily screen-on time.
    *   📶 **Data Usage:** Measures your daily mobile and Wi-Fi data consumption.
*   **Hardware Health Monitoring:** Displays real-time battery temperature, voltage, and overall battery health status.
*   **Dynamic UI:**
    *   Sleek, modern dashboard with smooth animations.
    *   Automatically switches the hero card display to "**Time Until Full**" when the device is plugged in, and back to "**Estimated Remaining Time**" when unplugged.
*   **Top Drainers List:** Identifies and scrolls through the specific apps consuming the most battery power on your device.

## 📸 Screenshots

*(You can drag and drop screenshots of your app's dashboard and onboarding screens here later!)*

## 🚀 Getting Started

### Prerequisites

*   Android Studio (latest version recommended)
*   Android device or emulator running Android 8.0 (Oreo, API 26) or higher.

### Installation

1.  Clone this repository:
    ```bash
    git clone https://github.com/Manvanth-Gowda-M/BATTERY-DRAIN-PREDICTOR-USING-LR.git
    ```
2.  Open the project in **Android Studio**.
3.  Allow Android Studio to sync the Gradle project and download any necessary dependencies.
4.  Connect your Android device (with USB debugging enabled) or start an emulator.
5.  Click the **Run** button to build and install the app.

### Permissions Required

To function correctly securely, the app requires the following permissions upon first launch:
*   **Usage Access (`PACKAGE_USAGE_STATS`):** Required to fetch accurate application usage time and screen time.
*   *(Note: The app will safely prompt you to grant this permission during the Onboarding flow.)*

## 🛠️ Built With

*   **Kotlin** - The primary programming language used.
*   **AndroidX** - Core libraries for UI and lifecycle management.
*   **Material Components** - UI components for a modern, attractive Material Design layout.
*   **Scikit-Learn (Python)** - Used to train the underlying Linear Regression predictive model.

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check the [issues page](https://github.com/Manvanth-Gowda-M/BATTERY-DRAIN-PREDICTOR-USING-LR/issues) if you want to contribute.
