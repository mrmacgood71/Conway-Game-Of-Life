package it.macgood.gol.presentation.gameoflife

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GameOfLifeView : View {

    var alivePaint: Paint? = null
    var deadPaint: Paint? = null

    private var cellSize = 0
    private var numRows = 0
    private var numCols = 0
    private var clickMode: ClickMode = ClickMode.SingleCellMode()

    private var fieldColor: FieldColors = FieldColors(
        Color.rgb(85, 238, 255),
        Color.rgb(0, 136, 151)
    )

    private lateinit var measure: Measures
    private lateinit var currentGeneration: Array<BooleanArray>
    private lateinit var nextGeneration: Array<BooleanArray>

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun setConfiguration(measures: Measures, clickMode: ClickMode, fieldColors: FieldColors) {
        measure = measures
        this.clickMode = clickMode
        cellSize = measure.cellSize
        setDimensions(measure.gridSize, measure.gridSize)
        this.fieldColor = fieldColors
    }

    fun setFieldColor(fieldColors: FieldColors) {
        this.fieldColor = fieldColors
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startX = calculateStartX()
        val startY = calculateStartY()
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                val x = startX + j * cellSize
                val y = startY + i * cellSize
                if (currentGeneration[i][j]) {
                    canvas.drawRect(
                        x.toFloat(),
                        y.toFloat(),
                        (x + cellSize).toFloat(),
                        (y + cellSize).toFloat(),
                        alivePaint!!
                    )
                } else {
                    canvas.drawRect(
                        x.toFloat(),
                        y.toFloat(),
                        (x + cellSize).toFloat(),
                        (y + cellSize).toFloat(),
                        deadPaint!!
                    )
                }
            }
        }
    }

    fun nextGeneration() {
        // calculate the next generation of cells based on the current generation
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                val neighbors = countNeighbors(i, j)
                if (currentGeneration[i][j]) {
                    // a live cell with 2 or 3 neighbors survives, otherwise it dies
                    nextGeneration[i][j] = neighbors == 2 || neighbors == 3
                } else {
                    // a dead cell with 3 neighbors becomes alive
                    nextGeneration[i][j] = neighbors == 3
                }
            }
        }
        // update the current generation to the next generation
        val temp = currentGeneration
        currentGeneration = nextGeneration
        nextGeneration = temp
        // redraw the view
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x - calculateStartX()
        val y = event.y - calculateStartY()
        val row = (y / cellSize).toInt()
        val col = (x / cellSize).toInt()
        if (row in 0 until numRows && col >= 0 && col < numCols) {
            configCellClick(row, col)
            invalidate()
        }
        return true
    }

    private fun init() {
        alivePaint = Paint()
        alivePaint!!.color = fieldColor.aliveColor!!
        deadPaint = Paint()
        deadPaint!!.color = fieldColor.deadColor!!
    }

    private fun setDimensions(numRows: Int, numCols: Int) {
        this.numRows = numRows
        this.numCols = numCols
        currentGeneration = Array(numRows) { BooleanArray(numCols) }
        nextGeneration = Array(numRows) { BooleanArray(numCols) }
        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                currentGeneration[i][j] = Math.random() < 0.5
            }
        }
    }

    private fun calculateStartX() = (width - cellSize * numCols) / 2
    private fun calculateStartY() = (height - cellSize * numRows) / 2

    private fun configCellClick(row: Int, col: Int) {
        when(clickMode) {
            is ClickMode.SingleCellMode -> {
                currentGeneration[row][col] = true
            }
            is ClickMode.CrossHairCellMode -> {
                configCrossHairClickModeDrawing(row, col)
            }
            is ClickMode.CircleCellMode -> {
                configCircleClickModeDrawing(row, col)
            }
        }
    }

    private fun countNeighbors(row: Int, col: Int): Int {
        var count = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) {
                    continue
                }
                val r = row + i
                val c = col + j
                if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
                    continue
                }
                if (currentGeneration[r][c]) {
                    count++
                }
            }
        }
        return count
    }

    private fun configCircleClickModeDrawing(row: Int, col: Int) {
        val isCanBeDrawn = row - 1 >= 0 && col - 1 >= 0 && row + 1 < currentGeneration.size - 1
                && col + 1 < currentGeneration.size - 1
        val isInFirstRow = row - 1 < 0 && col - 1 >= 0 && row + 1 < currentGeneration.size - 1
                && col + 1 < currentGeneration.size - 1
        val isInLastRow = row - 1 >= 0 && col - 1 >= 0 && col + 1 < currentGeneration.size - 1
                && row + 1 >= currentGeneration.size - 1
        val isInFirstColumn = row - 1 >= 0 && col - 1 < 0 && row + 1 < currentGeneration.size - 1
                && col + 1 < currentGeneration.size - 1
        val isInLastColumn = row - 1 >= 0 && col - 1 >= 0 && row + 1 < currentGeneration.size - 1
                && col + 1 >= currentGeneration.size - 1

        if (isCanBeDrawn) {
            currentGeneration[row + 1][col] = true
            currentGeneration[row][col + 1] = true
            currentGeneration[row][col - 1] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row + 1][col + 1] = true
            currentGeneration[row - 1][col - 1] = true
            currentGeneration[row - 1][col + 1] = true
            currentGeneration[row + 1][col - 1] = true
        } else if (isInLastRow) {
            currentGeneration[row][col + 1] = true
            currentGeneration[row][col - 1] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row - 1][col - 1] = true
            currentGeneration[row - 1][col + 1] = true
        } else if (isInFirstRow) {
            currentGeneration[row + 1][col] = true
            currentGeneration[row][col + 1] = true
            currentGeneration[row][col - 1] = true
            currentGeneration[row + 1][col + 1] = true
            currentGeneration[row + 1][col - 1] = true
        } else if (isInFirstColumn) {
            currentGeneration[row + 1][col] = true
            currentGeneration[row][col + 1] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row + 1][col + 1] = true
            currentGeneration[row - 1][col + 1] = true
        } else if (isInLastColumn) {
            currentGeneration[row + 1][col] = true
            currentGeneration[row][col - 1] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row - 1][col - 1] = true
            currentGeneration[row + 1][col - 1] = true
        }
    }

    private fun configCrossHairClickModeDrawing(row: Int, col: Int) {
        val isCanBeDrawn = row - 1 >= 0 && col - 1 >= 0 && row + 1 < currentGeneration.size - 1
                && col + 1 < currentGeneration.size - 1
        val isInFirstRow = row - 1 < 0 && col - 1 >= 0 && row + 1 < currentGeneration.size - 1
                && col + 1 < currentGeneration.size - 1
        val isInLastRow = row - 1 >= 0 && col - 1 >= 0 && col + 1 < currentGeneration.size - 1
                && row + 1 >= currentGeneration.size - 1
        val isInFirstColumn = row - 1 >= 0 && col - 1 < 0 && row + 1 < currentGeneration.size - 1
                && col + 1 < currentGeneration.size - 1
        val isInLastColumn = row - 1 >= 0 && col - 1 >= 0 && row + 1 < currentGeneration.size - 1
                && col + 1 >= currentGeneration.size - 1

        if (isCanBeDrawn) {
            currentGeneration[row][col] = true
            currentGeneration[row + 1][col] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row][col - 1] = true
            currentGeneration[row][col + 1] = true
        } else if (isInLastRow) {
            currentGeneration[row][col] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row][col + 1] = true
            currentGeneration[row][col - 1] = true
        } else if (isInFirstRow) {
            currentGeneration[row][col] = true
            currentGeneration[row + 1][col] = true
            currentGeneration[row][col - 1] = true
            currentGeneration[row][col + 1] = true
        } else if (isInFirstColumn) {
            currentGeneration[row][col] = true
            currentGeneration[row + 1][col] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row][col + 1] = true
        } else if (isInLastColumn) {
            currentGeneration[row][col] = true
            currentGeneration[row + 1][col] = true
            currentGeneration[row - 1][col] = true
            currentGeneration[row][col - 1] = true
        }
    }


    companion object {
        private val TAG = "TAG"

    }
}