package com.htetaunglin.rxjava.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.htetaunglin.rxjava.R
import com.htetaunglin.rxjava.module.GlideApp
import com.htetaunglin.rxjava.retrofit.models.Ticket
import kotlinx.android.synthetic.main.ticket_row.view.*

class TicketAdapter(
    var ticketList: List<Ticket> = emptyList(),
    var ticketSelectListener: (Ticket) -> Unit = {}
) :
    RecyclerView.Adapter<TicketAdapter.MyViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ticket_row, parent, false)
            .let {
                context = parent.context
                MyViewHolder(it)
            }

    override fun getItemCount() = ticketList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val ticket = ticketList[position]

        with(holder.itemView) {

            GlideApp.with(context)
                .load(ticket.airline?.logo)
                .placeholder(R.drawable.holder)
                .error(R.drawable.holder)
                .centerCrop() to logo

            airline_name.text = ticket.airline?.name

            departure.text = ticket.departure

            arrival.text = ticket.arrival

            duration.text = "${ticket.flightNumber}, ${ticket.duration}"

            number_of_stops.text = "${ticket.numberOfStops} Stops"

            if (TextUtils.isEmpty(ticket.instructions)) {
                duration.append(", ${ticket.instructions}")
            }

            if (ticket.price != null) {
                price.text = "₹ " + String.format("%.0f", ticket.price?.price?.toFloat())
                number_of_seats.text = "${ticket.price?.seats} Seats"
                loader.visibility = View.INVISIBLE
            } else {
                loader.visibility = View.VISIBLE
            }

            this.setOnClickListener { ticketSelectListener(ticket) }
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}