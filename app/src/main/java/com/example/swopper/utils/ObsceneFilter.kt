package com.example.swopper.utils

import com.example.swopper.R
import kotlin.math.ceil
import kotlin.math.floor

val forbidden: List<String> = listOf(*APP_ACTIVITY.resources.getStringArray(R.array.obstences))

enum class Filters {
    LEVENSHTEIN, BITAP
}

val replaces: Map<String, List<String>> = mapOf(
    "а" to listOf("а", "a", "@"),
    "б" to listOf("б", "b", "6"),
    "в" to listOf("в", "v"),
    "г" to listOf("г", "g"),
    "д" to listOf("д", "d"),
    "е" to listOf("е", "e"),
    "ж" to listOf("ж", "zh", "*"),
    "з" to listOf("з", "z", "3"),
    "и" to listOf("и", "i"),
    "к" to listOf("к", "k", "i{", "|{"),
    "л" to listOf("л", "l", "ji"),
    "м" to listOf("м", "m"),
    "н" to listOf("н", "n"),
    "о" to listOf("о", "o", "0"),
    "п" to listOf("п", "p"),
    "р" to listOf("р", "r"),
    "с" to listOf("с", "s", "c"),
    "т" to listOf("т", "t"),
    "у" to listOf("у", "u", "y"),
    "ф" to listOf("ф", "f"),
    "х" to listOf("х", "x", "h", ")(", "}{"),
    "ц" to listOf("ц", "u,"),
    "ч" to listOf("ч", "ch"),
    "ш" to listOf("ш", "sh"),
    "щ" to listOf("щ", "sch"),
    "ъ" to listOf("ъ"),
    "ы" to listOf("ы", "bi", "b|"),
    "ь" to listOf("ь"),
    "э" to listOf("э"),
    "ю" to listOf("ю", "io"),
    "я" to listOf("я", "ya")
)

private fun min(a: Int, b: Int, c: Int): Int {
    return a.coerceAtMost(b).coerceAtMost(c)
}

private fun levenshtein(input: String, pattern: String, limit: Int): Boolean {
    val dist = IntArray(input.length + 1)
    val dist1 = IntArray(input.length + 1)
    for (j in 0..input.length) {
        dist[j] = j
    }
    for (i in 1..pattern.length) {
        System.arraycopy(dist, 0, dist1, 0, dist1.size)
        dist[0] = i
        for (j in 1..input.length) {
            val cost = if (pattern[i - 1] != input[j - 1]) 1 else 0
            dist[j] = min(
                dist1[j] + 1,
                dist[j - 1] + 1,
                dist1[j - 1] + cost
            )
        }
    }
    return dist[dist.size - 1] <= limit
}

fun bitap(input: String, pattern: String, limit: Int): Boolean {
    var result = -1

    val R = LongArray(limit + 1)
    for (i in 0..limit) {
        R[i] = 1
    }

    val patternMask = LongArray(1104)
    for (i in pattern.indices) {
        patternMask[pattern[i].toInt()] = patternMask[pattern[i].toInt()] or (1 shl i).toLong()
    }

    var count = 0
    while (input.length > count) {
        var old: Long = 0
        var nextOld: Long = 0
        for (d in 0..limit) {
            val replace = old or (R[d] and patternMask[input[count].toInt()]) shl 1
            val insert = old or (R[d] and patternMask[input[count].toInt()] shl 1)
            val delete = nextOld or (R[d] and patternMask[input[count].toInt()]) shl 1
            old = R[d]
            R[d] = replace or insert or delete or 1
            nextOld = R[d]
        }
        if (0 < R[limit] and (1 shl pattern.length).toLong()) {
            if (result == -1 || count - result > pattern.length) {
                result = count
            }
        }
        count++
    }
    return result != -1
}

fun search(phrase: String, filter: Filters = Filters.BITAP): Boolean {
    var input =
        phrase.toLowerCase()
            .replace(" ", "")
            .replace("_", "")
            .replace(".", "")
            .replace("-", "")
            .replace("ё", "е")

    replaces.forEach { (key, value) -> value.forEach { item -> input = input.replace(item, key) } }

    for (word in forbidden) {
        for (i in 0..input.length - word.length) {
            val searched = when (filter) {
                Filters.LEVENSHTEIN -> levenshtein(
                    input.substring(i, i + word.length - 1),
                    word,
                    floor(0.25 * word.length).toInt()
                )
                Filters.BITAP -> bitap(input.substring(i, i + word.length - 1), word, floor(0.2 * word.length).toInt())
            }
            if (searched) {
                return true
            }
        }
    }
    return false
}