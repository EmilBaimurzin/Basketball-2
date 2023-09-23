package com.basketball.game.domain

import android.content.Context

class Sp(private val context: Context) {
    private val sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE)

    fun getStarsAmount(): Int = sp.getInt("STARS", 0)
    fun setStarsAmount(amount: Int) = sp.edit().putInt("STARS", getStarsAmount() + amount).apply()

    fun getBest(): Int = sp.getInt("BEST", 0)
    fun setBest(record: Int) = sp.edit().putInt("BEST", record).apply()

    fun getCurrentBall(): Int = sp.getInt("BALL", 1)
    fun setCurrentBall(ball: Int) = sp.edit().putInt("BALL", ball).apply()

    fun isBallBought(ball: Int): Boolean = sp.getBoolean(ball.toString(), false)
    fun buyBall(ball: Int) = sp.edit().putBoolean(ball.toString(), true).apply()
}