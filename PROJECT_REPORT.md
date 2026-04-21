# BATTERY DRAIN PREDICTOR USING LINEAR REGRESSION

## Project Report

Submitted in partial fulfillment of the requirements for the award of the degree of  
**Bachelor of Engineering / Bachelor of Technology**  
in  
**[Your Branch Name]**

Submitted by: **[Your Name]**  
USN: **[Your USN]**  
Guide: **[Guide Name]**  
Department of **[Department Name]**  
**[College Name]**, **[University Name]**  
Academic Year: **2025–26**

---

## Certificate
This is to certify that the project work titled **“Battery Drain Predictor Using Linear Regression”** is a bonafide work carried out by **[Your Name]** in partial fulfillment of the requirements for the degree of **[Degree Name]** during the academic year **2025–26** under my guidance.

Guide Signature: __________  
HOD Signature: __________  
Principal Signature: __________

---

## Declaration
I hereby declare that the project report titled **“Battery Drain Predictor Using Linear Regression”** is my original work and has not been submitted to any other university/institution for the award of any degree.

Place: _______  
Date: _______  
Signature: _______

---

## Acknowledgement
I sincerely thank our Principal, Head of the Department, and my project guide **[Guide Name]** for their valuable guidance and support. I also thank all faculty members, friends, and family for their encouragement throughout the project.

---

## Abstract
This project presents an Android application that predicts daily battery drain using a **Linear Regression** model. The app collects user/device parameters such as app usage time, screen-on duration, data usage, and battery health indicators (temperature, voltage, and health state). Using these features, the model estimates battery consumption and remaining battery behavior.  
The system provides a user-friendly dashboard with real-time statistics and a top battery drainer list. The project demonstrates that machine learning can support practical battery optimization by offering users predictive insights into usage patterns and power consumption.

---

## Chapter 1: Introduction
Smartphone users often face unexpected battery drain due to varying usage behavior and background applications. This project aims to solve this by predicting battery drain using historical and real-time usage factors.  
The solution is implemented as an Android app with a machine learning-based prediction engine.

### Objectives
1. To collect relevant battery and usage parameters from the device.  
2. To build a Linear Regression model for battery drain prediction.  
3. To display prediction and battery insights through an intuitive UI.  
4. To help users identify high battery-consuming applications.

### Scope
- Android-based implementation.  
- Real-time monitoring and prediction visualization.  
- User-level analysis of battery drain behavior.

---

## Chapter 2: Literature Survey
Existing battery monitoring systems mainly provide current status but not predictive insights. Prior studies show that machine learning models, especially regression-based methods, can estimate battery consumption from behavioral and system-level features.  
Linear Regression is selected in this project because it is simple, interpretable, and computationally efficient for mobile-oriented prediction tasks.

---

## Chapter 3: Methodology

### 3.1 Data/Features Used
- App usage duration  
- Screen-on time  
- Mobile/Wi-Fi data usage  
- Battery temperature  
- Battery voltage  
- Battery health status

### 3.2 Model
A **Linear Regression** model is trained (using Python/Scikit-learn) on battery-related usage data. The trained logic is used in the Android app workflow to estimate battery drain.

### 3.3 System Flow
1. Collect device usage and battery metrics  
2. Preprocess/normalize input values  
3. Apply regression equation/model  
4. Display predicted drain and related insights in UI

### 3.4 Tools & Technologies
- Kotlin (Android app development)  
- Android Studio  
- AndroidX + Material Components  
- Scikit-learn (model development)

---

## Chapter 4: System Design and Implementation
The app consists of:
1. **Onboarding Module** – permission guidance (usage access).  
2. **Data Collection Module** – fetches usage and battery statistics.  
3. **Prediction Module** – computes battery drain estimate.  
4. **Dashboard Module** – displays prediction, battery health, and top drainers.

The UI includes dynamic cards, smooth transitions, and clear battery analytics to improve usability.

---

## Chapter 5: Results and Discussion
The application predicts battery drain trends based on daily usage parameters. It also helps users understand which factors contribute most to battery loss.

Observed outcomes:
- Better awareness of high-drain apps  
- Improved user decisions for battery optimization  
- Practical use of lightweight ML on mobile-centric data

> Add your measured metrics here if available: **MAE, RMSE, R²**, screenshots, and test cases.

---

## Chapter 6: Conclusion and Future Work
The project demonstrates a practical approach to battery drain prediction using Linear Regression. The model and Android implementation provide useful battery insights with low complexity.

Future improvements:
- Use advanced models (Random Forest/XGBoost/Neural Networks)  
- Personalized per-user adaptive model  
- Cloud sync and long-term battery trend analytics  
- Automatic optimization recommendations

---

## References
1. Scikit-learn Documentation: https://scikit-learn.org  
2. Android Developers Documentation: https://developer.android.com  
3. Research papers on smartphone battery consumption modeling.

---

## Appendix
- App screenshots  
- Sample input/output records  
- Important code snippets (prediction and data collection modules)
