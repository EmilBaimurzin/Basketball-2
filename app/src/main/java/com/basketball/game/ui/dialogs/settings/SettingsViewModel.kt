package com.basketball.game.ui.dialogs.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    private val _currentBall = MutableLiveData(-1)
    val currentBall: LiveData<Int> = _currentBall

    fun left() {
        if (_currentBall.value!! - 1 >= 1) {
            _currentBall.postValue(_currentBall.value!! - 1)
        }
    }

    fun right() {
        if (_currentBall.value!! + 1 <= 6) {
            _currentBall.postValue(_currentBall.value!! + 1)
        }
    }

    fun initBall(ball: Int) {
        _currentBall.postValue(ball)
    }
}