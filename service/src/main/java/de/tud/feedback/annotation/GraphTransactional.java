package de.tud.feedback.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Transactional("neo4jTransactionManager")
public @interface GraphTransactional {

}
