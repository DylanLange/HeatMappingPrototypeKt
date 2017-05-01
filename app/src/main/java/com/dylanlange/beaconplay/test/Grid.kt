package com.dylanlange.beaconplay.test

/**
 * Created by dylanlange on 1/05/17.
 */

data class Grid(
        val width: Double,
        val height: Double,
        var beaconCoords: Coord
)

data class Coord(
        var x: Double,
        var y: Double
)