package com.dylanlange.beaconplay.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by dylanlange on 1/05/17.
 */
class DylanGridView: ImageView {

    constructor (context : Context) : super(context)
    constructor (context : Context, attrs : AttributeSet) : super(context,attrs)
    constructor (context : Context, attrs : AttributeSet, defStyleAttr : Int) : super(context,attrs,defStyleAttr)
    constructor (context : Context, attrs : AttributeSet, defStyleAttr : Int, defStyleRes: Int) : super(context,attrs,defStyleAttr,defStyleRes)

    val BEACON_PIX_WIDTH: Float = 20f
    val PLAYER_PIX_WIDTH: Float = 30f
    val METERS_PER_MOVEMENT: Double = 0.5

    var mPaint: Paint
    var mMeterWidth: Int = 0
    var mMeterHeight: Int = 0

    var mPixWidth: Int = 0
    var mPixHeight: Int = 0

    var pixelsPerMeter: Double = 0.0

    var mBeaconPositions: List<Coord> = ArrayList()
    lateinit var mPlayerPos: Coord

    init {
        mPaint = Paint()
        mPaint.color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas == null) return
        if(mPixWidth  == 0 || mPixHeight == 0 || mMeterWidth == 0 || mMeterHeight == 0) return

        drawGrid(canvas)
        drawBeacons(canvas)
        drawPerson(canvas)

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPixWidth = w
        mPixHeight = h
        mPlayerPos = Coord(mPixWidth/2.0, mPixHeight/2.0)
        pixelsPerMeter = w/mMeterWidth.toDouble()
    }

    fun setup(meterWidth: Int, meterHeight: Int){
        mMeterWidth = meterWidth
        mMeterHeight = meterHeight
    }

    fun drawGrid(canvas: Canvas) {
        mPaint.color = Color.BLACK
        var gridGap: Int = mPixWidth / mMeterWidth
        for(i in 0..mMeterWidth){
            var xPos: Float = i.toFloat() * gridGap
            canvas.drawLine(xPos, 0f, xPos, mPixHeight.toFloat(), mPaint)
        }
        for(i in 0..mPixHeight/gridGap){
            var yPos: Float = i.toFloat() * gridGap
            canvas.drawLine(0f, yPos, mPixWidth.toFloat(), yPos, mPaint)
        }
    }

    fun drawBeacons(canvas: Canvas) {
        mPaint.color = Color.RED
        for(c: Coord in mBeaconPositions){
            var xPos: Float = (c.x * mPixWidth - BEACON_PIX_WIDTH/2).toFloat()
            var yPos: Float = (c.y * mPixWidth - BEACON_PIX_WIDTH/2).toFloat()
            canvas.drawOval(xPos, yPos, xPos + BEACON_PIX_WIDTH, yPos + BEACON_PIX_WIDTH, mPaint)
        }
    }

    fun drawPerson(canvas: Canvas) {
        mPaint.color = Color.GREEN
        var xPos: Float = (mPlayerPos.x - PLAYER_PIX_WIDTH/2).toFloat()
        var yPos: Float = (mPlayerPos.y - PLAYER_PIX_WIDTH/2).toFloat()
        canvas.drawRect(xPos, yPos, xPos + PLAYER_PIX_WIDTH, yPos + PLAYER_PIX_WIDTH, mPaint)
    }

    fun setBeaconPositions(beaconPositions: List<Coord>){
        mBeaconPositions = beaconPositions
        invalidate()
    }

    fun movePlayer(dir: DIR){
        var distanceToMove = METERS_PER_MOVEMENT * pixelsPerMeter
        when(dir){
            DIR.LEFT -> mPlayerPos.x -= distanceToMove
            DIR.RIGHT -> mPlayerPos.x += distanceToMove
            DIR.UP -> mPlayerPos.y -= distanceToMove
            DIR.DOWN -> mPlayerPos.y += distanceToMove
        }
        invalidate()
    }

    enum class DIR {
        LEFT, RIGHT, UP, DOWN
    }

}