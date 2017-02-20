package de.tud.feedback.plugin.repository;


import de.tud.feedback.plugin.domain.NeoDevice;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeoDeviceRepository extends GraphRepository<NeoDevice> {

    /**
     * Finds the given device by the device id
     * @param deviceId
     * @return
     */
    NeoDevice findByDeviceId(String deviceId);

}
