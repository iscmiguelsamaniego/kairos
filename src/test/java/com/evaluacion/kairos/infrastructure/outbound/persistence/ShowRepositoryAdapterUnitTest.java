package com.evaluacion.kairos.infrastructure.outbound.persistence;

import com.evaluacion.kairos.domain.Show;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ShowRepositoryAdapterUnitTest {

    private ShowMongoRepository mongoRepository;
    private ShowRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        mongoRepository = mock(ShowMongoRepository.class);
        repositoryAdapter = new ShowRepositoryAdapter(mongoRepository);
    }

    @Test
    @DisplayName("Unit: ShowRepositoryAdapter - Finds and maps entity to domain when present")
    void shouldFindAndMapShowToDomain() {
        // Arrange
        Long showId = 1L;
        ShowEntity entity = new ShowEntity();
        entity.setId(showId);
        entity.setName("Breaking Bad");
        entity.setChannel("AMC");
        entity.setSummary("Chemistry teacher turns into drug kingpin.");
        entity.setGenres(List.of("Drama", "Crime"));

        when(mongoRepository.findById(showId)).thenReturn(Optional.of(entity));

        // Act
        Optional<Show> result = repositoryAdapter.findById(showId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(showId);
        assertThat(result.get().name()).isEqualTo("Breaking Bad");
        assertThat(result.get().channel()).isEqualTo("AMC");
        assertThat(result.get().genres()).contains("Drama", "Crime");
        verify(mongoRepository, times(1)).findById(showId);
    }

    @Test
    @DisplayName("Unit: ShowRepositoryAdapter - Returns empty when show is not found in repository")
    void shouldReturnEmptyWhenNotFound() {
        // Arrange
        Long showId = 99L;
        when(mongoRepository.findById(showId)).thenReturn(Optional.empty());

        // Act
        Optional<Show> result = repositoryAdapter.findById(showId);

        // Assert
        assertThat(result).isEmpty();
        verify(mongoRepository, times(1)).findById(showId);
    }

    @Test
    @DisplayName("Unit: ShowRepositoryAdapter - Maps domain to entity and saves successfully")
    void shouldMapAndSaveShow() {
        // Arrange
        Show show = new Show(1L, "Stranger Things", "Netflix", "Kids save town.", List.of("Sci-Fi"));

        ShowEntity entityToSave = new ShowEntity();
        entityToSave.setId(show.id());
        entityToSave.setName(show.name());
        entityToSave.setChannel(show.channel());
        entityToSave.setSummary(show.summary());
        entityToSave.setGenres(show.genres());

        when(mongoRepository.save(any(ShowEntity.class))).thenReturn(entityToSave);

        // Act
        Show savedShow = repositoryAdapter.save(show);

        // Assert
        assertThat(savedShow).isNotNull();
        assertThat(savedShow.id()).isEqualTo(1L);
        assertThat(savedShow.name()).isEqualTo("Stranger Things");

        ArgumentCaptor<ShowEntity> captor = ArgumentCaptor.forClass(ShowEntity.class);
        verify(mongoRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getChannel()).isEqualTo("Netflix");
    }
}