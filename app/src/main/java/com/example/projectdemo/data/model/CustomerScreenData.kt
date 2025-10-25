package com.example.projectdemo.data.model

import com.google.gson.annotations.SerializedName

// This is the top-level object from Customers.json
data class CustomerScreenData(
    @SerializedName("customerGroups") val customerGroups: List<CustomerGroup>
)

data class CustomerGroup(
    @SerializedName("groupTitle") val groupTitle: String,
    @SerializedName("customers") val customers: List<Customer>
)


data class Customer(
    @SerializedName("id") val id: String,
    @SerializedName("initials") val initials: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("price") val price: String,
    @SerializedName("tags") val tags: List<CustomerTag>
)

data class CustomerTag(
    @SerializedName("icon") val icon: String, // We can use this later to show icons
    @SerializedName("label") val label: String
)

/**
 * A sealed class to represent the different items in our RecyclerView.
 * It can be either a Header (like "Today") or a Customer item.
 */
sealed class CustomerListItem {
    data class HeaderItem(val title: String) : CustomerListItem()
    data class CustomerItem(val customer: Customer) : CustomerListItem()
}