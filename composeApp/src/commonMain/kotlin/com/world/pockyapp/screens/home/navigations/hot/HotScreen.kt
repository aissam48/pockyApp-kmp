package com.world.pockyapp.screens.home.navigations.hot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.mmk.kmpnotifier.notification.NotifierManager
import com.world.pockyapp.Constant.getUrl
import com.world.pockyapp.navigation.NavRoutes
import com.world.pockyapp.network.models.model.ProfileModel
import com.world.pockyapp.network.models.model.StreetModel
import com.world.pockyapp.screens.profile.convertPxToDp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_location_black
import kotlin.random.Random

@Composable
fun HotScreen(navController: NavHostController, viewModel: HotViewModel = koinViewModel()) {

    val streetState by viewModel.streetState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()
    val myProfile = remember { mutableStateOf(ProfileModel()) }

    when (val state = profileState) {
        is HotState.Success -> {
            myProfile.value = state.data
        }

        else -> {

        }
    }

    val screenSize = remember { mutableStateOf(Pair(-1, -1)) }

    LaunchedEffect(Unit) {
        viewModel.loadStreets()
        viewModel.getProfile()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(color = Color.White)
                .padding(start = 10.dp, end = 1.dp)
        ) {
            when (val state = streetState) {
                is HotState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                }

                is HotState.Error -> {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.height(150.dp))
                            Text(
                                text = state.error.message,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(50.dp))

                            Box(modifier = Modifier.background(
                                color = Color.Black,
                                shape = RoundedCornerShape(10.dp)
                            ).height(50.dp).width(100.dp).clickable {
                                viewModel.getProfile()
                                viewModel.loadStreets()
                            }) {
                                Text(
                                    text = "Reload",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    textAlign  = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                        }
                    }
                }

                is HotState.Success -> {
                    val hotMoments = state.data
                    hotMoments.sortedByDescending { it.moments.size }

                    item {
                        Text(
                            text = "Trending Places >",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                    }

                    if (hotMoments.isNotEmpty()) {


                        items(hotMoments.chunked(2)) { item ->

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                item.forEachIndexed { index, postModel ->

                                    HotMomentItem(
                                        street = item[index],
                                        navController = navController,
                                        screenSize = screenSize,
                                        myProfile = myProfile
                                    )
                                    Spacer(modifier = Modifier.size(7.dp))

                                }

                            }
                            Spacer(modifier = Modifier.size(10.dp))

                        }
                    } else {
                        item {
                            Spacer(modifier = Modifier.size(100.dp))
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "There are no moments in trending places",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                    }
                }
            }
            item {
                Layout(
                    modifier = Modifier.fillMaxWidth().height(0.dp),
                    measurePolicy = { measurables, constraints ->
                        // Use the max width and height from the constraints
                        val width = constraints.maxWidth
                        val height = constraints.maxHeight

                        screenSize.value = Pair(width, height)
                        println("Width: $width, height: $height")

                        layout(width, height) {

                        }
                    }
                )
            }
        }
    }


}

@Composable
fun HotMomentItem(
    street: StreetModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    screenSize: MutableState<Pair<Int, Int>>,
    myProfile: MutableState<ProfileModel>
) {
    val checkIfSeeAllMoments = street.moments.find { !it.viewed }
    Box(
        modifier = modifier
            .height(170.dp)
            .width((convertPxToDp(screenSize.value.first / 2) - 8).dp)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = if (checkIfSeeAllMoments != null) {
                        listOf(Color.Red, Color.Yellow, Color.White)
                    } else {
                        listOf(Color.Gray, Color.Gray, Color.Gray)
                    }
                ),
                shape = RoundedCornerShape(10.dp)
            ),
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            backgroundColor = Color.LightGray,
            modifier = Modifier
                .height(170.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = getUrl(street.moments[0].momentID),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        val modulesJson = Json.encodeToString(listOf(street))
                            .replace("/", "%")
                        navController.navigate(NavRoutes.MOMENTS_BY_LOCATION.route + "/${modulesJson}" + "/0" + "/${myProfile.value.id}")
                        // navController.navigate(NavRoutes.MOMENTS.route + "/${modulesJson}" + "/0" + "/$currentUserId")
                    },
                contentDescription = null
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(5.dp).align(Alignment.BottomCenter).fillMaxWidth()
        ) {

            Image(
                painter = painterResource(Res.drawable.ic_location_black),
                contentDescription = null,
                modifier = Modifier.background(color = Color.White, shape = CircleShape).size(20.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = street.geoLocation.street,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
