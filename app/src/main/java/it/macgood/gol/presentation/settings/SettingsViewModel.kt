package it.macgood.gol.presentation.settings

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.macgood.gol.presentation.gameoflife.ClickMode
import it.macgood.gol.presentation.gameoflife.FieldColors
import it.macgood.gol.presentation.gameoflife.Measures

class SettingsViewModel : ViewModel() {

    val colors: MutableLiveData<FieldColors> = MutableLiveData<FieldColors>()

    val clickMode: MutableLiveData<ClickMode> = MutableLiveData()

    val clickSize: MutableLiveData<Measures> = MutableLiveData()

    val delay: MutableLiveData<Long> = MutableLiveData()

    init {
        changeColor(
            FieldColors(
                Color.parseColor("#55eeff"),
                Color.parseColor("#008897")
            )
        )
        changeClickMode(ClickMode.CrossHairCellMode())
        clickSize.postValue(Measures.FiftyGridSize())
        delay.postValue(150)
    }

    fun changeColor(fieldColors: FieldColors) {
        colors.postValue(fieldColors)
    }

    fun changeClickMode(clickMode: ClickMode) {
        this.clickMode.postValue(clickMode)
    }

}