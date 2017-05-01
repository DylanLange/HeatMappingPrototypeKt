package com.dylanlange.beaconplay

import java.text.DecimalFormat

/**
 * Created by dylanlange on 1/05/17.
 */

fun Double.toTwoDp(): Double{
    val df = DecimalFormat("#.00")
    return df.format(this).toDouble()
}