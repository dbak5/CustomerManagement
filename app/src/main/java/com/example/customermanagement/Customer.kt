package com.example.customermanagement

class Customer(
    val id: Int = 0,
    val name: String,
    val email: String? = null,
    val mobile: String? = null,
) {

    override fun toString(): String {
        return "$id: $name: $email: $mobile"
    }
}