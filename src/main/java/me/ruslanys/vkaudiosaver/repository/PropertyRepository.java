package me.ruslanys.vkaudiosaver.repository;

import me.ruslanys.vkaudiosaver.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
}
