package com.example.swipe.utils

/**
 * Sealed class representing different resource states in the application.
 *
 * @param T The type of data associated with the resource state.
 * @property data The actual data associated with the resource state.
 * @property message A message providing additional information about the resource state.
 */
sealed class ResourceState<T>(val data: T? = null, private val message: String? = null) {
    class Loading<T> : ResourceState<T>()
    class Success<T>(data: T) : ResourceState<T>(data)
    class Error<T>(data: T? = null, message: String?) : ResourceState<T>(data,message)
}