package com.evaluacion.kairos.ports.out;

import com.evaluacion.kairos.domain.Show;

import java.util.Optional;

public interface ShowRepositoryPort {
    Optional<Show> findById(Long id);
    Show save(Show show);
}
