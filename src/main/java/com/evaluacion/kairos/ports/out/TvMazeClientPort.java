package com.evaluacion.kairos.ports.out;

import com.evaluacion.kairos.domain.Show;

import java.util.List;
import java.util.Optional;

public interface TvMazeClientPort {
    List<Show> searchShows(String query);
    Optional<Show> getShowById(Long id);
}