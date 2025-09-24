package com.world.pockyapp.screens.followers

import androidx.compose.runtime.Composable

import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.world.pockyapp.Constant.getUrl
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pockyapp.composeapp.generated.resources.Res
import pockyapp.composeapp.generated.resources.ic_placeholder

data class FollowerItem(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val profileImageUrl: String,
    val isOnline: Boolean = false,
    val isVerified: Boolean = false,
    val mutualFriendsCount: Int = 0,
    val followingSince: String = "",
    val isFollowingBack: Boolean = false,
    val lastSeen: String = ""
)

enum class FollowerSortType {
    RECENT, ALPHABETICAL, MUTUAL_FRIENDS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersListScreen(
    navController: NavHostController,
    viewModel: FollowersViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSortMenu by remember { mutableStateOf(false) }
    var sortType by remember { mutableStateOf(FollowerSortType.RECENT) }
    var selectedFollowers by remember { mutableStateOf(setOf<String>()) }
    var isSelectionMode by remember { mutableStateOf(false) }

    // Mock data - replace with actual data from ViewModel
    val followers = remember { generateMockFollowers() }

    val filteredAndSortedFollowers = remember(searchQuery, sortType, followers) {
        val filtered = followers.filter { follower ->
            follower.firstName.contains(searchQuery, ignoreCase = true) ||
                    follower.lastName.contains(searchQuery, ignoreCase = true) ||
                    follower.username.contains(searchQuery, ignoreCase = true)
        }

        when (sortType) {
            FollowerSortType.RECENT -> filtered.sortedByDescending { it.followingSince }
            FollowerSortType.ALPHABETICAL -> filtered.sortedBy { it.firstName }
            FollowerSortType.MUTUAL_FRIENDS -> filtered.sortedByDescending { it.mutualFriendsCount }
        }
    }

    // Modern color scheme
    val backgroundColor = Color(0xFFF8F9FA)
    val cardBackground = Color.White
    val primaryGold = Color(0xFFDFC46B)
    val textPrimary = Color(0xFF212529)
    val textSecondary = Color(0xFF6C757D)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            FollowersTopBar(
                title = "Followers",
                subtitle = "${filteredAndSortedFollowers.size} followers",
                onBackClick = { navController.popBackStack() },
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                sortType = sortType,
                onSortClick = { showSortMenu = true },
                isSelectionMode = isSelectionMode,
                selectedCount = selectedFollowers.size,
                onSelectionModeToggle = {
                    isSelectionMode = !isSelectionMode
                    if (!isSelectionMode) selectedFollowers = emptySet()
                },
                textColor = textPrimary,
                backgroundColor = cardBackground,
                primaryColor = primaryGold
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (filteredAndSortedFollowers.isEmpty()) {
                EmptyFollowersState(
                    searchQuery = searchQuery,
                    backgroundColor = cardBackground,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredAndSortedFollowers,
                        key = { it.id }
                    ) { follower ->
                        FollowerCard(
                            follower = follower,
                            isSelected = selectedFollowers.contains(follower.id),
                            isSelectionMode = isSelectionMode,
                            onCardClick = {
                                if (isSelectionMode) {
                                    selectedFollowers = if (selectedFollowers.contains(follower.id)) {
                                        selectedFollowers - follower.id
                                    } else {
                                        selectedFollowers + follower.id
                                    }
                                } else {
                                    // Navigate to user profile
                                    navController.navigate("profile/${follower.id}")
                                }
                            },
                            onFollowBackClick = {
                                // Handle follow back action
                            },
                            onRemoveClick = {
                                // Handle remove follower action
                            },
                            backgroundColor = cardBackground,
                            primaryColor = primaryGold,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Sort Menu
            if (showSortMenu) {
                SortDropdownMenu(
                    currentSort = sortType,
                    onSortSelected = {
                        sortType = it
                        showSortMenu = false
                    },
                    onDismiss = { showSortMenu = false }
                )
            }

            // Selection Mode FAB
            if (isSelectionMode && selectedFollowers.isNotEmpty()) {
                SelectionActionFAB(
                    selectedCount = selectedFollowers.size,
                    onRemoveSelected = {
                        // Handle bulk remove action
                        selectedFollowers = emptySet()
                        isSelectionMode = false
                    },
                    primaryColor = primaryGold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun FollowersTopBar(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortType: FollowerSortType,
    onSortClick: () -> Unit,
    isSelectionMode: Boolean,
    selectedCount: Int,
    onSelectionModeToggle: () -> Unit,
    textColor: Color,
    backgroundColor: Color,
    primaryColor: Color
) {
    var isSearchMode by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row with back button and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }

                if (isSelectionMode) {
                    Text(
                        text = "$selectedCount selected",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(onClick = onSelectionModeToggle) {
                        Text(
                            text = "Cancel",
                            color = primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = subtitle,
                            fontSize = 14.sp,
                            color = Color(0xFF6C757D)
                        )
                    }

                    // Action buttons
                    Row {
                        IconButton(onClick = { isSearchMode = !isSearchMode }) {
                            Icon(
                                imageVector = if (isSearchMode) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = textColor
                            )
                        }

                        IconButton(onClick = onSortClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Sort",
                                tint = textColor
                            )
                        }

                        IconButton(onClick = onSelectionModeToggle) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Select",
                                tint = textColor
                            )
                        }
                    }
                }
            }

            // Search bar
            if (isSearchMode && !isSelectionMode) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search followers...", color = Color(0xFF6C757D)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF6C757D)
                        )
                    },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF6C757D)
                                )
                            }
                        }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFFE9ECEF)
                    ),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun FollowerCard(
    follower: FollowerItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onCardClick: () -> Unit,
    onFollowBackClick: () -> Unit,
    onRemoveClick: () -> Unit,
    backgroundColor: Color,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 6.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isSelected) primaryColor.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.08f)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) primaryColor.copy(alpha = 0.1f) else backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox or profile image
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onCardClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = primaryColor,
                        uncheckedColor = textSecondary
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Profile image with online indicator
            Box {
                AsyncImage(
                    model = getUrl(follower.profileImageUrl),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            if (isSelected) primaryColor else Color.Transparent,
                            CircleShape
                        ),
                    placeholder = painterResource(Res.drawable.ic_placeholder),
                    error = painterResource(Res.drawable.ic_placeholder)
                )

                // Online indicator
                if (follower.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color(0xFF28A745), CircleShape)
                            .border(2.dp, backgroundColor, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }


            }

            Spacer(modifier = Modifier.width(12.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${follower.firstName} ${follower.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "@${follower.username}",
                    fontSize = 14.sp,
                    color = textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Additional info
                if (follower.mutualFriendsCount > 0) {
                    Text(
                        text = "${follower.mutualFriendsCount} mutual friends",
                        fontSize = 12.sp,
                        color = primaryColor,
                        fontWeight = FontWeight.Medium
                    )
                } else if (!follower.isOnline && follower.lastSeen.isNotEmpty()) {
                    Text(
                        text = "Last seen ${follower.lastSeen}",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }
            }

            // Action buttons
            if (!isSelectionMode) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!follower.isFollowingBack) {
                        OutlinedButton(
                            onClick = onFollowBackClick,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = primaryColor
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(primaryColor, primaryColor)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = "Follow",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFollowersState(
    searchQuery: String,
    backgroundColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (searchQuery.isEmpty()) "👥" else "🔍",
                fontSize = 64.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (searchQuery.isEmpty())
                    "No followers yet"
                else
                    "No followers found",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (searchQuery.isEmpty())
                    "When people follow you, they'll appear here"
                else
                    "Try a different search term",
                fontSize = 16.sp,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SortDropdownMenu(
    currentSort: FollowerSortType,
    onSortSelected: (FollowerSortType) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
    ) {
        FollowerSortType.values().forEach { sortType ->
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentSort == sortType) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFFDFC46B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Spacer(modifier = Modifier.width(24.dp))
                        }

                        Text(
                            text = when (sortType) {
                                FollowerSortType.RECENT -> "Most Recent"
                                FollowerSortType.ALPHABETICAL -> "A to Z"
                                FollowerSortType.MUTUAL_FRIENDS -> "Mutual Friends"
                            },
                            fontWeight = if (currentSort == sortType) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                },
                onClick = { onSortSelected(sortType) }
            )
        }
    }
}

@Composable
private fun SelectionActionFAB(
    selectedCount: Int,
    onRemoveSelected: () -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onRemoveSelected,
        modifier = modifier,
        containerColor = Color(0xFFE53E3E),
        contentColor = Color.White,
        text = {
            Text(
                text = "Remove ($selectedCount)",
                fontWeight = FontWeight.SemiBold
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove selected"
            )
        }
    )
}

