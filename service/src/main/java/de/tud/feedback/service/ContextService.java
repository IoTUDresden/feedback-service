package de.tud.feedback.service;

import de.tud.feedback.domain.Context;

public interface ContextService {

    void preProcess(Context context);

    void importFrom(Context context);

}
