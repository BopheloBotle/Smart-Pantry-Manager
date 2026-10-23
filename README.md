# Smart Pantry Manager (BlushPantry)

A Java Android app for a Mobile App Development 700 practical. It keeps a local pantry in SQLite and suggests meals **only** when every required ingredient is already in the pantry in a sufficient quantity.

The app works fully offline. It does not use maps, GPS, grocery-store search, or the internet.

## Features

- Home dashboard with pantry count, makeable recipe count, and optional expiry reminders
- Pantry list with add, edit, and delete
- Shared add/edit ingredient form with validation
- Suggested recipes that use **strict matching** (no partial matches)
- Recipe detail with ingredients, times, and step-by-step instructions
- Settings stored in SharedPreferences (expiry reminders and preferred units)
- 22 everyday recipes seeded the first time the database is created
- Blush-pink Material theme with bottom navigation

## Technology stack

- Java only (no Kotlin)
- XML layouts and Java Activities
- SQLite via `SQLiteOpenHelper`
- RecyclerView with custom Java adapters
- Explicit Intents between screens
- SharedPreferences for settings
- JUnit 4 unit tests for matching, units, and dates
- Minimum SDK 24, target SDK 35 (runs on Android 13 and 14)

## SQLite database

`DatabaseHelper` creates three tables:

| Table | Purpose |
| --- | --- |
| `pantry_items` | User ingredients: name, quantity, unit, optional expiry |
| `recipes` | Recipe name, description, prep/cook time, instructions |
| `recipe_ingredients` | Required ingredients for each recipe |

CRUD is implemented for pantry items. Recipes are read-only after seeding.

Data stays on the device after the app is closed. Uninstalling the app deletes the database.

## Database seeding

Recipes are inserted only inside `onCreate()` of `SQLiteOpenHelper`. Android calls that method **once**, when `blush_pantry.db` does not exist yet.

Later launches open the existing file. The seeder does not run again, and pantry rows are never deleted by seeding.

To reseed recipes during marking, uninstall the app or clear its storage, then install again. That also clears pantry data.

## Strict-matching algorithm

`RecipeMatcher` decides which recipes appear on Suggested Recipes.

1. Normalize every pantry name (`tomato` / `Tomatoes` → `tomato`).
2. Group pantry rows by that normalized name.
3. For a recipe to appear, **every** required ingredient must pass.
4. For each required line, convert pantry quantities into the recipe unit when the units are compatible (g↔kg, ml↔l, tsp↔tbsp↔cup).
5. Sum the converted amount. It must be at least the required quantity.
6. If a name is missing, a quantity is too low, or units cannot be compared safely (for example `piece` vs `g`), the recipe is excluded.

Normalization is not substring matching. `onion` does not match `spring onion`.

There is no "Almost There" list, so close matches cannot leak into suggestions.

## Android Studio setup

1. Install [Android Studio](https://developer.android.com/studio) with JDK 17+ and Android SDK 35.
2. Clone this repository and open the folder in Android Studio.
3. Let Gradle sync. Create `local.properties` automatically, or add:
   `sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk`
4. Create an emulator on API 33 or 34, or plug in a phone with USB debugging.
5. Press Run.

### One-command start (Git Bash / macOS / Linux)

```bash
chmod +x start.sh gradlew
./start.sh
```

`start.sh` locates the Android SDK, writes `local.properties`, runs the unit tests, builds the debug APK, and installs it if a device or emulator is connected.

On Windows PowerShell without Git Bash:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

You still need the Android SDK for that Gradle command.

## Testing

### Unit tests (strict matching)

In Android Studio: **Gradle → app → Tasks → verification → testDebugUnitTest**

Or:

```bash
./gradlew testDebugUnitTest
```

Covered cases:

- Full match when every ingredient and quantity is present
- Missing ingredient → no match
- Insufficient quantity → no match
- Singular/plural names (`egg` / `eggs`, `tomato` / `tomatoes`)
- Empty pantry
- Compatible unit conversion (g/kg, ml/l)
- Incompatible units are not treated as a match
- No substring matching

### Manual tests for the demo

1. Add a pantry item and confirm it appears in the list.
2. Edit it and confirm the row updates.
3. Delete it and confirm the empty state if nothing remains.
4. Force-stop the app and reopen it: pantry data is still there.
5. Add `3 piece egg` and `2 piece tomato` (and any other required items for a recipe) and confirm that recipe appears.
6. Remove tomatoes: the recipe disappears.
7. Set tomatoes to `1 piece`: the recipe stays hidden.
8. Add `tomatoes` instead of `tomato`: matching still works.
9. Open a suggestion: recipe details load through an Intent with the recipe ID.
10. Change Settings, restart the app, confirm the toggle is still set.
11. Submit the add form with a blank name or `0` quantity: it must not save.

### Demo pantry that unlocks several meals

Use the same units the recipes use:

- 4 piece egg
- 3 piece tomato
- 4 slice bread
- 50 g butter
- 80 g cheese
- 1 tsp salt
- 2 clove garlic
- 150 g pasta
- 15 ml oil

That combination can unlock meals such as Scrambled Eggs, Cheese Toast, Garlic Butter Pasta, and Tomato Cheese Toast.

## Project structure

```
app/src/main/java/com/bophelobotle/blushpantry/
  MainActivity.java
  PantryActivity.java
  AddEditIngredientActivity.java
  SuggestedRecipesActivity.java
  RecipeDetailActivity.java
  SettingsActivity.java
  database/DatabaseHelper.java
  database/SeedData.java
  models/
  adapters/
  utils/RecipeMatcher.java
  utils/UnitConverter.java
  utils/IngredientNormalizer.java
```

## Assignment notes

- No Google Maps, location, or store-discovery features
- No Kotlin
- No extra backend
- Do not claim a Gradle Android build passed unless `./start.sh` or Android Studio actually compiled the app on a machine that has the SDK
