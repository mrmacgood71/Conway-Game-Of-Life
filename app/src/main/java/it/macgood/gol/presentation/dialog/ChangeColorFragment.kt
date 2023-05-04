package it.macgood.gol.presentation.dialog

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import it.macgood.core.utils.UiUtils
import it.macgood.gol.databinding.FragmentChangeColorBinding
import it.macgood.gol.presentation.settings.SettingsViewModel
import it.macgood.gol.presentation.gameoflife.FieldColors


class ChangeColorFragment : DialogFragment() {

    private val settingsViewModel by activityViewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentChangeColorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChangeColorBinding.inflate(inflater, container, false)

        if (tag!! == ALIVE_COLOR_PICKER_TAG) {

            val aliveColor = settingsViewModel.colors.value?.aliveColor!!
            binding.colorPicker.setInitialColor(aliveColor)

            binding.colorPicker.setColorListener(ColorEnvelopeListener {envelope, _ ->
                val deadColor = settingsViewModel.colors.value?.deadColor
                settingsViewModel.changeColor(
                    FieldColors(
                        aliveColor = envelope.color,
                        deadColor = deadColor
                    ))

                Log.d("TAG", "onCreateView: ${envelope.color}")
            })

        } else if (tag!! == DEAD_COLOR_PICKER_TAG) {

            val deadColor = settingsViewModel.colors.value?.deadColor!!

            binding.colorPicker.setInitialColor(deadColor)
            binding.colorPicker.setColorListener(ColorEnvelopeListener {envelope, _ ->
                settingsViewModel.changeColor(
                    FieldColors(
                        aliveColor = settingsViewModel.colors.value?.aliveColor,
                        deadColor = envelope.color
                    ))
                Log.d("TAG", "onCreateView: ${UiUtils.isColorReadable(envelope.color)}")
            })

        }


        return binding.root
    }

    companion object {
        const val ALIVE_COLOR_PICKER_TAG = "ALIVE_COLOR_PICKER_TAG"
        const val DEAD_COLOR_PICKER_TAG = "DEAD_COLOR_PICKER_TAG"
    }


}