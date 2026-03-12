package com.pyro.gotools.settings

import com.intellij.openapi.options.Configurable
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JLayer
import javax.swing.JPanel

class RootConfigurable : Configurable {

    override fun getDisplayName(): String = "GoTools"

    override fun createComponent(): JComponent = FormBuilder
        .createFormBuilder()
        .addComponent(JLabel("Configure gotools settings in the subcategories below."))
        .addSeparator()
        .addComponent(JLabel("created with <3 by @pyrorhythm"))
        .addComponentFillVertically(JPanel(), 0)
        .panel

    override fun isModified(): Boolean = false

    override fun apply() {}
}