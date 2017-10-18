package me.ruslanys.vkmusic.repository;

import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {

    List<Audio> findAllByOrderByPositionAsc();

    List<Audio> findByStatusOrderByPositionAsc(DownloadStatus status);
}
