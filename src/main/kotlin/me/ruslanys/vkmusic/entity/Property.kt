package me.ruslanys.vkmusic.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Entity
class Property(
        @Id
        var id: String,

        @Column(nullable = false)
        var json: String
)
