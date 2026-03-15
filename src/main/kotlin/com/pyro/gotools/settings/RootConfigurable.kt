package com.pyro.gotools.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class RootConfigurable : Configurable {

    override fun getDisplayName(): String = "GoTools"

    override fun createComponent(): JComponent = panel {
        row {
            label("Configure GoTools in the subcategories below.")
        }

        row {
            label("Created with <3 by ")
            browserLink("@pyrorhythm", "https://github.com/pyrorhythm")
        }
    }

    override fun isModified(): Boolean = false

    override fun apply() {}
}