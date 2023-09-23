package com.basketball.game.ui.dialogs.settings

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.basketball.game.R
import com.basketball.game.core.library.ViewBindingDialog
import com.basketball.game.core.library.shortToast
import com.basketball.game.databinding.DialogSettingsBinding
import com.basketball.game.domain.Sp

class DialogSettings : ViewBindingDialog<DialogSettingsBinding>(DialogSettingsBinding::inflate) {
    private val viewModel: SettingsViewModel by viewModels()
    private val sp by lazy {
        Sp(requireContext())
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack()
                true
            } else {
                false
            }
        }

        if (viewModel.currentBall.value!! == -1) {
            viewModel.initBall(sp.getCurrentBall())
        }

        setButton()
        setStars()

        binding.left.setOnClickListener {
            viewModel.left()
            setButton()
        }

        binding.right.setOnClickListener {
            viewModel.right()
            setButton()
        }

        binding.select.setOnClickListener {
            if (sp.isBallBought(sp.getCurrentBall())) {
                sp.setCurrentBall(viewModel.currentBall.value!!)
            } else {
                if (sp.getStarsAmount() >= 150) {
                    sp.setStarsAmount(-150)
                    sp.buyBall(viewModel.currentBall.value!!)
                    setButton()
                    setStars()
                } else {
                    shortToast(requireContext(), "Not enough stars")
                }
            }
        }

        binding.close.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ok.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.currentBall.observe(viewLifecycleOwner) {
            binding.ball.setImageResource(
                when (it) {
                    1 -> R.drawable.ball01
                    2 -> R.drawable.ball02
                    3 -> R.drawable.ball03
                    4 -> R.drawable.ball04
                    5 -> R.drawable.ball05
                    else -> R.drawable.ball06
                }
            )
            setButton()
        }
    }

    private fun setButton() {
        val text = if (sp.isBallBought(viewModel.currentBall.value!!)) "select" else "buy"
        binding.select.text = text
    }

    private fun setStars() {
        binding.stars2.text = sp.getStarsAmount().toString()
    }
}