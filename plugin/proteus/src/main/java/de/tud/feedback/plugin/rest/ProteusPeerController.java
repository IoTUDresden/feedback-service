package de.tud.feedback.plugin.rest;

import de.tud.feedback.plugin.ProteusMonitorAgent;
import eu.vicci.process.distribution.core.PeerProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Peer controller for api which can be accessed by proteus
 */
@RestController
@RequestMapping("/proteus")
public class ProteusPeerController {

    private final ProteusMonitorAgent monitorAgent;

    @Autowired
    public ProteusPeerController(ProteusMonitorAgent monitorAgent){
        this.monitorAgent = monitorAgent;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void peerConnected(PeerProfile profile){
        monitorAgent.peerConnected(profile);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void superPeerRequesting(PeerProfile profile){
        monitorAgent.superPeerIsRequesting(profile);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void peerDisconnected(PeerProfile profile){
        monitorAgent.peerDisoconnected(profile);
    }
}
