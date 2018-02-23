package com.emilabraham.marveltimemachine

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_comic_cover.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Response
import java.util.logging.Logger

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ComicCover : AppCompatActivity() {
    private val log = Logger.getLogger(ComicCover::class.java.name)
    private val api: RestApi = RestApi()
    private val mHideHandler = Handler()
    private var timeline: MutableMap<String, MutableList<Comic>> = mutableMapOf<String, MutableList<Comic>>()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        loopThroughYears()
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log.info("Hello World!!!!!!")

        setContentView(R.layout.activity_comic_cover)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        dummy_button.setOnTouchListener(mDelayHideTouchListener)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    //Return the date range for the given year
    fun getDateRange(today: DateTime): String {
        var dateRangeString: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        var yesterday = today.minusDays(1)

        var dateRange = dateRangeString.print(yesterday) + "," + dateRangeString.print(today)

        return dateRange
    }

    //Create dateRange array from given date until first ever published comic
    fun createDateArray(today: DateTime): List<String> {
        var firstComicDate = DateTime(1939, 10, 1, 0, 0)
        var currentDate = today
        var dates: MutableList<String> = mutableListOf<String>()
        dates.add(getDateRange(currentDate))

        while (currentDate.isAfter(firstComicDate)) {
            currentDate = currentDate.minusYears(1)
            //For that last date
            if (currentDate.isAfter(firstComicDate)) {
                dates.add(getDateRange(currentDate))
            }
        }


        return dates
    }

    //Loop through the years and make the calls to api
    //Should update timeline
    private fun loopThroughYears() {
        var today = DateTime()
        createDateArray(today).forEach { iterDate ->
            ComicBackgroundCall().execute(iterDate)
        }
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }

    inner class ComicBackgroundCall : AsyncTask<String, String, Response<MarvelApiResponse>>() {
        override fun doInBackground(vararg p0: String): Response<MarvelApiResponse> {
            //TODO: Move the loop to outside the call and only pass in a single dateRange.
            var callResponse = api.getComic(p0[0])
            //response.body() Is automatically type MarvelApiResponse
            var response = callResponse.execute()

            log.info("Getting comics for the year: " + p0[0])
            return response
        }

        override fun onPostExecute(result: Response<MarvelApiResponse>) {
            //TODO: Create/update the map and views and shit here.
            var comicsInAYear = result.body()!!.data.results.size
            if (result.isSuccessful && comicsInAYear > 0) {
                log.info("Number of comics this year: " + result.body()?.data?.results?.size.toString())
                val myComics = result.body()?.data?.results?.forEach { comic ->
                    log.info(comic.title)

                    //Update the map
                    if (timeline.containsKey(getDateRange(comic.getSaleDate()))) { //TODO: Include the date in Comic
                        timeline.get(getDateRange(comic.getSaleDate()))!!.add(comic)
                    } else {
                        var newComicList: MutableList<Comic> = mutableListOf<Comic>()
                        newComicList.add(comic)
                        timeline.put(getDateRange(comic.getSaleDate()), newComicList)
                    }
                }

            } else {
                log.info("No comics for this year")
            }
        }

        //Return the date range for the given year
        fun getDateRange(today: DateTime): String {
            var dateRangeString: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
            var yesterday = today.minusDays(1)

            var dateRange = dateRangeString.print(yesterday) + "," + dateRangeString.print(today)

            return dateRange
        }

        //Create dateRange array from given date until first ever published comic
        fun createDateArray(today: DateTime): List<String> {
            var firstComicDate = DateTime(1939, 10, 1, 0, 0)
            var currentDate = today
            var dates: MutableList<String> = mutableListOf<String>()
            dates.add(getDateRange(currentDate))

            while (currentDate.isAfter(firstComicDate)) {
                currentDate = currentDate.minusYears(1)
                //For that last date
                if (currentDate.isAfter(firstComicDate)) {
                    dates.add(getDateRange(currentDate))
                }
            }


            return dates
        }
    }
}
