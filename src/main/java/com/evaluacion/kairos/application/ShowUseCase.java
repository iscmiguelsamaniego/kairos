package com.evaluacion.kairos.application;

import com.evaluacion.kairos.domain.Comment;
import com.evaluacion.kairos.domain.Show;
import com.evaluacion.kairos.ports.in.ShowServicePort;
import com.evaluacion.kairos.ports.out.CommentRepositoryPort;
import com.evaluacion.kairos.ports.out.ShowRepositoryPort;
import com.evaluacion.kairos.ports.out.TvMazeClientPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowUseCase implements ShowServicePort {

    private final TvMazeClientPort tvMazeClientPort;
    private final ShowRepositoryPort showRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;

    public ShowUseCase(TvMazeClientPort tvMazeClientPort,
                       ShowRepositoryPort showRepositoryPort,
                       CommentRepositoryPort commentRepositoryPort) {
        this.tvMazeClientPort = tvMazeClientPort;
        this.showRepositoryPort = showRepositoryPort;
        this.commentRepositoryPort = commentRepositoryPort;
    }

    @Override
    public List<Show> searchShows(String query) {

        List<Show> externalShows = tvMazeClientPort.searchShows(query);

        return externalShows.stream().map(show -> {
            List<Comment> comments = commentRepositoryPort.findCommentsByShowId(show.id());
            return new Show(
                    show.id(),
                    show.name(),
                    show.channel(),
                    show.summary(),
                    show.genres(),
                    comments
            );
        }).toList();
    }

    @Override
    public Show getShowById(Long id) {
        Show show = showRepositoryPort.findById(id)
                .orElseGet(() -> {
                    Show externalShow = tvMazeClientPort.getShowById(id)
                            .orElseThrow(() -> new RuntimeException("Show not found"));

                    return showRepositoryPort.save(externalShow);
                });

        List<Comment> comments = commentRepositoryPort.findCommentsByShowId(id);

        return new Show(
                show.id(),
                show.name(),
                show.channel(),
                show.summary(),
                show.genres(),
                comments
        );
    }

    @Override
    public void addComment(Long showId, String comment, Integer rating) {
        getShowById(showId);
        commentRepositoryPort.saveComment(showId, comment, rating);
    }
}