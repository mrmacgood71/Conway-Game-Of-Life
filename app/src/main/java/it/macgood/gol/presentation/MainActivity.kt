package it.macgood.gol.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import it.macgood.core.activity.restartApp
import it.macgood.core.extension.viewBinding
import it.macgood.core.utils.UiUtils
import it.macgood.gol.databinding.ActivityMainBinding
import it.macgood.gol.presentation.gameoflife.FieldColors
import it.macgood.gol.presentation.gameoflife.GameOfLifeView
import it.macgood.gol.presentation.gameoflife.Measures
import it.macgood.gol.presentation.settings.SettingsFragment
import it.macgood.gol.presentation.settings.SettingsViewModel
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    private var gameOfLifeView: GameOfLifeView? = null
    private var handler: Handler? = null
    private var firstLaunch: Boolean = true
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.gameOfLifeView.visibility = View.GONE

        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        firstLaunch = preferences.getBoolean(FIRST_LAUNCH_PREFERENCE, true)


        binding.newGameButton.setOnClickListener {
            settingsViewModel.clickSize.observe(this) { measures ->
                binding.root.removeView(binding.gameOfLifeView)
                binding.root.addView(configureGameView(measures))
                binding.welcomeImageView.visibility = View.GONE
                binding.welcomeTextView.visibility = View.GONE
                binding.descriptionLegendTextView.visibility = View.GONE
            }
        }

        binding.settingsButton.setOnClickListener {
            SettingsFragment().show(supportFragmentManager, "tag")
        }

        binding.newGameButton.performClick()

    }
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private fun configureGameView(measure: Measures) : GameOfLifeView {
        gameOfLifeView = binding.gameOfLifeView
        settingsViewModel.colors.observe(this) {
            gameOfLifeView?.alivePaint?.color = it.aliveColor!!
            val deadColor = it.deadColor!!
            gameOfLifeView?.deadPaint?.color = deadColor

            decorateElements(deadColor)

        }

        settingsViewModel.clickMode.observe(this) {
            gameOfLifeView?.setConfiguration(
                measure,
                it,
                FieldColors(Color.BLUE, Color.BLACK)
            )
        }

        settingsViewModel.clickSize.observe(this) {
            gameOfLifeView?.measure = it
        }

        gameOfLifeView!!.visibility = View.VISIBLE

        settingsViewModel.delay.observe(this) { it ->
            coroutineScope.launch {
                while (isActive) {
                    gameOfLifeView?.nextGeneration()
                    delay(it)
                }
            }

        }

        return gameOfLifeView as GameOfLifeView
    }

    private fun decorateElements(deadColor: Int) {
        binding.settingsButton.setBackgroundColor(deadColor)
        binding.newGameButton.setBackgroundColor(deadColor)
        window.statusBarColor = deadColor
        supportActionBar?.setBackgroundDrawable(ColorDrawable(deadColor))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        if (!UiUtils.isColorReadable(deadColor)) {
            supportActionBar?.title = TITLE_TEXT_DARK_VARIANT
            binding.newGameButton.setTextColor(Color.BLACK)
            binding.settingsButton.setTextColor(Color.BLACK)
        } else {
            supportActionBar?.title = TITLE_TEXT_LIGHT_VARIANT
            binding.newGameButton.setTextColor(Color.WHITE)
            binding.settingsButton.setTextColor(Color.WHITE)
        }
    }

    override fun onStop() {
        super.onStop()
        preferences.edit().putBoolean(FIRST_LAUNCH_PREFERENCE, true).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.edit().putBoolean(FIRST_LAUNCH_PREFERENCE, true).apply()
    }

    companion object {
        const val TAG = "TAG"
        val TITLE_TEXT_DARK_VARIANT: Spanned
                            = Html.fromHtml("<font color=\"#000000\">" + "GoL" + "</font>")
        val TITLE_TEXT_LIGHT_VARIANT: Spanned
                            = Html.fromHtml("<font color=\"#FFFFFF\">" + "GoL" + "</font>")
        const val APP_PREFERENCES = "APP_PREFERENCES"
        const val FIRST_LAUNCH_PREFERENCE = "FIRST_LAUNCH_PREFERENCE"
    }
}