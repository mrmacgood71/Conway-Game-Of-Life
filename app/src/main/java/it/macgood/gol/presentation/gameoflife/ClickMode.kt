package it.macgood.gol.presentation.gameoflife

sealed class ClickMode(
    val id: Int
) {
    class SingleCellMode : ClickMode(id = 1)
    class CrossHairCellMode : ClickMode(id = 2)
    class CircleCellMode : ClickMode(id = 3)
}