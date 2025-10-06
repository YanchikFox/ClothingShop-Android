# ClothingShop Android App ![Android Build](https://github.com/YanchikFox/ClothingShop-Android/actions/workflows/android-build.yml/badge.svg)

This repository contains the Jetpack Compose client for the [ClothingShop platform](https://github.com/YanchikFox/ClothingShop-API). The Android app lets customers browse the catalogue, manage their cart, and complete checkout flows against the companion backend service.

## Table of contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Getting started](#getting-started)
- [Configuring API endpoints](#configuring-api-endpoints)
- [Project structure](#project-structure)
- [Running checks](#running-checks)
- [Environment tips](#environment-tips)
- [Localization](#localization)
- [Troubleshooting](#troubleshooting)

## Overview

The app is written with the modern Android toolkit:

- **Jetpack Compose** for declarative UI
- **Navigation Compose** for in-app routing
- **ViewModel + StateFlow** for reactive state management
- **Retrofit + Gson** for networking and serialization
- **Coil** for efficient image loading
- **DataStore** for persisting authentication tokens and preferences

### Core features

- Browse and search the product catalogue via `/api/products`, `/api/categories`, and `/api/search`
- Inspect product details, add items to the cart, and adjust quantities
- Register, sign in, and maintain authenticated sessions with JWT tokens
- Persist user-selected language settings across sessions

## Prerequisites

Make sure the following tooling is installed locally:

- Android Studio Koala (2024.1.1) or newer with Compose support
- Android SDK 24+ (target SDK 35)
- JDK 11
- Access to a running instance of the ClothingShop backend API

## Getting started

1. **Clone the repository**
   ```bash
   git clone https://github.com/YanchikFox/ClothingShop-Android.git
   cd ClothingShop-Android
   ```
2. **Open in Android Studio**
   - Choose *File → Open…* and select the cloned project folder.
   - Allow Gradle to finish syncing before running the app.
3. **Sync the backend**
   - Start the [ClothingShop API](https://github.com/YanchikFox/ClothingShop-API) locally or expose it through a tunnel for devices.
   - Seed the database with the provided scripts so product data and translations match the mobile client expectations.
4. **Run the app**
   - Select an emulator or physical device with API level 24 or higher.
   - Press ▶ in Android Studio or run `./gradlew installDebug` from the command line.

## Configuring API endpoints

Endpoint URLs are supplied via Gradle build config fields. Update the values in [`app/build.gradle.kts`](app/build.gradle.kts) under the `buildConfigField` declarations:

```kotlin
defaultConfig {
    buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:3000/\"")
    buildConfigField("String", "IMAGES_BASE_URL", "\"http://10.0.2.2:3000/images/\"")
}
```

- Use `10.0.2.2` for local emulators, or the LAN/tunnel address for real devices.
- Sync Gradle after changes so the generated `BuildConfig` is updated.

## Project structure

```
app/
├─ src/main/java/com/shop/app/
│  ├─ data/           # Retrofit services, repositories, DataStore
│  ├─ ui/components   # Reusable Compose widgets
│  ├─ ui/screens      # Feature screens (Home, Catalog, Cart, Auth, etc.)
│  ├─ ui/viewmodels   # ViewModels that expose StateFlow to the UI
│  └─ ui/theme        # Typography, colours, shapes
└─ build.gradle.kts   # Module configuration with Compose & Retrofit dependencies
```

## Running checks

You can execute the most common checks from the command line:

```bash
./gradlew assembleDebug   # Build a debug APK
./gradlew lint            # Run Android Lint
./gradlew test            # Execute unit tests (add tests as needed)
```

## Environment tips

- Align backend seed data with the mobile app so product translations and inventory are in sync.
- The client automatically sends the preferred language in the `Accept-Language` header; make sure the backend respects it.
- When testing on a physical device, expose the backend via ngrok or Cloudflare Tunnel and update the URLs accordingly.
- Clearing app storage removes persisted DataStore entries and forces a fresh login.

## Localization

- Language preferences are stored in DataStore and propagated to both the UI and network layer.
- Switch between English, Russian, and Ukrainian within the in-app settings without restarting the app.
- If server responses stay in English, reseed the backend with translated data.

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Network calls fail with `ECONNREFUSED` | Confirm the backend is running and the `API_BASE_URL` points to a reachable host/IP. |
| Images do not load | Ensure `IMAGES_BASE_URL` matches the server path that exposes product images. |
| JWT requests return 401 | Log in again to refresh the token or reseed server-side accounts. |
