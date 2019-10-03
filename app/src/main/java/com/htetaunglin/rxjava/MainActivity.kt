package com.htetaunglin.rxjava

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.htetaunglin.rxjava.adapter.TicketAdapter
import com.htetaunglin.rxjava.retrofit.ApiClient
import com.htetaunglin.rxjava.retrofit.services.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.htetaunglin.rxjava.retrofit.models.Ticket
import kotlinx.android.synthetic.main.content_main.*
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList
import kotlin.math.roundToInt




class MainActivity : AppCompatActivity() {

    private val from = "DEL"
    private val to = "HYD"
    private val TAG = MainActivity::class.simpleName


    private lateinit var service : ApiService

    private lateinit var mAdapter: TicketAdapter

    private val ticketList = ArrayList<Ticket>()

    private val disposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        service = ApiClient.createService(ApiService::class.java)

        initToolbar()
        setUpAdapter()
        initRecycler()

        rxLoad()


    }

    private fun rxLoad() {
        val ticketObservable = getTicket(from, to).replay()

        disposable.add(
            ticketObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Ticket>>() {
                    override fun onComplete() {

                    }

                    override fun onNext(t: List<Ticket>) {
                        ticketList.clear()
                        ticketList.addAll(t)
                        mAdapter.notifyDataSetChanged()
                    }

                    override fun onError(e: Throwable) {
                        showError(e)
                    }

                })
        )


        disposable.add(
            ticketObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it)
                }.flatMap {
                    getPriceObservable(it)
                }.subscribeWith(object : DisposableObserver<Ticket>() {
                    override fun onComplete() {

                    }

                    override fun onNext(t: Ticket) {
                        val position = ticketList.indexOf(t)

                        if (position == -1) {
                            // TODO - take action
                            // Ticket not found in the list
                            // This shouldn't happen
                            return
                        }

                        ticketList[position] = t
                        mAdapter.notifyItemChanged(position)
                    }

                    override fun onError(e: Throwable) {
                        showError(e)
                    }
                })
        )

        ticketObservable.connect()
    }

    private fun showError(e: Throwable) {
        Log.e(TAG, "showError: " + e.message)
    }

    private fun getPriceObservable(ticket: Ticket) =
        service.getPrice(ticket.flightNumber ?: "no", ticket.from ?: "from", ticket.to ?: "to")
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                ticket.apply { price = it }
            }

    private fun getTicket(from: String, to: String) =
        service.searchTickets(from, to)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    private fun setUpAdapter() {
        mAdapter = TicketAdapter(ticketList)
    }


    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "$from > $to"
    }

    private fun initRecycler() {
        val mLayoutManager = GridLayoutManager(this, 1)
        recycler.layoutManager = mLayoutManager
        recycler.addItemDecoration(GridSpacingItemDecoration(1, dpToPx(5), true))
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.adapter = mAdapter
    }


    inner class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).roundToInt()
    }


    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

}
