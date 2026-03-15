package com.pyro.gotools.codeActions.builderPattern.util

import com.pyro.gotools.codeActions.builderPattern.GoStructField

fun generateBuilder(
    structName: String,
    fields: List<GoStructField>,
    typeParamDecl: String?,
    typeParamRef: String?
): String = buildString {
    val decl = typeParamDecl ?: ""
    val ref = typeParamRef ?: ""
    val builderName = "${structName.replaceFirstChar { it.uppercase() }}Builder"
    val implName = "${structName.replaceFirstChar { it.lowercase() }}Builder"
    val recv = structName.first().lowercase()

    appendLine()

    val used = mutableSetOf<String>()
    val fieldMethod = mutableMapOf<GoStructField, String>()

    appendLine("type $builderName$decl interface {")
    for (f in fields) {
        val initM = "With${f.name.replaceFirstChar { it.uppercase() }}"
        var m = initM
        var i = 1
        while (used.contains(m)) {
            m = initM + i.toString()
            i++
        }

        used.add(m)
        fieldMethod[f] = m
        appendLine("\t$m(${f.type?.text}) $builderName$ref")
    }
    appendLine("\tB() *$structName$ref")
    appendLine("}")

    used.clear()
    val fieldName = mutableMapOf<GoStructField, String>()

    appendLine()
    appendLine("type $implName$decl struct {")
    for (f in fields) {
        var m = f.name.replaceFirstChar { it.lowercase() }
        var i = 1
        while (used.contains(m)) {
            m = f.name.lowercase() + i.toString()
            i++
        }

        used.add(m)
        fieldName[f] = m

        appendLine("\t$m ${f.type?.text}")
    }
    appendLine("}")

    appendLine()
    appendLine("func New$builderName$decl() $builderName$ref {")
    appendLine("\treturn &$implName$ref{}")
    appendLine("}")

    for (f in fields) {
        val m = fieldMethod[f]
        var p = f.name[0].lowercase()
        if (p == recv) { p = recv.repeat(2) }

        appendLine()
        appendLine("func ($recv *$implName$ref) $m($p ${f.type?.text}) $builderName$ref {")
        appendLine("\t$recv.${fieldName[f]} = $p")
        appendLine("\treturn $recv")
        appendLine("}")
    }

    appendLine()
    appendLine("func ($recv *$implName$ref) B() *$structName$ref {")
    appendLine("\treturn &$structName$ref{")
    for (f in fields) {
        appendLine("\t\t${f.name}: $recv.${fieldName[f]},")
    }
    appendLine("\t}")
    appendLine("}")
}