package it.macgood.gol.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import it.macgood.core.extension.viewBinding
import it.macgood.core.utils.UiUtils
import it.macgood.gol.databinding.ActivityMainBinding
import it.macgood.gol.presentation.gameoflife.ClickMode
import it.macgood.gol.presentation.gameoflife.FieldColors
import it.macgood.gol.presentation.gameoflife.GameOfLifeView
import it.macgood.gol.presentation.gameoflife.Measures
import it.macgood.gol.presentation.settings.SettingsFragment
import it.macgood.gol.presentation.settings.SettingsViewModel


class MainActivity : AppCompatActivity() {

    private var gameOfLifeView: GameOfLifeView? = null
    private var mHandler: Handler? = null
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.gameOfLifeView.visibility = View.GONE

        binding.newGameButton.setOnClickListener {
            binding.root.removeView(binding.gameOfLifeView)
            binding.root.addView(configureGameView(Measures.FortyFiveGridSize()))
        }

        binding.settingsButton.setOnClickListener {
            SettingsFragment().show(supportFragmentManager, "tag")
        }



        binding.newGameButton.performClick()

    }

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

        gameOfLifeView!!.visibility = View.VISIBLE

        mHandler = Handler(mainLooper)
        mHandler!!.postDelayed(object : Runnable {
            override fun run() {
                gameOfLifeView?.nextGeneration()
                mHandler!!.postDelayed(this, 150)
            }
        }, 100)

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

    companion object {
        const val TAG = "TAG"
        val TITLE_TEXT_DARK_VARIANT: Spanned
                            = Html.fromHtml("<font color=\"#000000\">" + "GoL" + "</font>")
        val TITLE_TEXT_LIGHT_VARIANT: Spanned
                            = Html.fromHtml("<font color=\"#FFFFFF\">" + "GoL" + "</font>")
    }
}