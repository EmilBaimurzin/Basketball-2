package com.basketball.game.ui.home_page

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.basketball.game.R
import com.basketball.game.core.library.GameFragment
import com.basketball.game.databinding.FragmentHomePageBinding
import com.basketball.game.domain.Sp

class FragmentHomePage : GameFragment<FragmentHomePageBinding>(FragmentHomePageBinding::inflate) {
    private val sp by lazy {
        Sp(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sp.buyBall(1)
        binding.apply {
            play.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentBasketBall)
            }
            settings.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_dialogSettings)
            }
            exit.setOnClickListener {
                requireActivity().finish()
            }
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}