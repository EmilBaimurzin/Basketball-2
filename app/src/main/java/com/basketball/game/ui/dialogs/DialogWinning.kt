package com.basketball.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.basketball.game.R
import com.basketball.game.core.library.ViewBindingDialog
import com.basketball.game.databinding.DialogWiningsBinding
import com.basketball.game.domain.Sp

class DialogWinning : ViewBindingDialog<DialogWiningsBinding>(DialogWiningsBinding::inflate) {
    private val args: DialogWinningArgs by navArgs()
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
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }

        binding.bestScore.text = sp.getBest().toString()
        binding.score.text = args.scores.toString()

        binding.menu.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.restart.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentBasketBall)
        }

        binding.settings.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(R.id.action_fragmentMain_to_dialogSettings)
        }
    }
}