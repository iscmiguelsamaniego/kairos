package com.evaluacion.kairos.application;

import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.in.ShowServicePort;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowUseCase implements ShowServicePort {

    private final TvMazeClientPort tvMazeClientPort;

    public ShowUseCase(TvMazeClientPort tvMazeClientPort) {
        this.tvMazeClientPort = tvMazeClientPort;
    }

    @Override
    public List<Show> searchShows(String query) {

        return tvMazeClientPort.searchShows(query);
    }

    @Override
    public Show getShowById(Long id) {
    return tvMazeClientPort.getShowById(id)
            .orElseThrow(() -> new RuntimeException("Show not found"));
    }

}