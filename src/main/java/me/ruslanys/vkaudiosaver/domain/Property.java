package me.ruslanys.vkaudiosaver.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String json;

    public Property(String key, String json) {
        this.id = key;
        this.json = json;
    }

}
