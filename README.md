# ClothingShop Android App  ![Android Build](https://github.com/YanchikFox/ClothingShop-Android/actions/workflows/android-build.yml/badge.svg)


This document describes how to set up and run the companion Android application that consumes the [ClothingShop API](https://github.com/YanchikFox/ClothingShop-API). The source lives in a separate repository: [`YanchikFox/ClothingShop-Android`](https://github.com/YanchikFox/ClothingShop-Android).

## Overview

The app is a Jetpack Compose client that lets shoppers browse the catalogue, search products, manage their cart, and log in or register with the backend service.

### Key features

- **Browse and search** – Home, catalogue, and search screens render data from `/api/products`, `/api/categories`, and `/api/search` endpoints.
- **Product details** – Users can inspect descriptions, pricing, and availability, and add items to the cart directly from the detail view.
- **Cart management** – Cart items sync with the backend via `POST /api/cart`, `PUT /api/cart/item/:id`, and `DELETE /api/cart/item/:id` requests.
- **Authentication** – Registration, login, logout, and profile retrieval powered by JWT tokens that are stored using DataStore.
- **Modern Android stack** – Compose for UI, Navigation Compose, ViewModel + StateFlow for state management, Retrofit + Gson for networking, Coil for image loading.

## Prerequisites

- Android Studio Koala (2024.1.1) or newer with Compose support
- Android SDK 24+ (target SDK 35)
- JDK 11

## Getting started

1. **Clone the repository**

   ```bash
   git clone https://github.com/YanchikFox/ClothingShop-Android.git
   cd ClothingShop-Android
   ```

2. **Open in Android Studio**

   - Use *File → Open…* and select the cloned folder.
   - Let Gradle finish syncing. Dependencies are managed via the Gradle Version Catalog (`libs.versions.toml`).

3. **Configure the backend endpoint**

   - Edit [`RetrofitInstance.kt`](https://github.com/YanchikFox/ClothingShop-Android/blob/main/app/src/main/java/com/shop/app/data/network/RetrofitInstance.kt).
   - Set `BASE_URL` to point to your running backend (e.g. `http://10.0.2.2:3000/` for the Android emulator, or an HTTPS ngrok tunnel for real devices).
   - Ensure the port matches the Express server (`3000` by default).

4. **Run the app**

   - Choose an emulator or physical device with API level 24 or higher.
   - Click *Run* (▶) in Android Studio.

## Project structure

```
app/
├─ src/main/java/com/shop/app/
│  ├─ data/                 # Retrofit API layer, repositories, models, DataStore
│  ├─ ui/components         # Reusable Compose UI widgets
│  ├─ ui/screens            # Feature screens (Home, Catalog, Cart, Auth, etc.)
│  ├─ ui/viewmodels         # ViewModel + StateFlow state holders
│  └─ ui/theme              # Theming, typography, colours
└─ build.gradle.kts         # Module configuration with Compose & Retrofit dependencies
```

## Environment tips

- Use the same seed data from `setup-database.js` when testing locally so the Android catalogue matches the backend inventory.
- When testing on a real device, expose the API with a tunnelling service (ngrok, Cloudflare Tunnel) and update `BASE_URL` accordingly.
- The client persists auth tokens with DataStore; clearing app storage forces a fresh login.

## Useful Gradle tasks

- `./gradlew assembleDebug` – build a debug APK
- `./gradlew lint` – run Android lint checks
- `./gradlew test` – execute unit tests (none defined yet but useful when added)

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Network calls fail with `ECONNREFUSED` | Ensure the backend is running and `BASE_URL` points to a reachable host/IP. |
| Images do not load | Confirm `BASE_IMAGE_URL` in `RetrofitInstance.kt` matches where product images are hosted. |
| JWT requests return 401 | Log in again to refresh the token, or reset server-side seed data to include the t
