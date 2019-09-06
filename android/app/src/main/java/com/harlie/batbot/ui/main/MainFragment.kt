package com.harlie.batbot.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.harlie.batbot.R
import com.harlie.batbot.util.CacheManager

class MainFragment : Fragment() {
    val TAG = "LEE: <" + MainFragment::class.java.getName() + ">";

    private val cacheManager: CacheManager = CacheManager.getInstance()

    companion object {
        val instance = MainFragment()
        fun getInstance(): Fragment {
            return instance
        }
    }

    private lateinit var mainViewModel: MainViewModel
    private var textOutput: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.main_fragment, container, false)
        this.textOutput = view.findViewById(R.id.textOutput) as TextView

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mainViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

        mainViewModel.inputCommand.observe(this, Observer {
            it?.let {
                Log.d(TAG, "it=" + it)
                setTextOutput(it)
            }
        })
    }

    fun setTextOutput(s: String?) {
        textOutput!!.text = s
    }

}
