package de.tud.feedback.plugin.events;

import eu.vicci.process.distribution.core.PeerProfile;
import eu.vicci.process.distribution.core.SuperPeerRequest;
import eu.vicci.process.model.util.messages.core.IStateChangeMessage;
import eu.vicci.process.model.util.messages.core.PeerMetrics;

public abstract class ProteusEvent {

    public static abstract class ProfileEvent extends ProteusEvent{
        private final PeerProfile profile;

        ProfileEvent(PeerProfile profile) {
            this.profile = profile;
        }

        public PeerProfile getProfile() {
            return profile;
        }
    }

    public static class NewSuperPeerEvent extends  ProteusEvent {
        private final SuperPeerRequest request;

        public NewSuperPeerEvent(SuperPeerRequest request){
            this.request = request;
        }

        public SuperPeerRequest getRequest() {
            return request;
        }
    }

    public static class PeerConnectedEvent extends ProfileEvent{
        public PeerConnectedEvent(PeerProfile profile) {
            super(profile);
        }
    }

    public static class PeerDisconnectedEvent extends ProfileEvent{
        public PeerDisconnectedEvent(PeerProfile profile) {
            super(profile);
        }
    }

    public static class StateChangeEvent extends ProteusEvent{
        private final IStateChangeMessage message;
        public StateChangeEvent(IStateChangeMessage message) {
            this.message = message;
        }

        public IStateChangeMessage getMessage() {
            return message;
        }
    }

    public static class PeerMetricsEvent extends ProteusEvent{
        private final PeerMetrics metrics;

        public PeerMetricsEvent(PeerMetrics metrics) {
            this.metrics = metrics;
        }

        public PeerMetrics getMetrics() {
            return metrics;
        }
    }


}
