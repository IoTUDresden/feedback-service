package de.tud.feedback.service;

import de.tud.feedback.domain.context.Context;

public interface ContextService {

    void importFrom(Context context);

    void preProcess(Context context);

}
