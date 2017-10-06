package de.tud.feedback.plugin.rest;

import de.tud.feedback.plugin.ProteusMonitorAgent;
import eu.vicci.process.distribution.core.PeerProfile;
import eu.vicci.process.distribution.core.SuperPeerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

/**
 * Peer controller for api which can be accessed by proteus
 */
@RestController
@RequestMapping("/proteus")
public class ProteusPeerController {

    @Autowired
    private Provider<ProteusMonitorAgent> proteusMonitorAgentProvider;

    @RequestMapping(value="/peerConnected", method = RequestMethod.POST)
    public void peerConnected(PeerProfile profile){
        proteusMonitorAgentProvider.get().peerConnected(profile);
    }

    @RequestMapping(value="/superPeerRequesting",
            method = RequestMethod.POST,
            consumes="application/json")
    public void superPeerRequesting(@RequestBody  SuperPeerRequest request){
        //TODO maybe we can get the correct ip by the servlet request
        proteusMonitorAgentProvider.get().superPeerIsRequesting(request);
    }

    @RequestMapping(value="/peerDisconnected", method = RequestMethod.POST)
    public void peerDisconnected(PeerProfile profile){
        proteusMonitorAgentProvider.get().peerDisconnected(profile);
    }

}
