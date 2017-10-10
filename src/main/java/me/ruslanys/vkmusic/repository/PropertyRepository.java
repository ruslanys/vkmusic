package me.ruslanys.vkmusic.repository;

import me.ruslanys.vkmusic.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
}
