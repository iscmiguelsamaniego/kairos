package com.evaluacion.kairos.infrastructure.outbound.persistence;

import com.evaluacion.kairos.domain.Show;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowRepositoryAdapterTest {

    @Mock
    private ShowMongoRepository mongoRepository;

    @InjectMocks
    private ShowRepositoryAdapter showRepositoryAdapter;

    @Test
    @DisplayName("Should find show by ID and map entity to domain successfully")
    void shouldFindByIdSuccessfully() {
        // Arrange
        Long showId = 10L;
        ShowEntity entity = new ShowEntity();
        entity.setId(showId);
        entity.setName("Breaking Bad");
        entity.setChannel("AMC");
        entity.setSummary("Chemistry teacher turns into drug kingpin");
        entity.setGenres(List.of("Drama", "Crime"));

        when(mongoRepository.findById(showId)).thenReturn(Optional.of(entity));

        // Act
        Optional<Show> result = showRepositoryAdapter.findById(showId);

        // Assert
        assertTrue(result.isPresent());
        Show show = result.get();
        assertEquals(showId, show.id());
        assertEquals("Breaking Bad", show.name());
        assertEquals("AMC", show.channel());
        assertEquals("Chemistry teacher turns into drug kingpin", show.summary());
        assertEquals(List.of("Drama", "Crime"), show.genres());

        verify(mongoRepository).findById(showId);
    }

    @Test
    @DisplayName("Should return empty when show ID does not exist in repository")
    void shouldReturnEmptyWhenFindByIdNotFound() {
        // Arrange
        Long showId = 99L;
        when(mongoRepository.findById(showId)).thenReturn(Optional.empty());

        // Act
        Optional<Show> result = showRepositoryAdapter.findById(showId);

        // Assert
        assertFalse(result.isPresent());
        verify(mongoRepository).findById(showId);
    }

    @Test
    @DisplayName("Should map domain to entity, save, and return mapped domain show")
    void shouldSaveShowSuccessfully() {
        // Arrange
        Long showId = 15L;
        Show domainShow = new Show(
                showId,
                "The Wire",
                "HBO",
                "Baltimore drug scene drama",
                List.of("Crime", "Drama")
        );

        ShowEntity savedEntity = new ShowEntity();
        savedEntity.setId(showId);
        savedEntity.setName("The Wire");
        savedEntity.setChannel("HBO");
        savedEntity.setSummary("Baltimore drug scene drama");
        savedEntity.setGenres(List.of("Crime", "Drama"));

        when(mongoRepository.save(any(ShowEntity.class))).thenReturn(savedEntity);

        // Act
        Show result = showRepositoryAdapter.save(domainShow);

        // Assert
        assertNotNull(result);
        assertEquals(showId, result.id());
        assertEquals("The Wire", result.name());
        assertEquals("HBO", result.channel());
        assertEquals("Baltimore drug scene drama", result.summary());
        assertEquals(List.of("Crime", "Drama"), result.genres());

        ArgumentCaptor<ShowEntity> entityCaptor = ArgumentCaptor.forClass(ShowEntity.class);
        verify(mongoRepository).save(entityCaptor.capture());

        ShowEntity captured = entityCaptor.getValue();
        assertEquals(showId, captured.getId());
        assertEquals("The Wire", captured.getName());
    }
}