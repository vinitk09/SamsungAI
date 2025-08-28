# On-Device Multi-Agent System for Behavior-Based Anomaly & Fraud Detection

## Overview

This document provides a detailed technical overview of the On-Device Multi-Agent System for Behavior-Based Anomaly & Fraud Detection.

> **Note**: This is an early-stage implementation designed for small-scale data collection and analysis. Some minor bugs may be present as we continue to refine and expand the system.

## 1. Our Approach and Its Uniqueness

The core of our solution is a decentralized, privacy-first, multi-agent system that runs entirely on the user's device. Unlike traditional security solutions that rely on cloud-based analysis, our approach ensures that sensitive user data never leaves the phone.

**What makes our approach unique?**

- **Multi-Agent Architecture**: Instead of a single, monolithic application, we use a collection of small, independent "agents." Each agent has a single responsibility (e.g., monitoring touch events, analyzing data). This makes the system highly modular, resilient, and easy to extend.

- **Continuous Behavioral Profiling**: The system doesn't rely on static signatures. It continuously learns the user's unique behavioral biometrics—their typing rhythm, how they hold their device, their swipe speed, and touch pressure—to create a dynamic profile that evolves over time.

- **Hybrid Detection Model**: We combine two layers of security:
  - **Rule-Based Heuristics**: A fast, efficient layer that checks for clear-cut anomalies (e.g., inhumanly fast typing, accessing a banking app at 3 AM).
  - **Machine Learning Inference**: A sophisticated layer that uses a pre-trained TensorFlow Lite autoencoder model to detect subtle deviations from the user's learned profile that a human programmer might miss.

- **On-Device Privacy**: All data collection, profiling, and analysis happen locally. This guarantees user privacy and allows the system to function perfectly without an internet connection.

## 2. Technical Stack

Our project is built using modern, industry-standard tools for Android development.

- **Programming Language**: Kotlin - For modern, concise, and safe Android development.
- **User Interface**: Jetpack Compose - For building the native UI with a declarative approach.
- **Asynchronous Communication**: GreenRobot EventBus - A lightweight publish/subscribe event bus used for communication between the different agents.
- **Machine Learning**: TensorFlow Lite (TFLite) - For on-device inference using our pre-trained anomaly detection model.
- **Data Persistence**: Gson - For serializing and deserializing user interaction data to local JSON files for persistence.

## 3. Technical Architecture

The system is architected as a decentralized network of agents communicating via an Event Bus. This decoupled design ensures that agents can operate independently.

**Core Components:**

- **Data Collection Agents (The Sensors)**:
  - `MovementAgent`: Listens to the accelerometer and gyroscope to capture device motion.
  - `AppUsageAgent`: Uses the UsageStatsManager to track which app is in the foreground.
  - `TouchAgent`: Deploys a clever 1x1 pixel system-wide overlay to capture all touch events (pressure, coordinates) without blocking the UI.
  - `MasAccessibilityService`: Acts as the Typing Agent, capturing the time between keystrokes (inter-key latency) across all apps.

- **Analysis & Profiling Agent (The Brain)**:
  - `DataAnalysisAgent`: Subscribes to events from all collection agents. It aggregates this data into rolling lists and calculates statistical measures (average, standard deviation) to build a UserProfile. It also contains the rule-based detection logic.

- **ML Inference Agents (The Specialist)**:
  - `FeatureExtractionAgent`: Converts the UserProfile object into a normalized numerical array (feature vector) that the ML model can understand.
  - `ModelingAgent`: Loads the model.tflite file and runs the feature vector through it to get an "anomaly score."
  - `AnomalyDetectionAgent`: Orchestrates the ML pipeline by passing the feature vector to the ModelingAgent and broadcasting the resulting score.

- **UI (The Dashboard)**:
  - `MainActivity`: Provides the user with a dashboard to view agent statuses, grant necessary permissions, and see any detected anomaly alerts.

## 4. Implementation Details

- **Event-Driven Communication**: Agents do not call each other directly. For instance, when the TouchAgent captures a touch, it posts a TouchEvent to the EventBus. The DataAnalysisAgent, being a subscriber, receives this event and updates its internal state. This keeps the system clean and scalable.

- **User Profile Creation**: The DataAnalysisAgent requires a certain number of data points (`minDataPointsForBaseline`) before it considers the user's profile "established." Before this, no anomalies are flagged. This initial learning period ensures the baseline is accurate.

- **Anomaly Scoring**: The TFLite model is an autoencoder. It's trained to "reconstruct" feature vectors of normal user behavior. When a feature vector from an imposter or a bot is passed in, the model struggles to reconstruct it accurately. The difference between the original vector and the reconstructed one is the reconstruction error, which we use as our anomaly score. A higher score means a higher probability of an anomaly.

## 5. Installation Instructions

**Prerequisites:**

- Android Studio (latest version recommended).
- An Android device running Android 8.0 (Oreo) or higher.

**Setup:**

1. Clone the repository: `git clone <your-repo-link>`
2. Open the project in Android Studio.
3. Ensure the TensorFlow Lite model file, `model.tflite`, is located in the `app/src/main/assets/` directory.
4. Build the project (Build > Make Project).

**Running the Application:**

1. Run the app on a physical Android device.
2. The main dashboard will appear, showing the status of each agent.

## 6. User Guide

- **Granting Permissions**: Upon first launch, the app will require three critical permissions. Use the buttons on the dashboard to grant them:
  - **Usage Access**: Allows the AppUsageAgent to see the foreground app.
  - **Draw Over Other Apps**: Allows the TouchAgent to capture screen-wide touch events.
  - **Accessibility Service**: Allows the MasAccessibilityService to capture typing dynamics.

- **Learning Phase**: After granting permissions, use your phone normally. The app will enter a learning phase to establish your baseline behavioral profile. The "Learned User Profile" card will show a "⏳" icon during this time.

- **Active Monitoring**: Once the baseline is established (the icon changes to "✅"), the system is in active monitoring mode. It will silently analyze your behavior in the background.

- **Viewing Alerts**: If an anomaly is detected, a prominent red card will appear at the top of the dashboard explaining the reason for the alert.

## 7. Salient Features

- Comprehensive Behavioral Tracking: Monitors typing, touch, swipe, and movement biometrics.
- Context-Aware Detection: Correlates behavioral data with contextual information, like the app being used and the time of day.
- Dual-Layer Security: Combines fast rule-based checks with deep ML-based analysis for robust detection.
- Complete On-Device Operation: Ensures 100% user privacy with no data leaving the device.
- Real-Time Alerts: Instantly notifies the user of suspicious activity through the in-app dashboard.
- Transparent Dashboard: Allows the user to see exactly what the system is monitoring and the profile it has learned.
