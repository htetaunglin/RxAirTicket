package com.htetaunglin.rxjava.retrofit.models

import com.google.gson.annotations.SerializedName

data class Price(
	val price: Int? = null,

	@SerializedName(value = "flight_number")
	val flightNumber: String? = null,

	val currency: String? = null,

	val from: String? = null,

	val to: String? = null,

	val seats: Int? = null
)
