package com.dylanlange.beaconplay.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dylanlange.beaconplay.R
import kotlinx.android.synthetic.main.activity_test.*




/**
 * Created by dylanlange on 1/05/17.
 */

class TestActivity: AppCompatActivity() {

    private val BEACON_1: Coord = Coord(0.2, 0.2)
    private val BEACON_2: Coord = Coord(0.8, 0.2)
    private val BEACON_3: Coord = Coord(0.2, 0.8)
    private val BEACON_4: Coord = Coord(0.8, 0.8)

    private val METER_WIDTH: Int = 20
    private val METER_HEIGHT: Int = 20

    lateinit private var mPos: Coord
    lateinit private var mBeacons: List<Coord>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        mPos = Coord(METER_WIDTH/2.0, METER_HEIGHT/2.0)

        //getScreenSize()
        setupCanvas()
        setupBtnListeners()
        setupBeaconPositions()
    }

    /*private fun getScreenSize() {
        val display: Display = windowManager.defaultDisplay
        val size: Point = Point()
        display.getSize(size)
        mScreenWidth = size.x
        mScreenHeight = size.y

        mCanvasHeight = iv_canvas.height
    }*/

    private fun setupCanvas() {
        iv_canvas.setup(METER_WIDTH, METER_HEIGHT)
    }

    private fun setupBtnListeners() {
        btn_down.setOnClickListener({
            v ->
            mPos.y += 0.2
            iv_canvas.movePlayer(DylanGridView.DIR.DOWN)
        })
        btn_up.setOnClickListener({
            v ->
            mPos.y -= 0.2
            iv_canvas.movePlayer(DylanGridView.DIR.UP)
        })
        btn_left.setOnClickListener({
            v ->
            mPos.x -= 0.2
            iv_canvas.movePlayer(DylanGridView.DIR.LEFT)
        })
        btn_right.setOnClickListener({
            v ->
            mPos.x += 0.2
            iv_canvas.movePlayer(DylanGridView.DIR.RIGHT)
        })
    }

    private fun setupBeaconPositions() {
        iv_canvas.setBeaconPositions(
                listOf(
                        BEACON_1,
                        BEACON_2,
                        BEACON_3,
                        BEACON_4
                )
        )
    }


}