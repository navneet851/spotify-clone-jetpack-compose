package com.music.stream.neptune.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.music.stream.neptune.R
import com.music.stream.neptune.data.preferences.addLikedSongId
import com.music.stream.neptune.data.preferences.isSongLiked
import com.music.stream.neptune.data.preferences.removeLikedSongId
import com.music.stream.neptune.di.Palette
import com.music.stream.neptune.di.SongPlayer
import com.music.stream.neptune.ui.navigation.Routes
import com.music.stream.neptune.ui.theme.AppBackground
import com.music.stream.neptune.ui.theme.GridBackground
import com.music.stream.neptune.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay

@Composable
fun Loader() {
    Column(Modifier
        .background(Color(AppBackground.toArgb())),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(45.dp),
            color = Color(0xFF4A4AC4)
        )
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MiniPlayer(navController: NavHostController) {
    val miniPlayerViewModel : PlayerViewModel = hiltViewModel()
    val songTitle = miniPlayerViewModel.currentSongTitle.value
    val songSinger = miniPlayerViewModel.currentSongSinger.value
    val songCoverUri = miniPlayerViewModel.currentSongCoverUri.value
    var songPlayingState = miniPlayerViewModel.currentSongPlayingState.value
    val songId = miniPlayerViewModel.currentSongId.value
    val songIndex = miniPlayerViewModel.currentSongIndex.value
    val songAlbum = miniPlayerViewModel.currentSongAlbum.value


    var songProgress by remember { mutableFloatStateOf(0f) }

    songProgress = if (SongPlayer.getDuration() > 0) {
        SongPlayer.getCurrentPosition().toFloat() / SongPlayer.getDuration().toFloat()
    } else {
        0f
    }

    val currentRoute = navController.currentBackStackEntry?.destination?.route



    LaunchedEffect(key1  = songPlayingState) {
            while (songPlayingState) {
                songProgress = SongPlayer.getCurrentPosition().toFloat() / SongPlayer.getDuration().toFloat()
                delay(300L) // update every .00 second

                if (songProgress > 0f && songProgress >= 1f && currentRoute != Routes.Player.route) {
                    navController.navigate(Routes.Player.route)
                    songPlayingState = false
                }
        }
    }

    val context = LocalContext.current

    var darkVibrantColor by remember {
        mutableStateOf(Color(GridBackground.toArgb()))
    }
    Palette().extractFirstColorFromImageUrl(context = context, songCoverUri){ color ->
        darkVibrantColor = color
    }

    var isLiked by remember {
        mutableStateOf(false)
    }
    val likeState = miniPlayerViewModel.likeState.value
    LaunchedEffect(likeState, songId) {
        isLiked = isSongLiked(context, songId.toString())
    }


    Column(
        modifier = Modifier

            .padding(13.dp, 0.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(darkVibrantColor)
            .padding(8.dp, 0.dp)
            .clipToBounds()

    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                //.background(Color.Green)
                .padding(0.dp, 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    navController.navigate(Routes.Player.route)
                }
        ){

            Row(horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(260.dp)
                    .clipToBounds()
            ) {
                GlideImage(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 10.dp, 0.dp)
                        .size(42.dp)
                        .clip(RoundedCornerShape(6.dp))
                    ,
                    model = songCoverUri,
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.placeholder),
                    loading = placeholder(R.drawable.placeholder),
                    contentDescription = ""
                )
                Column(
                    modifier = Modifier
                        .widthIn(Dp.Unspecified, 200.dp)
                ) {
                    Text(text = songTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold )
                    Text(text = songSinger, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(70.dp)
            ) {

                Icon(
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (isLiked) {
                                removeLikedSongId(context, songId.toString())
                            } else {
                                addLikedSongId(context, songId.toString())
                            }
                            isLiked = isSongLiked(context, songId.toString())
                            miniPlayerViewModel.updateLikeState(!likeState)

                        },
                    painter = if (isLiked){
                        painterResource(id = R.drawable.added)
                    }
                    else{
                        painterResource(id = R.drawable.ic_add)
                    },
                    tint = Color.White,
                    contentDescription = ""
                )


                Icon(
                    painter = if (songPlayingState)
                        painterResource(id = R.drawable.ic_playing)
                    else
                        painterResource(id = R.drawable.play_svgrepo_com)
                    ,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(5.dp, 0.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (songPlayingState) {
                                SongPlayer.pause()
                                miniPlayerViewModel.updateSongState(
                                    songCoverUri,
                                    songTitle,
                                    songSinger,
                                    false,
                                    songId,
                                    songIndex,
                                    songAlbum
                                )
                            } else {
                                SongPlayer.play()
                                miniPlayerViewModel.updateSongState(
                                    songCoverUri,
                                    songTitle,
                                    songSinger,
                                    true,
                                    songId,
                                    songIndex,
                                    songAlbum
                                )
                            }
                        }
                )
            }


        }


        CustomSlider(
            value = songProgress,
            onValueChange = { newValue ->
                SongPlayer.seekTo((newValue * SongPlayer.getDuration()).toLong())
            },
            valueRange = 0f..1f,
            steps = 0,
            modifier = Modifier
                .fillMaxWidth()
            ,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.Gray
            )
        )
    }



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    colors: SliderColors = SliderDefaults.colors(),
) {
    Box(
        //contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxWidth()
            .height(0.dp)

    ) {

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = colors,
            thumb = {
                SliderDefaults.Thumb( //androidx.compose.material3.SliderDefaults
                    interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.align(Alignment.Center),
                    colors = colors,
                    thumbSize = DpSize(4.dp, 4.dp)
                )
            }
        )
    }
}


@Composable
fun Snackbar(showMessage : String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        Text(
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            color = Color.Black,
            text = showMessage
        )
    }
}