package com.basketball.game.ui.basketball

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.basketball.game.core.library.GameViewModel
import com.basketball.game.core.library.XYIMpl
import com.basketball.game.core.library.random
import com.basketball.game.domain.Ball
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BasketBallViewModel : GameViewModel() {
    private val _symbols = MutableStateFlow<List<Ball>>(emptyList())
    val symbols = _symbols.asStateFlow()

    private val _stars = MutableStateFlow<List<XYIMpl>>(emptyList())
    val stars = _stars.asStateFlow()

    private val _starsAmount = MutableLiveData(0)
    val starsAmount: LiveData<Int> = _starsAmount

    private val _trigger = MutableStateFlow<Boolean>(true)
    val trigger = _trigger.asStateFlow()

    private val _trigger2 = MutableStateFlow<Boolean>(true)
    val trigger2 = _trigger2.asStateFlow()

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _isMenuOpened = MutableLiveData(false)
    val isMenuOpened: LiveData<Boolean> = _isMenuOpened

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    var starsCallback: (() -> Unit)? = null

    private var isSpawning = false
    private var isSpawning2 = false

    fun changeMenuState() {
        _isMenuOpened.postValue(!_isMenuOpened.value!!)
    }

    init {
        _playerXY.postValue(XYIMpl(0f, 0f))
    }

    fun start(
        symbolSize: Int,
        maxX: Int,
        generationTime: Long,
        fallDelay: Long,
        maxY: Int,
        playerWidth: Int,
        playerHeight: Int,
        distance: Int,
        starSize: Int
    ) {
        isSpawning = false
        isSpawning2 = false
        gameScope = CoroutineScope(Dispatchers.Default)
        generateSymbols(symbolSize, maxX, generationTime)
        letItemsFall(maxY, symbolSize, playerWidth, playerHeight)

        generateStars(starSize, maxX)
        letStarsFall(maxY, starSize, playerWidth, playerHeight)
    }

    private fun generateSymbols(symbolSize: Int, maxX: Int, generationTime: Long) {
        gameScope.launch {
            while (true) {
                delay(2000L)
                isSpawning = true
                val currentList = _symbols.value.toMutableList()
                currentList.add(
                    Ball(
                        y = 0f - symbolSize,
                        x = (0 random (maxX - symbolSize)).toFloat(),
                    )
                )
                _symbols.value = currentList
                isSpawning = false
            }
        }
    }

    private fun generateStars(starSize: Int, maxX: Int) {
        gameScope.launch {
            while (true) {
                delay(10000)
                isSpawning2 = true
                val currentList = _stars.value.toMutableList()
                currentList.add(
                    XYIMpl(
                        y = 0f - starSize,
                        x = (0 random (maxX - starSize)).toFloat(),
                    )
                )
                _stars.value = currentList
                isSpawning2 = false
            }
        }
    }

    private fun letItemsFall(
        maxY: Int,
        symbolSize: Int,
        playerWidth: Int,
        playerHeight: Int,
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (!isSpawning) {
                    val currentList = _symbols.value
                    val newList = mutableListOf<Ball>()
                    currentList.forEach { obj ->
                        if (obj.y <= maxY) {
                            val objX = obj.x.toInt()..obj.x.toInt() + symbolSize
                            val objY =
                                (obj.y.toInt() + symbolSize / 1.2).toInt()..obj.y.toInt() + symbolSize
                            val playerX =
                                _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                            val playerY =
                                _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight / 6
                            if (playerX.any { it in objX } && playerY.any { it in objY }) {
                                if (!obj.isTouched) {
                                    _scores.postValue(_scores.value!! + 1)
                                }
                                obj.isTouched = true
                            }
                            obj.y = obj.y + 6
                            obj.x = obj.x
                            newList.add(obj)
                        } else {
                            if (!obj.isTouched) {
                                _lives.postValue(_lives.value!! - 1)
                            }
                        }
                    }
                    _symbols.value = newList
                    _trigger.value = !_trigger.value
                }
            }
        }
    }

    private fun letStarsFall(
        maxY: Int,
        starSize: Int,
        playerWidth: Int,
        playerHeight: Int,
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (!isSpawning2) {
                    val currentList = _stars.value
                    val newList = mutableListOf<XYIMpl>()
                    currentList.forEach { obj ->
                        if (obj.y <= maxY) {
                            val objX = obj.x.toInt()..obj.x.toInt() + starSize
                            val objY =
                                obj.y.toInt()..obj.y.toInt() + starSize
                            val playerX =
                                _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                            val playerY =
                                _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight / 6
                            if (playerX.any { it in objX } && playerY.any { it in objY }) {
                                _starsAmount.postValue(_starsAmount.value!! + 1)
                                starsCallback?.invoke()
                            } else {
                                obj.y = obj.y + 5
                                obj.x = obj.x
                                newList.add(obj)
                            }
                        }
                    }
                    _stars.value = newList
                    _trigger2.value = !_trigger2.value
                }
            }
        }
    }


    fun initPlayer(x: Int, y: Int, playerWidth: Int, playerHeight: Int) {
        _playerXY.postValue(
            XYIMpl(
                x = x / 2 - (playerWidth.toFloat() / 2),
                y = y - playerHeight.toFloat()
            )
        )
    }

    fun playerMoveLeft(limit: Float) {
        if (_playerXY.value!!.x - 6f > limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x - 6, _playerXY.value!!.y))
        }
    }

    fun playerMoveRight(limit: Float) {
        if (_playerXY.value!!.x + 6f < limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x + 6, _playerXY.value!!.y))
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}