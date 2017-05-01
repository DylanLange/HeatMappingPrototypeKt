package com.dylanlange.beaconplay.test

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView
import com.dylanlange.beaconplay.toTwoDp
import io.reactivex.functions.Consumer

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
    val TEXT_SIZE: Float = 60f
    val MAX_HEAT: Float = 100f
    val HEAT_PER_MOVEMENT: Float = 3f
    val START_HEAT_COL: Int = Color.WHITE
    val END_HEAT_COL: Int = Color.RED

    var mPaint: Paint
    var mMeterWidth: Int = 0
    var mMeterHeight: Int = 0

    var mPixWidth: Int = 0
    var mPixHeight: Int = 0

    var pixelsPerMeter: Double = 0.0
    var mGridGap: Int = 0

    var mBeaconPositions: List<Coord> = ArrayList()
    lateinit var mPlayerPos: Coord
    lateinit var mHeatmap: Array<FloatArray>

    var mDoAfterInitialised: List<Consumer<Void>> = ArrayList()

    init {
        mPaint = Paint()
        mPaint.color = Color.BLACK
        mPaint.textSize = TEXT_SIZE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas == null) return
        if(mPixWidth  == 0 || mPixHeight == 0 || mMeterWidth == 0 || mMeterHeight == 0) return

        drawHeatmap(canvas)
        drawGrid(canvas)
        drawBeacons(canvas)
        drawPerson(canvas)
        drawDistanceVectors(canvas)

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPixWidth = w
        mPixHeight = h
        mPlayerPos = Coord(mPixWidth/2.0, mPixHeight/2.0)
        pixelsPerMeter = w/mMeterWidth.toDouble()

        for(consumer: Consumer<Void> in mDoAfterInitialised){
            consumer.accept(null)
        }
        mGridGap = mPixWidth / mMeterWidth

        mHeatmap = Array(mMeterWidth) { FloatArray(mPixHeight/mGridGap) }//initialise heatmap array
    }

    /**
     * FYI: This gets called before onSizeChanged
     */
    fun setup(meterWidth: Int, meterHeight: Int){
        mMeterWidth = meterWidth
        mMeterHeight = meterHeight
    }

    private fun drawGrid(canvas: Canvas) {
        mPaint.color = Color.BLACK
        for(i in 0..mMeterWidth){
            var xPos: Float = i.toFloat() * mGridGap
            canvas.drawLine(xPos, 0f, xPos, mPixHeight.toFloat(), mPaint)
        }
        for(i in 0..mPixHeight/mGridGap){
            var yPos: Float = i.toFloat() * mGridGap
            canvas.drawLine(0f, yPos, mPixWidth.toFloat(), yPos, mPaint)
        }
    }

    private fun drawBeacons(canvas: Canvas) {
        mPaint.color = Color.RED
        for(c: Coord in mBeaconPositions){
            var xPos: Float = (c.x - BEACON_PIX_WIDTH/2).toFloat()
            var yPos: Float = (c.y - BEACON_PIX_WIDTH/2).toFloat()
            canvas.drawOval(xPos, yPos, xPos + BEACON_PIX_WIDTH, yPos + BEACON_PIX_WIDTH, mPaint)
        }
    }

    private fun drawPerson(canvas: Canvas) {
        mPaint.color = Color.GREEN
        var xPos: Float = (mPlayerPos.x - PLAYER_PIX_WIDTH/2).toFloat()
        var yPos: Float = (mPlayerPos.y - PLAYER_PIX_WIDTH/2).toFloat()
        canvas.drawRect(xPos, yPos, xPos + PLAYER_PIX_WIDTH, yPos + PLAYER_PIX_WIDTH, mPaint)
    }

    /**
     * Don't know if the function name makes sense... but this is going to draw
     * lines from the beacon to the player and some text saying how far the player
     * is from each beacon in meters
     */
    private fun drawDistanceVectors(canvas: Canvas) {
        mPaint.color = Color.BLUE
        for(bPos: Coord in mBeaconPositions){
            var dist: Double = getHypotenuseLength(bPos.x, bPos.y, mPlayerPos.x, mPlayerPos.y)
            var distInMeters: Double = dist/pixelsPerMeter
            canvas.drawLine(
                    bPos.x.toFloat(),
                    bPos.y.toFloat(),
                    mPlayerPos.x.toFloat(),
                    mPlayerPos.y.toFloat(),
                    mPaint
            )
            canvas.drawText(
                    distInMeters.toTwoDp().toString() + "m",
                    bPos.x.toFloat() + 10f,
                    bPos.y.toFloat() + 10f,
                    mPaint
            )
        }
        invalidate()
    }

    private fun getHypotenuseLength(x1: Double, y1: Double, x2: Double, y2: Double): Double{
        var dx: Double = Math.abs(x1 - x2)
        var dy: Double = Math.abs(y1 - y2)
        return Math.sqrt((dx * dx) + (dy * dy))//pythag
    }

    private fun heatmapPosition() {
        var heatVal = mHeatmap[
                (mPlayerPos.x / mGridGap).toInt()
                ][
                (mPlayerPos.y / mGridGap).toInt()
                ]
        mHeatmap[
                (mPlayerPos.x / mGridGap).toInt()
                ][
                (mPlayerPos.y / mGridGap).toInt()
                ] = Math.min(MAX_HEAT, heatVal + HEAT_PER_MOVEMENT)
    }

    private fun drawHeatmap(canvas: Canvas) {
        for(row in 0..mHeatmap.size - 1) {
            for(col in 0..mHeatmap[row].size - 1) {
                if(mHeatmap[row][col] == 0f) continue
//                if(row == 10 && col == 10) Log.d("DYLAN", mHeatmap[row][col].toString())
                mPaint.color = getColorOfDegradate(START_HEAT_COL, END_HEAT_COL, mHeatmap[row][col]/100f)
                var xPosPix: Float = (row * mGridGap).toFloat()
                var yPosPix: Float = (col * mGridGap).toFloat()
                canvas.drawRect(
                        xPosPix,
                        yPosPix,
                        xPosPix + mGridGap,
                        yPosPix + mGridGap,
                        mPaint
                )
            }
        }
    }

    fun setBeaconPositions(beaconPositions: List<Coord>){
        mBeaconPositions = beaconPositions
        mDoAfterInitialised = mDoAfterInitialised.plus(object: Consumer<Void> {
            override fun accept(t: Void?) {
                mBeaconPositions.map {
                    it.x *= mPixWidth
                    it.y *= mPixWidth
                }
            }
        })
        invalidate()
    }

    fun movePlayer(dir: DIR){

        heatmapPosition()

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

    private fun getColorOfDegradate(colorStart: Int, colorEnd: Int, percent: Float): Int {
        return ArgbEvaluator().evaluate(percent, colorStart, colorEnd) as Int
    }

}