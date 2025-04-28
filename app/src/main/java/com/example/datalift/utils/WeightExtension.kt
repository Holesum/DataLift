package com.example.datalift.utils

// Extension function to convert weight based on imperial/metric preference
fun Int.toDisplayWeight(isImperial: Boolean): Int =
    if (isImperial) this else (this * 0.453592).toInt()  // Convert lbs to kg if not imperial

fun Double.toDisplayWeight(isImperial: Boolean): Double =
    if (isImperial) this else (this * 0.453592).let { "%.2f".format(it).toDouble() }  // Convert lbs to kg if not imperial

fun Long.toDisplayWeight(isImperial: Boolean): Long =
    if (isImperial) this else (this * 0.453592).toLong()  // Convert lbs to kg if not imperial)

