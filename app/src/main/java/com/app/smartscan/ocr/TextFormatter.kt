package com.app.smartscan.ocr

/**
 * Smart ingredient text formatter for OCR results.
 *
 *  - Detects the start of the ingredient list in multiple languages
 *  - Removes line breaks, numbers, and symbols
 *  - Cleans up duplicated spaces
 *  - Removes irrelevant parts like "May contain" or "Contains less than"
 *  - Formats each ingredient on its own line with bullets
 *
 */
fun formatIngredientsSmart(text: String): String {
    // 1. Define possible keywords for "ingredients" in multiple languages
    val keywords = listOf(
        "ingredients", "ingredienser", "ingrédients", "ingredientes", "ingrediens", "inhaltsstoffe"
    )

    // 2. Find where the ingredient section starts
    var detectedText = text
    for (keyword in keywords) {
        if (text.contains(keyword, ignoreCase = true)) {
            detectedText = text.substringAfter(keyword, text)
            break
        }
    }

    // 3. Clean and normalize the text
    var cleaned = detectedText
        .replace("\n", " ") // remove newlines
        .replace("\\s+".toRegex(), " ") // remove extra spaces
        .replace("(?i)may contain.*".toRegex(), "") // remove "may contain" parts
        .replace("(?i)contains less than.*".toRegex(), "")
        .replace("(?i)free from.*".toRegex(), "")
        .replace("[^a-zA-Z0-9ÅÄÖåäö,()\\s-]".toRegex(), "") // remove strange chars
        .trim()

    // 4. Split text by commas or semicolons
    val ingredients = cleaned
        .split(",", ";")
        .map { it.trim() }
        .filter { it.length > 1 && it.any { ch -> ch.isLetter() } } // keep only real words

    // 5. Format output
    return if (ingredients.isNotEmpty()) {
        ingredients.joinToString("\n• ", prefix = "• ")
    } else {
        "No clear ingredient list detected.\nRaw text:\n$text"
    }
}
