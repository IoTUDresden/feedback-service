package de.tud.feedback.service;

import de.tud.feedback.domain.Context;

public interface ContextService {

    void importAllOf(Context context);

    void deleteImportsFor(Context context);

}
