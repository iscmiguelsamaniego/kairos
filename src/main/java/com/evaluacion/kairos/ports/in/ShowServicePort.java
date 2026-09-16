package com.evaluacion.kairos.ports.in;

import com.evaluacion.kairos.domain.Show;
import java.util.List;

public interface ShowServicePort {
    List<Show> searchShows(String query);
}