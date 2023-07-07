package etu1909.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


//déclare l'annotation "RequestMapping"
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {  // L'annotation est conservée à l'exécution
    String path();  //sera utilisé pour spécifier le chemin associé à une requête dans le framework
}
