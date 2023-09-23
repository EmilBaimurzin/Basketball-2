package com.basketball.game.ui.basketball

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.basketball.game.R
import com.basketball.game.core.library.GameFragment
import com.basketball.game.databinding.FragmentBasketballBinding
import com.basketball.game.domain.Sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentBasketBall :
    GameFragment<FragmentBasketballBinding>(FragmentBasketballBinding::inflate) {
    private val viewModel: BasketBallViewModel by viewModels()
    private var ballImg = R.drawable.ball01
    private var moveScope = CoroutineScope(Dispatchers.Default)
    private val sp by lazy {
        Sp(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBall()

        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.basket.apply {
                x = it.x
                y = it.y
            }
        }

        binding.menu.setOnClickListener {
            viewModel.changeMenuState()
            setBall()
            if (viewModel.isMenuOpened.value!!) {
                viewModel.pauseState = false
                viewModel.start(
                    dpToPx(70),
                    xy.x.toInt(),
                    1800,
                    12L,
                    xy.y.toInt(),
                    binding.basket.width,
                    binding.basket.height,
                    distance = 8,
                    dpToPx(30)
                )
            } else {
                viewModel.stop()
                viewModel.pauseState = true
            }
        }

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentBasketBall_to_dialogSettings)
        }

        binding.resume.setOnClickListener {
            viewModel.pauseState = false
            setBall()
            viewModel.changeMenuState()
            viewModel.start(
                dpToPx(70),
                xy.x.toInt(),
                1800,
                12L,
                xy.y.toInt(),
                binding.basket.width,
                binding.basket.height,
                distance = 8,
                dpToPx(30)
            )
        }

        binding.mainMenu.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.starsCallback = {
            sp.setStarsAmount(1)
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.score.text = it.toString()
        }

        viewModel.isMenuOpened.observe(viewLifecycleOwner) {
            binding.menuLayout.isVisible = it
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val heartView = ImageView(requireContext())
                heartView.layoutParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(25)).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
                heartView.setImageResource(R.drawable.live)
                binding.livesLayout.addView(heartView)
            }

            if (it == 0 && viewModel.gameState) {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.gameState = false
                    viewModel.stop()
                    if (sp.getBest() < viewModel.scores.value!!) {
                        sp.setBest(viewModel.scores.value!!)
                    }
                    findNavController().navigate(FragmentBasketBallDirections.actionFragmentBasketBallToDialogWinning(viewModel.scores.value!!))
                }
            }
        }

        viewModel.starsAmount.observe(viewLifecycleOwner) {
            binding.stars.text = it.toString()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trigger.collect {
                    binding.ballsLayout.removeAllViews()
                    viewModel.symbols.value.forEach { symbol ->
                        val symbolView = ImageView(requireContext())
                        symbolView.layoutParams =
                            ViewGroup.LayoutParams(dpToPx(70), dpToPx(70))
                        symbolView.setImageResource(ballImg)
                        symbolView.x = symbol.x
                        symbolView.y = symbol.y
                        binding.ballsLayout.addView(symbolView)
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trigger2.collect {
                    binding.starsLayout.removeAllViews()
                    viewModel.stars.value.forEach { star ->
                        val starView = ImageView(requireContext())
                        starView.layoutParams =
                            ViewGroup.LayoutParams(dpToPx(30), dpToPx(30))
                        starView.setImageResource(R.drawable.star)
                        starView.x = star.x
                        starView.y = star.y
                        binding.starsLayout.addView(starView)
                    }
                }
            }
        }

        lifecycleScope.launch {
            delay(10)
            if (viewModel.playerXY.value!!.y == 0f) {
                viewModel.initPlayer(
                    xy.x.toInt(),
                    xy.y.toInt(),
                    binding.basket.width,
                    binding.basket.height + dpToPx(100)
                )
            }

            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    dpToPx(70),
                    xy.x.toInt(),
                    1800,
                    12L,
                    xy.y.toInt(),
                    binding.basket.width,
                    binding.basket.height,
                    distance = 8,
                    dpToPx(30)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setButtons()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtons() {
        binding.buttonLeft.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!viewModel.isMenuOpened.value!!) {
                        moveScope.launch {
                            while (true) {
                                viewModel.playerMoveLeft(0f)
                                delay(2)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    if (!viewModel.isMenuOpened.value!!) {
                        moveScope.launch {
                            while (true) {
                                viewModel.playerMoveLeft(0f)
                                delay(2)
                            }
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.buttonRight.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!viewModel.isMenuOpened.value!!) {
                        moveScope.launch {
                            while (true) {
                                viewModel.playerMoveRight((xy.x - binding.basket.width))
                                delay(2)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    if (!viewModel.isMenuOpened.value!!) {
                        moveScope.launch {
                            while (true) {
                                viewModel.playerMoveRight((xy.x - binding.basket.width))
                                delay(2)
                            }
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }
    }

    private fun setBall() {
        ballImg = when (sp.getCurrentBall()) {
            1 -> R.drawable.ball01
            2 -> R.drawable.ball02
            3 -> R.drawable.ball03
            4 -> R.drawable.ball04
            5 -> R.drawable.ball05
            else -> R.drawable.ball06
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}