// Mock data generator
private fun generateMockFollowers(): List<FollowerItem> {
    return listOf(
        FollowerItem(
            id = "1",
            firstName = "Sarah",
            lastName = "Johnson",
            username = "sarah.j",
            profileImageUrl = "profile1.jpg",
            isOnline = true,
            isVerified = true,
            mutualFriendsCount = 5,
            followingSince = "2023-12-01",
            isFollowingBack = true
        ),
        FollowerItem(
            id = "2",
            firstName = "Mike",
            lastName = "Chen",
            username = "mike.chen",
            profileImageUrl = "profile2.jpg",
            isOnline = false,
            mutualFriendsCount = 0,
            followingSince = "2023-11-15",
            isFollowingBack = false,
            lastSeen = "2 hours ago"
        ),
        FollowerItem(
            id = "3",
            firstName = "Emma",
            lastName = "Wilson",
            username = "emma.w",
            profileImageUrl = "profile3.jpg",
            isOnline = true,
            mutualFriendsCount = 12,
            followingSince = "2023-10-20",
            isFollowingBack = true
        ),
        FollowerItem(
            id = "4",
            firstName = "David",
            lastName = "Rodriguez",
            username = "david.r",
            profileImageUrl = "profile4.jpg",
            isOnline = false,
            isVerified = true,
            mutualFriendsCount = 3,
            followingSince = "2023-09-05",
            isFollowingBack = false,
            lastSeen = "1 day ago"
        ),
        FollowerItem(
            id = "5",
            firstName = "Lisa",
            lastName = "Kim",
            username = "lisa.kim",
            profileImageUrl = "profile5.jpg",
            isOnline = true,
            mutualFriendsCount = 8,
            followingSince = "2023-08-12",
            isFollowingBack = true
        )
    )
}




/*@Composable
fun FollowersScreen(
    navController: NavHostController,
    viewModel: FollowersViewModel = koinViewModel(),
    id: String
) {

    val followersState = viewModel.followersState.collectAsState()
    LaunchedEffect(Unit){
        viewModel.getFollowers(id)
    }



}*/