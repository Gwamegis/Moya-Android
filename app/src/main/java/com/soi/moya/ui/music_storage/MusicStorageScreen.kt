package com.soi.moya.ui.music_storage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.layoutId
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.soi.moya.R
import com.soi.moya.models.StoredMusic
import com.soi.moya.models.Team
import com.soi.moya.models.toMusic
import com.soi.moya.ui.AppViewModelProvider
import com.soi.moya.ui.component.MusicListItem
import com.soi.moya.ui.listItem_menu.ListItemMenuScreen
import com.soi.moya.ui.theme.MoyaColor
import com.soi.moya.ui.theme.MoyaFont
import com.soi.moya.ui.theme.getTextStyle
import kotlinx.coroutines.launch

enum class SwipingStates {
    EXPANDED,
    COLLAPSED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicStorageScreen(
    viewModel: MusicStorageViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
) {

    val storageUiState by viewModel.storageUiState.collectAsState()
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
                        ),
                        enabled = storageUiState.itemList.isNotEmpty()
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
                scalingLayout(computedProgress = computedProgress) {
                    headerContent(
                        viewModel = viewModel,
                        startHeightNum = viewModel.startHeightNum,
                        storageUiState = storageUiState
                    )
                    bodyContent(
                        storageUiState = storageUiState,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun ItemList(
    storageMusicItems: List<StoredMusic>,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(storageMusicItems) { item ->
            ItemView(
                music = item,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemView(
    music: StoredMusic,
    navController: NavHostController,
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    MusicListItem(
        music = music.toMusic(),
        team = Team.valueOf(music.team),
        buttonImageResourceId = R.drawable.ellipse,
        onClickCell = {
            navController.navigate("MUSIC_PLAYER/${music.team}/${music.id}")
        },
        onClickExtraButton = {
            showBottomSheet = true
        }
    )
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(12.dp),
            containerColor = MoyaColor.background,
            dragHandle = {},
            windowInsets = WindowInsets.navigationBars
        ) {
            Box(modifier = Modifier.navigationBarsPadding()) {
                ListItemMenuScreen(
                    music = music.toMusic(),
                    //TODO: 팀정보 연결
                    team = Team.doosan,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun emptyList() {
    val image = painterResource(id = R.drawable.storage_empty)
    Image(
        painter = image,
        contentDescription = "storage is empty.",
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
    )
}

@Composable
fun scalingLayout(computedProgress: Float,content: @Composable MotionLayoutScope.() -> Unit) {
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
                this.top.linkTo(headerBar.bottom,0.dp)
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
                this.top.linkTo(headerBar.bottom,0.dp)
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
        content = content
    )
}

@Composable
fun headerContent(
    viewModel: MusicStorageViewModel,
    startHeightNum: Int,
    storageUiState: StorageUiState
) {
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
        text = stringResource(R.string.music_storage),
        style = getTextStyle(style = MoyaFont.CustomStorageHeaderTitle),
        modifier = Modifier
            .layoutId("content1")
    )

    Text(
        text = "총 ${storageUiState.itemList.count()} 곡",
        style = getTextStyle(style = MoyaFont.CustomCaptionBold),
        color = MoyaColor.darkGray,
        modifier = Modifier
            .layoutId("headerBar")
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .fillMaxWidth()
    )
    Divider(startIndent = 0.dp, thickness = 1.dp, color = MoyaColor.gray)
}

@Composable
fun bodyContent(
    storageUiState: StorageUiState,
    navController: NavHostController
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .layoutId("body")
            .fillMaxWidth()
    ){
        //content, not necessarily scrollable or list
        if (storageUiState.itemList.isNotEmpty()) {
            ItemList(
                storageMusicItems = storageUiState.itemList,
                navController = navController
            )
        } else {
            emptyList()
        }
    }
}