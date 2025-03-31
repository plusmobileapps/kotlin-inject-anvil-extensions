package com.plusmobileapps.kotlin.inject.anvil.assistedfactory.util

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.Annotatable
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import software.amazon.lastmile.kotlin.inject.anvil.internal.Origin
import kotlin.reflect.KClass

/**
 * Returns the qualified name of the receiver [KClass].
 */
fun KClass<*>.requireQualifiedName(): String = requireNotNull(qualifiedName) {
    "Qualified name was null for $this"
}

/**
 * Return `software.amazon.Test` into `SoftwareAmazonTest`.
 */
fun KSClassDeclaration.safeClassName(logger: KSPLogger): String {
    val qualifiedName = qualifiedName?.asString()
    requireNotNull(qualifiedName) {
        "Qualified name must not be null for $this".also {
            logger.error(it, this)
        }
    }
    return qualifiedName
        .split(".")
        .joinToString(separator = "") { it.capitalize() }
}

fun KSDeclaration.requireContainingFile(
    logger: KSPLogger,
): KSFile = requireNotNull(containingFile) {
    "Containing file was null for $this".also {
        logger.error(it, this)
    }
}

/**
 * Adds an [Origin] annotation to the given [clazz].
 */
fun <T : Annotatable.Builder<T>> Annotatable.Builder<T>.addOriginAnnotation(
    clazz: KSClassDeclaration,
): T = addAnnotation(
    AnnotationSpec.builder(Origin::class)
        .addMember("value = %T::class", clazz.toClassName())
        .build(),
)

fun KSDeclaration.innerClassNames(
    logger: KSPLogger,
    separator: String = ""
): String {
    val classNames = requireQualifiedName(logger).substring(packageName.asString().length + 1)
    return classNames.replace(".", separator)
}

fun KSDeclaration.requireQualifiedName(
    logger: KSPLogger,
): String {
    val qualifiedName = qualifiedName?.asString()
    requireNotNull(qualifiedName) {
        "Qualified name for $this cannot be null".also {
            logger.error(it, this)
        }
    }
    return qualifiedName
}

fun KSClassDeclaration.findAnnotations(
    annotation: KClass<out Annotation>,
    logger: KSPLogger,
): List<KSAnnotation> {
    val fqName = annotation.requireQualifiedName()
    return annotations.filter { it.isAnnotation(fqName, logger) }.toList()
}

fun KSAnnotation.isAnnotation(
    fqName: String,
    logger: KSPLogger,
): Boolean {
    return annotationType.resolve().declaration.requireQualifiedName(logger) == fqName
}