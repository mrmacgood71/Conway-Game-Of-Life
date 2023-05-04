package it.macgood.gol.presentation.gameoflife

sealed class Measures(
    val gridSize: Int,
    val cellSize: Int
) {

    class TenGridSize : Measures(10, 90)
    class FifteenGridSize : Measures(15, 70)
    class TwentyGridSize : Measures(20, 50)
    class TwentyFiveGridSize : Measures(25, 40)
    class ThirtyGridSize : Measures(30, 35)
    class ThirtyFiveGridSize : Measures(35, 30)
    class FortyGridSize : Measures(40, 25)
    class FortyFiveGridSize : Measures(45, 23)
    class FiftyGridSize : Measures(50, 20)

}