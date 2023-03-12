package com.justin.sparkeffect

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

fun Modifier.sparkOnClick(
    context: Context,
    color: Color = Color.Black,
    durationMillis: Int = 5000,
    easing: Easing = LinearEasing,
    onClick: (() -> Unit)? = null
): Modifier = composed {

    val DOTS_COUNT = 10

    val OUTER_DOTS_POSITION_ANGLE = 360 / DOTS_COUNT

    val color1 = -0x3ef9
    val color2 = -0x6800
    val color3 = -0xa8de
    val color4 = -0xbbcca

    val circlePaints = arrayOfNulls<Paint>(4)

    var centerX = 0
    var centerY = 0

    var maxOuterDotsRadius = 0f
    var maxInnerDotsRadius = 0f
    var maxDotSize = 0f

    var currentProgress = 0f

    var currentRadius1 = 0f
    var currentDotSize1 = 0f

    var currentDotSize2 = 0f
    var currentRadius2 = 0f

    val argbEvaluator = ArgbEvaluator()

    val animationScope = rememberCoroutineScope()
    val factor = remember { Animatable(initialValue = 0f) }
    val alpha = remember { Animatable(initialValue = 0f) }
    var isComputed by remember { mutableStateOf(false) }
    var height = 0
    var width = 0


    this.then(
        onGloballyPositioned {
            if (!isComputed) {
                Log.d("TAG_JUSTIN ", " onGloballyPositioned $isComputed---> ${factor.value}")

                height = it.size.height
                width = it.size.width

                centerX = width / 2
                centerY = height / 2

                maxOuterDotsRadius = width / 2 - maxDotSize * 2
                maxInnerDotsRadius = 0.8f * maxOuterDotsRadius

                maxDotSize = Utils
                    .dpToPx(context = context, dp = 4)
                    .toFloat()
                for (i in circlePaints.indices) {
                    circlePaints[i] = Paint()
                    circlePaints[i]?.style = Paint.Style.FILL
                }
            }
            Log.d("TAG_JUSTIN ", " onGloballyPositioned General ---> ${factor.value}")
        }
            .drawWithContent {
                this.drawContent()
                Log.d("TAG_JUSTIN ", " drawWithContent ---> ${factor.value}")
                currentProgress = factor.value

                ///updateDotsPaints
                if (currentProgress < 0.5f) {
                    val progress =
                        Utils
                            .mapValueFromRangeToRange(
                                currentProgress.toDouble(),
                                0.0,
                                0.5,
                                0.0,
                                1.0
                            )
                            .toFloat()
                    circlePaints[0]!!.color =
                        (argbEvaluator.evaluate(progress, color1, color2) as Int)
                    circlePaints[1]!!.color =
                        (argbEvaluator.evaluate(progress, color2, color3) as Int)
                    circlePaints[2]!!.color =
                        (argbEvaluator.evaluate(progress, color3, color4) as Int)
                    circlePaints[3]!!.color =
                        (argbEvaluator.evaluate(progress, color4, color1) as Int)
                } else {
                    val progress =
                        Utils
                            .mapValueFromRangeToRange(
                                currentProgress.toDouble(),
                                0.5,
                                1.0,
                                0.0,
                                1.0
                            )
                            .toFloat()
                    circlePaints[0]!!.color =
                        (argbEvaluator.evaluate(progress, color2, color3) as Int)
                    circlePaints[1]!!.color =
                        (argbEvaluator.evaluate(progress, color3, color4) as Int)
                    circlePaints[2]!!.color =
                        (argbEvaluator.evaluate(progress, color4, color1) as Int)
                    circlePaints[3]!!.color =
                        (argbEvaluator.evaluate(progress, color1, color2) as Int)
                }
/////////////////////////////////////////////


//updateDotsAlpha
                val progress = Utils
                    .clamp(currentProgress.toDouble(), 0.6, 1.0)
                    .toFloat()
                val alpha =
                    Utils
                        .mapValueFromRangeToRange(progress.toDouble(), 0.6, 1.0, 255.0, 0.0)
                        .toInt()
                circlePaints[0]!!.alpha = alpha
                circlePaints[1]!!.alpha = alpha
                circlePaints[2]!!.alpha = alpha
                circlePaints[3]!!.alpha = alpha
/////////////////////////////////////////////



                ///updateInnerDotsPosition
                if (currentProgress < 0.3f) {
                    currentRadius2 = Utils
                        .mapValueFromRangeToRange(
                            currentProgress.toDouble(), fromLow = 0.0, fromHigh = 0.3, toLow = 0.0,
                            maxInnerDotsRadius.toDouble()
                        )
                        .toFloat()
                } else {
                    currentRadius2 = maxInnerDotsRadius
                }

                if (currentProgress < 0.2) {
                    currentDotSize2 = maxDotSize
                } else if (currentProgress < 0.5) {
                    currentDotSize2 = Utils
                        .mapValueFromRangeToRange(
                            currentProgress.toDouble(), 0.2, 0.5,
                            maxDotSize.toDouble(), 0.3 * maxDotSize
                        )
                        .toFloat()
                } else {
                    currentDotSize2 = Utils
                        .mapValueFromRangeToRange(
                            currentProgress.toDouble(), 0.5, 1.0,
                            (maxDotSize * 0.3f).toDouble(), 0.0
                        )
                        .toFloat()
                }
                /////////////////////////////////////////////


                //drawInnerDotsFrame
                for (i in 0 until DOTS_COUNT) {
                    val cX =
                        (centerX + currentRadius2 * Math.cos((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
                    val cY =
                        (centerY + currentRadius2 * Math.sin((i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180)).toInt()
                    /*this.drawCircle(
                        cX.toFloat(), cY.toFloat(), currentDotSize2,
                        circlePaints[(i + 1) % circlePaints.size]!!
                    )*/
                    Log.d("TAG_JUSTIN","$color || $currentDotSize2 ||")
                    this.drawCircle(
                        color = Color(circlePaints[(i + 1) % circlePaints.size]?.color!!),
                        radius = currentDotSize2,
                        center = Offset(cX.toFloat(), cY.toFloat())
                    )
                }
                ///////////////////////////////////////////////////



                ////updateOuterDotsPosition
                if (currentProgress < 0.3f) {
                    currentRadius1 = Utils
                        .mapValueFromRangeToRange(
                            currentProgress.toDouble(), 0.0, 0.3, 0.0,
                            (maxOuterDotsRadius * 0.8f).toDouble()
                        )
                        .toFloat()
                } else {
                    currentRadius1 = Utils
                        .mapValueFromRangeToRange(
                            currentProgress.toDouble(), 0.3, 1.0,
                            (0.8f * maxOuterDotsRadius).toDouble(), maxOuterDotsRadius.toDouble()
                        )
                        .toFloat()
                }


                if (currentProgress < 0.7) {
                    currentDotSize1 = maxDotSize
                } else {
                    currentDotSize1 = Utils
                        .mapValueFromRangeToRange(
                            currentProgress.toDouble(), 0.7, 1.0,
                            maxDotSize.toDouble(), 0.0
                        )
                        .toFloat()
                }
                /////////////////////////////////////////////


                //drawOuterDotsFrame
                for (i in 0 until DOTS_COUNT) {
                    Log.d(
                        "TAG_JUSTIN",
                        "----   $centerX || $currentRadius1 || ${Math.cos(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180)}"
                    )
                    val cX =
                        (centerX + currentRadius1 * Math.cos(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180)).toInt()
                    val cY =
                        (centerY + currentRadius1 * Math.sin(i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180)).toInt()

                    Log.d(
                        "TAG_JUSTIN",
                        "----   $color || $currentDotSize1 || ${Offset(cX.toFloat(), cY.toFloat())}"
                    )


                    this.drawCircle(
                        color = Color(circlePaints[(i + 1) % circlePaints.size]?.color!!),
                        radius = currentDotSize1,
                        center = Offset(cX.toFloat(), cY.toFloat())
                        //color = circlePaints[i % circlePaints.size]!!,//TODO find color
                    )
                }
                /////////////////////////////////////


            }
            .clickable {
                Log.d("TAG_JUSTIN ", " clickable 1 ---> ${factor.value}")

                animationScope.launch {
                    if (!isComputed) {

                        isComputed = true
                    }



                    val result = async {
                        Log.d("TAG_JUSTIN ", " clickable  2 ---> ${factor.value}")

                        factor.animateTo(
                            targetValue = 1.0f,
                            animationSpec = tween(durationMillis = durationMillis, easing = easing)
                        )
                    }
                    result.invokeOnCompletion {
                        this.launch {
                            factor.snapTo(0f)
                        }
                    }

                }


                onClick?.invoke()
            }
    )
}

