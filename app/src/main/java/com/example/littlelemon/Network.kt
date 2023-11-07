package com.example.littlelemon

/* This file contains network data models.
JSON data will be converted to these models.

Following is the sample data: A JSON dictionary contains an array of menu items.
{
  // ArrayList can use [] or {}, so we can define menu as a List<MenuItem>
  "menu": [
    //MenuItem object {properties}
    {
      "id": 1,
      "title": "Spinach Artichoke Dip",
      "price": "10"
    },
    {
      "id": 2,
      "title": "Hummus",
      "price": "10"
    },
    ...
  ]
}
*/


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Following 2 classes represents the menu being decoded from the server result.
 * Add properties to both data classes to match the structure of the JSON given at the beginning of this step:
 */

/** decode JSON object into class-- "menu": ArrayList<MenuItemNetwork>*/
@Serializable
data class MenuNetwork(
    // add code here
    @SerialName("menu") //@SerialName("key") --> in order to map correspondingly
    val menuItems: List<MenuItemNetwork>
)

/** decode each JSON MenuItemNetwork list object into class*/
@Serializable
data class MenuItemNetwork(
    // add code here
    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String,

    @SerialName("price")
    val price: Double
) {
    /** Convert MenuItemNetwork to MenuItemRoom
     * toMenuItemRoom function should use all of the properties of the MenuItemNetwork class and
     * return an instance of the MenuItemRoom class that can be saved to the database.
     */
    fun toMenuItemRoom() = MenuItemRoom( //use serialized JSON data to initial an MenuItemRoom obj
        // add code here
        id,
        title,
        price
    )
}
