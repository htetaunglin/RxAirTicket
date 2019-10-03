package com.htetaunglin.rxjava.retrofit.services

import com.htetaunglin.rxjava.retrofit.models.Price
import com.htetaunglin.rxjava.retrofit.models.Ticket
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("airline-tickets.php")
    fun searchTickets(@Query("from") from: String, @Query("to") to: String): Single<List<Ticket>>

    @GET("airline-tickets-price.php")
    fun getPrice(@Query("flight_number") flightNumber: String, @Query("from") from: String, @Query("to") to: String): Single<Price>
}