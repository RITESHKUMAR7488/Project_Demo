package com.example.projectdemo.data.model

import com.google.gson.annotations.SerializedName

data class ProfileScreenData(
    @SerializedName("profile") val profile: Profile,
    @SerializedName("menuItems") val menuItems: List<ProfileMenuItem>
)

data class Profile(
    @SerializedName("initials") val initials: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String
)

data class ProfileMenuItem(
    @SerializedName("icon") val icon: String,
    @SerializedName("title") val title: String
)

/**
 * A sealed class to represent the different items in our RecyclerView.
 * It can be a Header (the profile card) or a menu item.
 */
sealed class ProfileListItem {
    data class Header(val profile: Profile) : ProfileListItem()
    data class Item(val menuItem: ProfileMenuItem) : ProfileListItem()
}