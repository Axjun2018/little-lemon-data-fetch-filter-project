package com.example.littlelemon

/*
* This file represents the screen UI and holds the entire application logic.
* It also contains httpClient which will be used to retrieve data from
* the network and an instance of the local database used to store the retrieved data.
*/

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.littlelemon.ui.theme.LittleLemonTheme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    /** initializes httpClient on Android with ContentNegotiation plugin*/
    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(contentType = ContentType("text", "plain"))
        }
    }

    /** initialize database by room builder */
    private val database by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LittleLemonTheme {
                // add databaseMenuItems code here: to observe local database menu items as state
                val databaseMenuItems by database.menuItemDao().getAll().observeAsState(emptyList())

                /** Sorting by name */
                // add orderMenuItems variable here, event/input can cause state changes, so using by remember
                var orderMenuItems by remember { mutableStateOf(false) }
                // add menuItems variable here
                var menuItems =
                    if(orderMenuItems) databaseMenuItems.sortedBy { it.title }
                    else databaseMenuItems

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo",
                        modifier = Modifier.padding(50.dp)
                    )
                    // add Button code here
                    Button(
                        onClick = { orderMenuItems = true},
                        modifier = Modifier.padding(90.dp, 10.dp)
                        ) {
                        Text(text = "Tap to Order By Name")
                    }

                    // add searchPhrase variable here: store information on the search phrase entered by the user.
                    var searchPhrase by remember { mutableStateOf("")}

                    // Add OutlinedTextField
                    OutlinedTextField(value = searchPhrase,
                        onValueChange = {
                            searchPhrase = it },
                        label = { Text("🔍 search") },
                        modifier = Modifier.fillMaxWidth().padding(50.dp, 10.dp)
                    )

                    // add is not empty check here
                    if (searchPhrase != ""){
                        // store the filtered result to a variable
                        var filteredMenuItems = menuItems.filter {
                            it.title.lowercase().contains(searchPhrase)
                        }
                       MenuItemsList(filteredMenuItems)
                    }

                    MenuItemsList(menuItems)
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            if (database.menuItemDao().isEmpty()) {
                // add code here
                saveMenuToDatabase(fetchMenu())
            }
        }
    }

    /** create asynchronous function to fetch the data from URL and convert JSON to class object*/
    private suspend fun fetchMenu(): MenuNetwork{ //fetch entire JSON data to map on the MenuNetwork type here
        //Retrieve data
        // data URL: https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonSimpleMenu.json

        // the return type of body() depends on how you've set up Ktor and the content negotiation in your project.
        // In this case, the body() function returns a List<MenuItemNetwork>, which is the deserialized JSON data.
        return httpClient.get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonSimpleMenu.json")
            .body()
    }

    /**
     * convert network menu items to Room menu items and
     * save them to the database.
     */
    private fun saveMenuToDatabase(menuItemsNetwork: MenuNetwork) { // pass MenuNetwork type
        val menuItemsRoom = menuItemsNetwork.menuItems.map { it.toMenuItemRoom() } // map MenuNetwork.menuItems
        database.menuItemDao().insertAll(*menuItemsRoom.toTypedArray())
    }
}

@Composable
private fun MenuItemsList(items: List<MenuItemRoom>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 20.dp)
    ) {
        items(
            items = items,
            itemContent = { menuItem ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(menuItem.title)
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp),
                        textAlign = TextAlign.Right,
                        text = "%.2f".format(menuItem.price)
                    )
                }
            }
        )
    }
}
