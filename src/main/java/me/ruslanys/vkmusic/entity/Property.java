package me.ruslanys.vkmusic.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Data
@NoArgsConstructor

@Entity
public class Property {

    @Id
    private String id;

    @Column(nullable = false)
    private String json;

    public Property(String key, String json) {
        this.id = key;
        this.json = json;
    }

}
