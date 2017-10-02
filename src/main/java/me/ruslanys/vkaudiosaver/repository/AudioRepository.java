package me.ruslanys.vkaudiosaver.repository;

import me.ruslanys.vkaudiosaver.domain.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Repository
public interface AudioRepository extends JpaRepository<Audio, Integer> {
}
