package com.basketball.game.domain

import com.basketball.game.core.library.XY

data class Ball(
    override var x: Float,
    override var y: Float,
    var isTouched: Boolean = false
): XY