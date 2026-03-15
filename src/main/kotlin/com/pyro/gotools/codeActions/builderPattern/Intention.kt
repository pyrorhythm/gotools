package com.pyro.gotools.codeActions.builderPattern

import com.goide.intentions.GoBaseIntentionAction
import com.goide.intentions.generate.constructor.GoMemberChooser
import com.goide.intentions.generate.constructor.GoMemberChooserNode
import com.goide.psi.GoNamedElement
import com.goide.psi.GoStructType
import com.goide.psi.GoType
import com.goide.psi.GoTypeDeclaration
import com.goide.psi.GoTypeParameters
import com.goide.psi.GoTypeSpec
import com.goide.psi.impl.GoPsiUtil
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ObjectUtils
import com.intellij.util.containers.ContainerUtil
import com.pyro.gotools.codeActions.builderPattern.util.generateBuilder

data class GoStructField(val name: String, val type: GoType?)

class Intention : GoBaseIntentionAction() {
    override fun getText(): String = "Generate struct builder"

    override fun getFamilyName(): String = "GoTools"

    override fun startInWriteAction(): Boolean = false

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        val typeSpec = findTypeSpec(element) ?: return false
        if (!GoPsiUtil.isTopLevelDeclaration(typeSpec)) return false
        return typeSpec.specType.type is GoStructType
    }

    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val typeSpec = findTypeSpec(element) ?: return
        if (!GoPsiUtil.isTopLevelDeclaration(typeSpec)) return

        val structType = ObjectUtils.tryCast(typeSpec.specType.type, GoStructType::class.java) ?: return
        val structName = typeSpec.name ?: return

        val typeParameters = typeSpec.specType.typeParameters
        val typeParamDecl = typeParameters?.text
        val typeParamRef = buildTypeParamRef(typeParameters)

        val definitions = ContainerUtil.filter(structType.fieldDefinitions) { !it.isBlank }
        if (definitions.isEmpty()) return

        val chooserNodes = ContainerUtil.map2Array(definitions, GoMemberChooserNode::class.java) { GoMemberChooserNode(it) }
        val chooser = GoMemberChooser(chooserNodes, project, null)
        chooser.title = "Select Fields for Builder"
        if (!chooser.showAndGet()) return

        val selectedFields = chooser.selectedElements
            .mapNotNull { node ->
                val named = ObjectUtils.tryCast(node.psiElement, GoNamedElement::class.java) ?: return@mapNotNull null
                val name = named.name ?: return@mapNotNull null
                val type = named.getGoType(ResolveState.initial()) ?: return@mapNotNull null
                GoStructField(name, type)
            }
        if (selectedFields.isEmpty()) return

        val typeDeclaration = PsiTreeUtil.getParentOfType(typeSpec, GoTypeDeclaration::class.java) ?: return
        val generatedCode = generateBuilder(structName, selectedFields, typeParamDecl, typeParamRef)

        WriteAction.run<RuntimeException> {
            val document = editor.document
            document.insertString(typeDeclaration.textRange.endOffset, generatedCode)
            PsiDocumentManager.getInstance(project).commitDocument(document)

            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
            if (psiFile != null) {
                CodeStyleManager.getInstance(project).reformat(psiFile)
            }
        }
    }

    private fun findTypeSpec(element: PsiElement): GoTypeSpec? {
        val parent = PsiTreeUtil.getNonStrictParentOfType(element, GoTypeSpec::class.java, GoTypeDeclaration::class.java)
        return when (parent) {
            is GoTypeSpec -> parent
            is GoTypeDeclaration -> parent.typeSpecList.singleOrNull()
            else -> null
        }
    }

    private fun buildTypeParamRef(typeParameters: GoTypeParameters?): String? = typeParameters?.let {
        buildString {
            append("[")
            typeParameters.typeParameterDeclarationList.forEachIndexed { declIndex, decl ->
                decl.typeParamDefinitionList.forEachIndexed { defIndex, def ->
                    def.name?.let { append(it) }
                    if (defIndex != decl.typeParamDefinitionList.lastIndex) append(", ")
                }
                if (declIndex != typeParameters.typeParameterDeclarationList.lastIndex) append(", ")
            }
            append("]")
        }
    }
}