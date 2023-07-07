package etu1909.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)  //spécifie que l'annotation peut être appliquée uniquement aux paramètres de méthode. Cela signifie que l'annotation @SessionAttribute peut être utilisée pour annoter les paramètres de méthode.
@Retention(RetentionPolicy.RUNTIME) //définit la politique de rétention de l'annotation. Dans ce cas, l'annotation est conservée à l'exécution, ce qui signifie qu'elle sera disponible pour l'inspection à l'exécution du programme.
public @interface SessionAttribute {
    String value(); //Cet attribut sera utilisé pour spécifier le nom de l'attribut de session auquel le paramètre annoté est associé.
}
