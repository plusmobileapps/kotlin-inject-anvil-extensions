package com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Visibility

internal fun KSClassDeclaration.checkIsPublic(logger: KSPLogger) {
    check(getVisibility() == Visibility.PUBLIC) {
        "Contributed component interfaces must be public.".also {
            logger.error(it, this)
        }
    }
}