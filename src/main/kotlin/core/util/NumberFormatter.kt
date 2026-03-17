package com.oussama_chatri.core.util

import java.text.DecimalFormat
import kotlin.math.abs

object NumberFormatter {


    /** Format to [decimals] decimal places. Default: 2 */
    fun format(value: Double, decimals: Int = 2): String {
        val pattern = if (decimals == 0) "#,##0" else "#,##0.${"0".repeat(decimals)}"
        return DecimalFormat(pattern).format(value)
    }

    /** Format an integer (no decimal) with thousands separator. */
    fun formatInt(value: Int): String = DecimalFormat("#,##0").format(value)

    /** Pressure in psi — 1 decimal place. e.g. "3,100.5 psi" */
    fun psi(value: Double, decimals: Int = 1): String =
        "${format(value, decimals)} psi"

    /** Mud weight / ECD in ppg — 2 decimal places. e.g. "10.85 ppg" */
    fun ppg(value: Double, decimals: Int = 2): String =
        "${format(value, decimals)} ppg"

    /** Depth in feet — 0 decimal places. e.g. "9,500 ft" */
    fun feet(value: Double, decimals: Int = 0): String =
        "${format(value, decimals)} ft"

    /** Depth in meters. e.g. "2,895.6 m" */
    fun meters(value: Double, decimals: Int = 1): String =
        "${format(value, decimals)} m"

    /** Flow rate in gpm. e.g. "400 gpm" */
    fun gpm(value: Double, decimals: Int = 0): String =
        "${format(value, decimals)} gpm"

    /** Velocity in ft/min. e.g. "185.3 ft/min" */
    fun ftPerMin(value: Double, decimals: Int = 1): String =
        "${format(value, decimals)} ft/min"

    /** Viscosity in cP. e.g. "18 cP" */
    fun cP(value: Double, decimals: Int = 0): String =
        "${format(value, decimals)} cP"

    /** Inches — bit size, pipe OD/ID. e.g. "8.500 in" */
    fun inches(value: Double, decimals: Int = 3): String =
        "${format(value, decimals)} in"

    /** Horsepower. e.g. "1,240 hp" */
    fun hp(value: Double, decimals: Int = 0): String =
        "${format(value, decimals)} hp"

    /** HSI — hp/in². e.g. "1.25 hp/in²" */
    fun hsi(value: Double, decimals: Int = 2): String =
        "${format(value, decimals)} hp/in²"

    /** Force in lbf. e.g. "2,450 lbf" */
    fun lbf(value: Double, decimals: Int = 0): String =
        "${format(value, decimals)} lbf"

    /** Area in in². e.g. "0.452 in²" */
    fun squareInches(value: Double, decimals: Int = 3): String =
        "${format(value, decimals)} in²"

    /** Temperature in °F. e.g. "220 °F" */
    fun fahrenheit(value: Double, decimals: Int = 0): String =
        "${format(value, decimals)} °F"

    /** Temperature in °C. */
    fun celsius(value: Double, decimals: Int = 0): String =
        "${format(value, decimals)} °C"

    /** Percentage. e.g. "12.5%" */
    fun percent(value: Double, decimals: Int = 1): String =
        "${format(value, decimals)}%"

    /** Angle in degrees. e.g. "45.0°" */
    fun degrees(value: Double, decimals: Int = 1): String =
        "${format(value, decimals)}°"

    /**
     * Compacts large numbers for dashboard stat cards:
     * - >= 1,000,000 → "1.2M"
     * - >= 1,000     → "4.5K"
     * - else         → normal format
     */
    fun compact(value: Double): String = when {
        abs(value) >= 1_000_000 -> "${format(value / 1_000_000, 1)}M"
        abs(value) >= 1_000     -> "${format(value / 1_000, 1)}K"
        else                    -> format(value, 1)
    }
}