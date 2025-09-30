package com.example.ia3_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.example.ia3_4.ui.theme.Ia3_4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ia3_4Theme {
                ContactScreen()
            }
        }
    }
}


data class Contact(val name: String, val phone: String)
data class NavItem(val label: String, val icon: ImageVector)
val navItems = listOf(
    NavItem("Home", Icons.Filled.Home),
    NavItem("Settings", Icons.Filled.Settings),
    NavItem("Profile", Icons.Filled.Person),
)


fun generateName(): String {
    val vowels = listOf("a", "e", "i", "o", "u")
    val consonants = listOf("b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "r", "s", "t", "v", "w", "y", "z")

    val numS = Random.nextInt(2, 4)
    val nameBuilder = StringBuilder()
    var isConsonant = Random.nextBoolean()

    for (i in 0 until numS) {
        if (isConsonant) {
            nameBuilder.append(consonants[Random.nextInt(consonants.size)])
            if (Random.nextBoolean() || i == numS - 1) {
                nameBuilder.append(vowels[Random.nextInt(vowels.size)])
            }
        } else {
            nameBuilder.append(vowels[Random.nextInt(vowels.size)])
            if (Random.nextBoolean() || i == numS - 1) {
                nameBuilder.append(consonants[Random.nextInt(consonants.size)])
            }
        }
        isConsonant = !isConsonant
    }
    if (nameBuilder.length < 2) {
        return generateName()
    }
    return nameBuilder.toString().replaceFirstChar { it.uppercase() }
}


fun generateContacts(): List<Contact> {
    val contacts = mutableListOf<Contact>()
    var phoneNum = 1111111
    val numToGenerate = 70
    val generatedFullNames = mutableSetOf<String>()
    while (generatedFullNames.size < numToGenerate){
        val firstN = generateName()
        val lastN = generateName()
        val fullName = "$firstN $lastN"
        if (!generatedFullNames.contains(fullName)) {
            generatedFullNames.add(fullName)
            val phone = String.format("111-%04d", phoneNum++)
            contacts.add(Contact(fullName, phone))
        }
    }
    return contacts.sortedBy {it.name}
}


fun groupItem(contacts: List<Contact>): Map<Char, List<Contact>> {
    val sortedContacts = contacts.sortedBy { it.name }
    return sortedContacts.groupBy { it.name.first().uppercaseChar() }
}


@Composable
fun HeaderItem(char: Char) {
    Text(
        text = char.toString(),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}


@Composable
fun ContactItemR(contact: Contact) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.first().uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = contact.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = contact.phone,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Divider(modifier = Modifier.padding(start = 16.dp))
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactScreen() {
    val groupedContacts = remember { groupItem(generateContacts()) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
       snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar =  {
            TopAppBar(
                title = {Text("My Contacts")},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        label = { Text(item.label) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "click FAB, current tab: ${navItems[selectedItemIndex].label}",
                            actionLabel = "Dismiss",
                            withDismissAction = true
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(Icons.Filled.Add, "add new contact")
            }
        },
        content = { paddingValues ->
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                groupedContacts.forEach{ (char, contactsInGroup) ->
                    stickyHeader(key = char) {
                        HeaderItem(char)
                    }
                    items(
                        items = contactsInGroup,
                        key = { contact -> "contact:${contact.phone}"}
                    ) { contact ->
                        ContactItemR(contact)
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    Ia3_4Theme {
        ContactScreen()
    }
}