package de.tud.feedback.plugin.repository;


import de.tud.feedback.plugin.domain.NeoPeer;
import de.tud.feedback.plugin.domain.NeoPeerMetric;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Repo for the metrics
 */
@Repository
public interface NeoPeerMetricRepository extends GraphRepository<NeoPeerMetric> {
}
