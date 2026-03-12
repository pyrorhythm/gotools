package com.pyro.gotools.settings.inlayHints

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.FormBuilder
import com.pyro.gotools.settings.inlayHints.Settings
import javax.swing.*

class Configurable : Configurable {
    private lateinit var chanStyleCombo: JComboBox<Settings.ChanStyle>
    private lateinit var chanTypeBracketsStyleCombo: JComboBox<Settings.ChanTypeBracketsStyle>
    private lateinit var ellipsisStyleCombo: JComboBox<Settings.EllipsisStyle>
    private lateinit var genericBracketCombo: JComboBox<Settings.GenericBracketStyle>
    private lateinit var pointerStyleCombo: JComboBox<Settings.PointerStyle>
    private lateinit var separatorStyleCombo: JComboBox<Settings.SeparatorStyle>
    private lateinit var funcLiteralStyleCombo: JComboBox<Settings.FuncLiteralStyle>
    private lateinit var insertSpaceOnLeftToggle: JCheckBox
    private lateinit var renderTypeParamsToggle: JCheckBox
    private lateinit var renderTypeParamsConstraintsToggle: JCheckBox
    private lateinit var maxHintLengthSpinner: JSpinner

    override fun getDisplayName(): String = "Inlay Hints"

    override fun createComponent(): JComponent {
        chanStyleCombo = ComboBox(Settings.ChanStyle.entries.toTypedArray()).apply {
            renderer = enumRenderer {
                when (it) {
                    Settings.ChanStyle.DEFAULT -> "Default (<-chan / chan<- / chan)"
                    Settings.ChanStyle.UNICODE -> "Unicode (← chan / → chan / ⇄ chan)"
                    Settings.ChanStyle.LITERAL -> "Literal (chan recv / chan send / chan bdir)"
                }
            }
        }

        ellipsisStyleCombo = ComboBox(Settings.EllipsisStyle.entries.toTypedArray()).apply {
            renderer = enumRenderer {
                when (it) {
                    Settings.EllipsisStyle.DEFAULT -> "Default (...)"
                    Settings.EllipsisStyle.UNICODE -> "Unicode (…)"
                    Settings.EllipsisStyle.UNICODE_MIDDLE -> "Unicode middle-aligned (⋯)"
                    Settings.EllipsisStyle.TILDE -> "Tilde (~)"
                }
            }
        }

        genericBracketCombo = ComboBox(Settings.GenericBracketStyle.entries.toTypedArray()).apply {
            renderer = enumRenderer {
                when (it) {
                    Settings.GenericBracketStyle.DEFAULT -> "Default - [int]"
                    Settings.GenericBracketStyle.UNICODE_ANGLED -> "Unicode angled - ⟨int⟩"
                    Settings.GenericBracketStyle.ASCII_ANGLED -> "ASCII angled - <int>"
                }
            }
        }

        pointerStyleCombo = ComboBox(Settings.PointerStyle.entries.toTypedArray()).apply {
            renderer = enumRenderer {
                when (it) {
                    Settings.PointerStyle.DEFAULT -> "Default (*int)"
                    Settings.PointerStyle.CARET -> "Caret (^int)"
                    Settings.PointerStyle.AMPERSAND -> "Ampersand (&int)"
                    Settings.PointerStyle.PTR_OF -> "Literal (ptrOf int)"
                }
            }
        }

        separatorStyleCombo = ComboBox(Settings.SeparatorStyle.entries.toTypedArray()).apply {
            renderer = enumRenderer {
                when (it) {
                    Settings.SeparatorStyle.DEFAULT -> "Default (a, b)"
                    Settings.SeparatorStyle.PIPE -> "Pipe (a | b)"
                    Settings.SeparatorStyle.SEMICOLON -> "Semicolon (a; b)"
                }
            }
        }

        funcLiteralStyleCombo = ComboBox(Settings.FuncLiteralStyle.entries.toTypedArray()).apply {
            renderer = enumRenderer {
                when (it) {
                    Settings.FuncLiteralStyle.DEFAULT -> "Default ( func(int) -> float )"
                    Settings.FuncLiteralStyle.SCIENTIFIC -> "Scientific ( ƒ(int) -> float )"
                    Settings.FuncLiteralStyle.NO -> "No literal ( (int) -> float )"
                }
            }
        }


        chanTypeBracketsStyleCombo = ComboBox(Settings.ChanTypeBracketsStyle.entries.toTypedArray()).apply {
            renderer = enumRenderer {
                when (it) {
                    Settings.ChanTypeBracketsStyle.DEFAULT -> "Default ( chan int )"
                    Settings.ChanTypeBracketsStyle.ROUND -> "Round ( chan(int) )"
                    Settings.ChanTypeBracketsStyle.SQUARE -> "Squared ( chan[int] )"
                    Settings.ChanTypeBracketsStyle.UNICODE_ANGLED -> "Unicode angled ( chan⟨int⟩ )"
                    Settings.ChanTypeBracketsStyle.ASCII_ANGLED -> "ASCII angled ( chan<int> )"
                }
            }
        }


        maxHintLengthSpinner = JSpinner(SpinnerNumberModel(60, 0, 500, 5)).apply {
            (editor as JSpinner.DefaultEditor).textField.columns = 4
        }

        insertSpaceOnLeftToggle = JCheckBox()
        renderTypeParamsToggle = JCheckBox()
        renderTypeParamsConstraintsToggle = JCheckBox()

        renderTypeParamsConstraintsToggle.isEnabled = renderTypeParamsToggle.isSelected
        renderTypeParamsToggle.addActionListener {
            renderTypeParamsConstraintsToggle.isEnabled = renderTypeParamsToggle.isSelected
            if (!renderTypeParamsToggle.isSelected) renderTypeParamsConstraintsToggle.isSelected = false
        }

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Max hint length (0 = unlimited)", maxHintLengthSpinner)
            .addSeparator()
            .addLabeledComponent("Insert one space on left for each hint", insertSpaceOnLeftToggle)
            .addLabeledComponent("Render type parameters", renderTypeParamsToggle)
            .addLabeledComponent("Also render their constraints", renderTypeParamsConstraintsToggle)
            .addSeparator()
            .addLabeledComponent("Channel arrows:", chanStyleCombo)
            .addLabeledComponent("Channel type brackets:", chanTypeBracketsStyleCombo)
            .addLabeledComponent("Variadic ellipsis:", ellipsisStyleCombo)
            .addLabeledComponent("Generic brackets:", genericBracketCombo)
            .addLabeledComponent("Pointer prefix:", pointerStyleCombo)
            .addLabeledComponent("Type separator:", separatorStyleCombo)
            .addLabeledComponent("Function literal:", funcLiteralStyleCombo)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val settings = Settings.getInstance()
        return chanStyleCombo.selectedItem != settings.chanStyle
                || ellipsisStyleCombo.selectedItem != settings.ellipsisStyle
                || genericBracketCombo.selectedItem != settings.genericBracketStyle
                || pointerStyleCombo.selectedItem != settings.pointerStyle
                || separatorStyleCombo.selectedItem != settings.separatorStyle
                || maxHintLengthSpinner.value != settings.state.maxHintLength
                || funcLiteralStyleCombo.selectedItem != settings.funcLiteralStyle
                || chanTypeBracketsStyleCombo.selectedItem != settings.chanTypeBracketsStyle
                || insertSpaceOnLeftToggle.isSelected != settings.state.insertSpaceOnLeft
                || renderTypeParamsToggle.isSelected != settings.state.renderTypeParams
                || renderTypeParamsConstraintsToggle.isSelected != settings.state.renderTypeParamsConstraints
    }

