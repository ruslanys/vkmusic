package me.ruslanys.vkmusic.service

import me.ruslanys.vkmusic.property.Properties

interface PropertyService {

    fun <T : Properties> set(properties: T): T

    fun <T : Properties> get(clazz: Class<T>): T?

    fun <T : Properties> remove(clazz: Class<T>)

}
