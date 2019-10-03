package com.htetaunglin.rxjava.retrofit.models

import com.google.gson.annotations.SerializedName

data class Ticket(
    val duration: String? = null,

    val instructions: String? = null,

    val arrival: String? = null,

    @SerializedName(value = "flight_number")
    val flightNumber: String? = null,

    val from: String? = null,

    val to: String? = null,

    val departure: String? = null,

    @SerializedName("stops")
    val numberOfStops: Int? = null,

    val airline: Airline? = null,

    var price: Price? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        return if (other !is Ticket) {
            false
        } else flightNumber.equals(other.flightNumber)

    }

    override fun hashCode(): Int {
        var hash = 3
        hash = 53 * hash + if (this.flightNumber != null) this.flightNumber.hashCode() else 0
        return hash
    }
}