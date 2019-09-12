package com.harlie.batbot.ui.control

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
import androidx.annotation.Nullable
import com.harlie.batbot.databinding.ControlFragmentBinding


class ControlFragment : Fragment() {
    val TAG = "LEE: <" + ControlFragment::class.java.getName() + ">";

    companion object {
        val instance = ControlFragment()
        fun getInstance(): Fragment {
            return instance
        }
    }

    private var m_robotCommand = RobotCommandModel("", "")
    private lateinit var m_controlFragBinding : ControlFragmentBinding
    private lateinit var m_controlViewModel: Control_ViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView");
        m_controlFragBinding = DataBindingUtil.inflate(
            inflater, com.harlie.batbot.R.layout.control_fragment, container, false
        )
        m_controlFragBinding.robotCommand = m_robotCommand
        m_controlFragBinding.lifecycleOwner = viewLifecycleOwner
        val view = m_controlFragBinding.getRoot()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            m_controlViewModel = ViewModelProviders.of(it).get(Control_ViewModel::class.java)
        }
        m_controlViewModel.m_inputCommand.observe(this, Observer {
            it?.let {
                Log.d(TAG, "it.m_robotCommand=" + it.robotCommand)
                m_robotCommand.robotCommand = it.robotCommand
            }
        })
    }
}
