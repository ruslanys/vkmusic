package me.ruslanys.vkmusic.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.ruslanys.vkmusic.entity.Property
import me.ruslanys.vkmusic.property.DownloaderProperties
import me.ruslanys.vkmusic.property.Properties
import me.ruslanys.vkmusic.repository.PropertyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Service
class DefaultPropertyService(private val propertyRepository: PropertyRepository) : PropertyService {

    @PostConstruct
    private fun init() {
        if (get(DownloaderProperties::class.java) == null) {
            set(DownloaderProperties())
        }
    }

    @Transactional
    override fun <T : Properties> set(properties: T): T {
        val json = jacksonObjectMapper().writeValueAsString(properties)
        val entity = Property(properties.javaClass.simpleName, json)
        propertyRepository.save(entity)
        return properties
    }

    @Transactional(readOnly = true)
    override fun <T : Properties> get(clazz: Class<T>): T? {
        val entity = propertyRepository.findById(clazz.simpleName).orElse(null)
        return if (entity != null) {
            jacksonObjectMapper().readValue<T>(entity.json, clazz)
        } else null
    }

    @Transactional
    override fun <T : Properties> remove(clazz: Class<T>) {
        val key = clazz.simpleName
        if (propertyRepository.existsById(key)) {
            propertyRepository.deleteById(key)
        }
    }

}
