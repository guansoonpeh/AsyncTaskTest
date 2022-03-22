package com.example.asynctasktest

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.asynctasktest.databinding.ActivityMainBinding
import java.lang.ref.WeakReference
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    var myVariable = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDoAsync.setOnClickListener {
            val task = MyAsyncTask(this)
            task.execute(10)
        }
    }

    private inner class MyAsyncTask internal constructor(context: MainActivity) : AsyncTask<Int, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<MainActivity> = WeakReference(context)

            override fun onPreExecute() {
                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg params: Int?): String? {
                publishProgress("Sleeping Started") // Calls onProgressUpdate()
                try {
                    val time = params[0]?.times(1000)
                    time?.toLong()?.let { Thread.sleep(it / 2) }
                    publishProgress("Half Time") // Calls onProgressUpdate()
                    time?.toLong()?.let { Thread.sleep(it / 2) }
                    publishProgress("Sleeping Over") // Calls onProgressUpdate()
                    resp = "Android was sleeping for " + params[0] + " seconds"
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    resp = e.message
                } catch (e: Exception) {
                    e.printStackTrace()
                    resp = e.message
                }

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return
                binding.progressBar.visibility = View.GONE
                binding.textView.text = result.let { it }
                activity.myVariable = 100
            }

            override fun onProgressUpdate(vararg text: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                Toast.makeText(activity, text.firstOrNull(), Toast.LENGTH_SHORT).show()

            }
        }
}