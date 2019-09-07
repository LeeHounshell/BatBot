package com.harlie.batbot.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.harlie.batbot.model.RobotCommandModel
import com.harlie.batbot.util.CacheManager
import androidx.annotation.Nullable
import com.harlie.batbot.databinding.MainFragmentBinding


class MainFragment : Fragment() {
    val TAG = "LEE: <" + MainFragment::class.java.getName() + ">";

    companion object {
        val instance = MainFragment()
        fun getInstance(): Fragment {
            return instance
        }
    }

    private lateinit var fragBinding : MainFragmentBinding
    private lateinit var mainViewModel: MainViewModel

    private var robotCommand = RobotCommandModel("", "")
    private val cacheManager: CacheManager = CacheManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView");
        fragBinding = DataBindingUtil.inflate(
            inflater, com.harlie.batbot.R.layout.main_fragment, container, false
        )
        fragBinding.robotCommand = robotCommand
        fragBinding.setLifecycleOwner(this)
        val view = fragBinding.getRoot()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mainViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }
        mainViewModel.inputCommand.observe(this, Observer {
            it?.let {
                Log.d(TAG, "it.robotCommand=" + it.robotCommand)
                robotCommand.robotCommand = it.robotCommand
            }
        })
    }
}
