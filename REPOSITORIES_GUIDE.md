# Repositories Guide (Backend Contract)

This document serves as the official contract between the backend/data layer and the frontend/UI layer. It details how to interact with the application's data using the provided repository classes and cloud functions.

## Core Principle

The UI (Composable screens) should **not** talk to Firebase directly. Instead, the UI should talk to a `ViewModel`, and the `ViewModel` will use these repositories to get or save data.

## Setup

To use any repository, you will first need an instance of it. In a ViewModel, you would get these instances like this:

```kotlin
// Inside your ViewModel class

// Get instances of the Firebase services
private val auth = FirebaseAuth.getInstance()
private val db = FirebaseFirestore.getInstance()
private val functions = FirebaseFunctions.getInstance()

// Create the repositories
private val authRepository = AuthRepository(auth, db)
private val userRepository = UserRepository(db)
private val productRepository = ProductRepository(db)
private val scanRepository = ScanRepository(db)
```

---

## 1. AuthRepository

**Purpose:** Handles all user authentication tasks (sign-up, sign-in, sign-out).

(Content unchanged...)

---

## 2. UserRepository

**Purpose:** Manages user profile data (skin type, allergies, etc.).

(Content unchanged...)

---

## 3. ProductRepository

**Purpose:** Fetches product information.

(Content unchanged...)

---

## 4. ScanRepository

**Purpose:** Creates new scan records and observes them for real-time updates.

(Content unchanged...)

---

## 5. Cloud Functions

**Purpose:** To trigger complex backend operations that shouldn't be run on the client, like calling third-party APIs.

### Functions

#### `fetchProductFromOBF`

Takes a product `barcode` and fetches its data from the OpenBeautyFacts API, saving the result to our own Firestore `products` collection. This allows us to cache the data and reduce our reliance on the external API.

**Function Name in Code:** `fetchProductFromOBF`

**Sample Call (from a ViewModel):**

```kotlin
viewModelScope.launch {
    try {
        // The data is a simple map.
        val data = mapOf("barcode" to "3337872411991")

        // Call the function by its name
        val result = functions.getHttpsCallable("fetchProductFromOBF").call(data).await()
        
        // The result will be a map, e.g., {found=true, source=obf}
        val resultMap = result.data as? Map<*, *>
        // Update UI with the result

    } catch (e: Exception) {
        // Handle errors, e.g., product not found or network issues
    }
}
```
