package com.soi.moya.ui.music_storage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.layoutId
import com.soi.moya.R
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle

enum class SwipingStates {
    EXPANDED,
    COLLAPSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipingHeader() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        val swipingState = rememberSwipeableState(initialValue = SwipingStates.EXPANDED)
        BoxWithConstraints(//to get the max height
            modifier = Modifier.fillMaxSize()
        ) {
            val heightInPx = with(LocalDensity.current) { maxHeight.toPx() }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val delta = available.y
                        return if (delta < 0) {
                            swipingState.performDrag(delta).toOffset()
                        } else {
                            Offset.Zero
                        }
                    }

                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val delta = available.y
                        return swipingState.performDrag(delta).toOffset()
                    }

                    override suspend fun onPostFling(
                        consumed: Velocity,
                        available: Velocity
                    ): Velocity {
                        swipingState.performFling(velocity = available.y)
                        return super.onPostFling(consumed, available)
                    }

                    private fun Float.toOffset() = Offset(0f, this)
                }
            }

            Box(//root container
                modifier = Modifier
                    .fillMaxSize()
                    .swipeable(
                        state = swipingState,
                        thresholds = { _, _ ->
                            FractionalThreshold(0.05f)//it can be 0.5 in general
                        },
                        orientation = Orientation.Vertical,
                        anchors = mapOf(
                            0f to SwipingStates.COLLAPSED,//min height is collapsed
                            heightInPx to SwipingStates.EXPANDED,//max height is expanded
                        )
                    )
                    .nestedScroll(nestedScrollConnection)
            ) {
                val computedProgress by remember {//progress value will be decided as par state
                    derivedStateOf {
                        if (swipingState.progress.to == SwipingStates.COLLAPSED)
                            swipingState.progress.fraction
                        else
                            1f - swipingState.progress.fraction
                    }
                }
                val startHeightNum = 200
                MotionLayout(
                    modifier = Modifier.fillMaxSize(),
                    start = ConstraintSet {
                        val header = createRefFor("header")
                        val body = createRefFor("body")
                        val content1 = createRefFor("content1")
                        val content2 = createRefFor("content2")
                        val headerBar = createRefFor("headerBar")
                        constrain(header){
                            this.width = Dimension.matchParent
                            this.height = Dimension.value(200.dp)
                            this.top.linkTo(parent.top)
                        }
                        constrain(body){
                            this.width = Dimension.matchParent
                            this.height = Dimension.fillToConstraints
                            this.top.linkTo(header.bottom,0.dp)
                            this.bottom.linkTo(parent.bottom,0.dp)
                        }
                        constrain(content1){
                            this.start.linkTo(header.start, 20.dp)
                            this.top.linkTo(header.top,110.dp)
                            this.height = Dimension.fillToConstraints
                        }
                        constrain(content2){
                            this.start.linkTo(header.start)
                            this.end.linkTo(header.end)
                            this.bottom.linkTo(header.bottom,24.dp)
                        }
                        constrain(headerBar) {
                            this.start.linkTo(header.start)
                            this.top.linkTo(header.bottom)
                        }
                    },
                    end = ConstraintSet {
                        val header = createRefFor("header")
                        val body = createRefFor("body")
                        val content1 = createRefFor("content1")
                        val content2 = createRefFor("content2")
                        val headerBar = createRefFor("headerBar")
                        constrain(header){
                            this.height = Dimension.value(86.dp)
                            this.alpha = 0F
                        }
                        constrain(body){
                            this.width = Dimension.matchParent
                            this.height = Dimension.fillToConstraints
                            this.top.linkTo(header.bottom,0.dp)
                            this.bottom.linkTo(parent.bottom,0.dp)
                        }
                        constrain(content1){
                            this.start.linkTo(header.start)
                            this.end.linkTo(header.end)
                            this.top.linkTo(header.top,15.dp)
                            this.bottom.linkTo(header.bottom,5.dp)
                            this.height = Dimension.fillToConstraints
                            this.scaleX = 0.5F
                            this.scaleY = 0.5F
                        }
                        constrain(content2){
                            this.start.linkTo(content1.end,12.dp)
                            this.bottom.linkTo(header.bottom)
                            this.top.linkTo(header.top)
                        }
                        constrain(headerBar) {
                            this.start.linkTo(header.start)
                            this.top.linkTo(header.bottom)
                        }
                    },
                    progress = computedProgress,
                ) {

                    //리스트
                    Box(
                        modifier = Modifier
                            .layoutId("body")
                            .fillMaxWidth()
                            .background(Color.Green)
                    ){
                        //content, not necessarily scrollable or list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ){

                        }
                    }

                    //배경
                    val storageImage = painterResource(id = R.drawable.storage_header)
                    Image(
                        painter = storageImage,
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .layoutId("header")
                            .fillMaxWidth()
                            .height(startHeightNum.dp)
                        )

                    //헤더 콘텐츠 (보관함)
                    Text(
                        text = stringResource(R.string.storage),
                        style = getTextStyle(style = MoyaFont.CustomStorageHeaderTitle),
                        modifier = Modifier
                            .layoutId("content1")
                    )

                    Text(
                        text = "총 0곡",
                        style = getTextStyle(style = MoyaFont.CustomCaptionBold),
                        color = MoyaColor().darkGray,
                        modifier = Modifier.layoutId("headerBar")
                            .background(Color.White)
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                            .fillMaxWidth()
                    )
                    Divider(startIndent = 0.dp, thickness = 1.dp, color = MoyaColor().gray)
                }
            }
        }
    }
}

class MusicStorageScreen {
}