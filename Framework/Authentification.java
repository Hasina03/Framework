package etu1909.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Définition de l'annotation "Authentification"
@Retention(RetentionPolicy.RUNTIME) // L'annotation est conservée à l'exécution
@Target(ElementType.METHOD) // L'annotation peut être appliquée uniquement aux méthodes
public @interface Authentification {
    String profile();
}
