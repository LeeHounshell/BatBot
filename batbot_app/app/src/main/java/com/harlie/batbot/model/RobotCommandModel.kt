// Copyright (c) 2019, Lee Hounshell. All rights reserved.

package com.harlie.batbot.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.harlie.batbot.BR
import kotlin.properties.Delegates


class RobotCommandModel(rc: String, cp: String) : BaseObservable() {
    val TAG = "LEE: <" + RobotCommandModel::class.java.getName() + ">"

    @get:Bindable var robotCommand: String by Delegates.observable(rc)
    { prop, old, new ->
        notifyPropertyChanged(BR.robotCommand)
    }
    @get:Bindable var commandPriority: String by Delegates.observable(cp)
    { prop, old, new ->
        notifyPropertyChanged(BR.robotCommand)
    }

    override fun toString(): String {
        return "RobotCommandModel(robotCommand="+robotCommand+", commandPriority="+commandPriority+")"
    }
}
