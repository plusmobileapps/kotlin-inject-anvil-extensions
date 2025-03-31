package com.plusmobileapps.sample.anvilkmp.util

fun interface Consumer<T> {
    fun emit(value: T)
}