package com.example.nzweather

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*


import kotlin.random.Random

@Composable
fun ParticleEffect(
    condition: String,
    modifier: Modifier = Modifier
) {
    val rainDrops = remember {
        List(200) { // Tăng số lượng hạt để dễ nhìn thấy hơn
            Drop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() + 1
            )
        }
    }
    val sunRays = remember {
        List(200) {
            Drop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() + 1
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()

    val animatedRainY = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing), // Tăng thời gian để thấy rõ sự di chuyển
            repeatMode = RepeatMode.Restart
        )
    )

    val animatedSunY = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing), // Tăng thời gian để thấy rõ sự di chuyển
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        when {
            condition.contains("rain", ignoreCase = true) -> {
                rainDrops.forEach { drop ->
                    val yPosition = drop.y + animatedRainY.value * drop.speed
                    drawCircle(
                        color = Color.Blue,
                        radius = 10f, // Tăng kích thước hạt để dễ nhìn thấy hơn
                        center = this.center.copy(x = size.width * drop.x, y = size.height * yPosition)
                    )
                }
            }
            condition.contains("clear", ignoreCase = true) -> {
                sunRays.forEach { drop ->
                    val yPosition = drop.y - animatedSunY.value * drop.speed
                    drawCircle(
                        color = Color.Yellow,
                        radius = 10f, // Tăng kích thước hạt để dễ nhìn thấy hơn
                        center = this.center.copy(x = size.width * drop.x, y = size.height * yPosition)
                    )
                }
            }
        }
    }
}

data class Drop(
    val x: Float,
    val y: Float,
    val speed: Float
)