    override fun apply() {
        val state = Settings.getInstance().state
        state.chanStyle = chanStyleCombo.selectedItem as Settings.ChanStyle
        state.ellipsisStyle = ellipsisStyleCombo.selectedItem as Settings.EllipsisStyle
        state.genericBracketStyle = genericBracketCombo.selectedItem as Settings.GenericBracketStyle
        state.pointerStyle = pointerStyleCombo.selectedItem as Settings.PointerStyle
        state.separatorStyle = separatorStyleCombo.selectedItem as Settings.SeparatorStyle
        state.chanTypeBracketsStyle = chanTypeBracketsStyleCombo.selectedItem as Settings.ChanTypeBracketsStyle
        state.funcLiteralStyle = funcLiteralStyleCombo.selectedItem as Settings.FuncLiteralStyle
        state.maxHintLength = maxHintLengthSpinner.value as Int
        state.insertSpaceOnLeft = insertSpaceOnLeftToggle.isSelected
        state.renderTypeParams = renderTypeParamsToggle.isSelected
        state.renderTypeParamsConstraints = renderTypeParamsConstraintsToggle.isSelected
    }

    override fun reset() {
        val settings = Settings.getInstance()
        chanStyleCombo.selectedItem = settings.chanStyle
        ellipsisStyleCombo.selectedItem = settings.ellipsisStyle
        genericBracketCombo.selectedItem = settings.genericBracketStyle
        pointerStyleCombo.selectedItem = settings.pointerStyle
        separatorStyleCombo.selectedItem = settings.separatorStyle
        chanTypeBracketsStyleCombo.selectedItem = settings.chanTypeBracketsStyle
        funcLiteralStyleCombo.selectedItem = settings.funcLiteralStyle
        maxHintLengthSpinner.value = settings.state.maxHintLength
        insertSpaceOnLeftToggle.setSelected(settings.state.insertSpaceOnLeft)
        renderTypeParamsToggle.setSelected(settings.state.renderTypeParams)
        renderTypeParamsConstraintsToggle.setSelected(settings.state.renderTypeParamsConstraints)
    }

}

private inline fun <reified T : Enum<T>> enumRenderer(crossinline display: (T) -> String): ListCellRenderer<T> =
    DefaultListCellRenderer().let { delegate ->
        ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
            delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).also {
                if (value is T) delegate.text = display(value)
            }
        }
    }