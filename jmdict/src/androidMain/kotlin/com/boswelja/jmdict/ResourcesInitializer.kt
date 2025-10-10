package com.boswelja.jmdict

import android.content.Context
import android.content.res.Resources
import androidx.startup.Initializer

internal class ResourcesInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        resources = context.resources
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    companion object {
        internal lateinit var resources: Resources
    }
}