package it.macgood.gol.presentation.settings

import android.R
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.macgood.core.fragment.themeTextColor
import it.macgood.core.utils.UiUtils
import it.macgood.gol.databinding.FragmentSettingsBinding
import it.macgood.gol.presentation.dialog.ChangeColorFragment
import it.macgood.gol.presentation.gameoflife.ClickMode
import java.lang.String
import java.util.*


class SettingsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)


        binding.measureSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                Log.d("TAG", "onStopTrackingTouch: ${seekBar?.progress}")
            }

        })

        binding.chooseAliveColorLayout.setOnClickListener {
            ChangeColorFragment().show(
                requireActivity().supportFragmentManager,
                ChangeColorFragment.ALIVE_COLOR_PICKER_TAG
            )
        }

        binding.chooseDeadColorLayout.setOnClickListener {
            ChangeColorFragment().show(
                requireActivity().supportFragmentManager,
                ChangeColorFragment.DEAD_COLOR_PICKER_TAG
            )
        }

        settingsViewModel.colors.observe(viewLifecycleOwner) {
            val aliveColor = it.aliveColor!!
            val deadColor = it.deadColor!!
            configAliveColorLayout(aliveColor)
            configDeadColorLayout(deadColor)
        }

        configClickMode()

        binding.closeButton.setOnClickListener {
            this.dismiss()
        }

        return binding.root
    }

    private fun configClickMode() {

        binding.singleCellRadioButton.setOnClickListener {
            settingsViewModel.clickMode.postValue(ClickMode.SingleCellMode())
        }

        binding.crosshairCellRadioButton.setOnClickListener {
            settingsViewModel.clickMode.postValue(ClickMode.CrossHairCellMode())
        }

        binding.circleCellRadioButton.setOnClickListener {
            settingsViewModel.clickMode.postValue(ClickMode.CircleCellMode())
        }

        settingsViewModel.clickMode.observe(viewLifecycleOwner) {
            when(it) {
                is ClickMode.SingleCellMode -> {
                    binding.singleCellRadioButton.isChecked = true
                }
                is ClickMode.CrossHairCellMode -> {
                    binding.crosshairCellRadioButton.isChecked = true
                }
                is ClickMode.CircleCellMode -> {
                    binding.circleCellRadioButton.isChecked = true
                }
                else -> {binding.crosshairCellRadioButton.isChecked = true}
            }
        }
    }

    private fun configAliveColorLayout(aliveColor: Int) {
        if (!UiUtils.isColorReadable(aliveColor)) {
            binding.aliveColorPickerTextView.setTextColor(themeTextColor(R.attr.textColor))
        } else {
            binding.aliveColorPickerTextView.setTextColor(aliveColor)
        }
        binding.aliveColorPickerPreference.setBackgroundColor(aliveColor)

        binding.aliveColorPickerTextView.text = String.format("#%06X", 0xFFFFFF and aliveColor)
            .lowercase(
                Locale.ROOT
            )
    }

    private fun configDeadColorLayout(deadColor: Int) {
        if (!UiUtils.isColorReadable(deadColor)) {
            binding.deadColorPickerTextView.setTextColor(themeTextColor(R.attr.textColor))
        } else {
            binding.deadColorPickerTextView.setTextColor(deadColor)
        }
        binding.deadColorPickerPreference.setBackgroundColor(deadColor)

        binding.deadColorPickerTextView.text = String.format("#%06X", 0xFFFFFF and deadColor)
            .lowercase(
                Locale.ROOT
            )
    }

}