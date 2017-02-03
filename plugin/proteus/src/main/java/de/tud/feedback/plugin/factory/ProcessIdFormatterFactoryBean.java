package de.tud.feedback.plugin.factory;

import de.tud.feedback.plugin.ProcessIdFormatter;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class ProcessIdFormatterFactoryBean extends AbstractFactoryBean<ProcessIdFormatter> {

    @Override
    public Class<?> getObjectType() {
        return ProcessIdFormatter.class;
    }

    @Override
    protected ProcessIdFormatter createInstance()  {
        return new ProcessIdFormatter();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
