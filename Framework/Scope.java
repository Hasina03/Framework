package etu1909.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scope {   //L'annotation est définie en utilisant l'annotation @interface, qui est utilisée pour définir de nouvelles annotations personnalisées.
    static int Singleton = 1;   //déclare une constante "Singleton" de type int dans l'annotation. La constante est statique, ce qui signifie qu'elle peut être utilisée sans créer d'instance de l'annotation.

    int scope(); // déclare un attribut "scope" de type int pour l'annotation. Cet attribut sera utilisé pour spécifier la portée d'un élément dans le framework. La valeur de l'attribut sera fournie lors de l'utilisation de l'annotation.
}
