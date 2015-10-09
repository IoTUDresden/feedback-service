package de.tud.feedback.repository.index;

import de.tud.feedback.index.HistoricalState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface HistoricalStateRepository extends CrudRepository<HistoricalState, String> {
}
