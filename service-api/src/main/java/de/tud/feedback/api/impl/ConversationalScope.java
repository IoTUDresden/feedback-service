package de.tud.feedback.api.impl;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.config.Scope;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.synchronizedMap;

public class ConversationalScope implements Scope {

    private final Map<String, Object> beans = synchronizedMap(newHashMap());

    private final String conversationId;

    private ConversationalScope(String conversationId) {
        this.conversationId = conversationId;
    }

    public static CustomScopeConfigurer createScopeConfigurerFor(String conversationId) {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(conversationId, new ConversationalScope(conversationId));
        return configurer;
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (!beans.containsKey(name))
            beans.put(name, objectFactory.getObject());

        return beans.get(name);
    }

    @Override
    public Object remove(String name) {
        return beans.remove(name);
    }

    @Override
    public String getConversationId() {
        return conversationId;
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // nothing to do
    }

}
