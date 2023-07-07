package utilitaires;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;    //permet d'utiliser la classe Gson pour effectuer des opérations de sérialisation et de désérialisation JSON en utilisant la bibliothèque Gson.

import etu1909.framework.annotation.*;
import etu1909.framework.Mapping;

public class Util {

    // la  méthode convertJSONToObject utilise la bibliothèque Gson pour convertir une chaîne de caractères JSON en un objet Java du type spécifié. Elle facilite la désérialisation d'un objet Java à partir d'une représentation JSON.
    public static Object convertJSONToObject(String json, Class<?> classe) {
        Gson gson = new Gson();
        return gson.fromJson(json, classe);
    }

    // la méthode convertObjectToJSON utilise la bibliothèque Gson pour convertir un objet Java en une chaîne de caractères JSON. Elle facilite la sérialisation d'un objet Java dans un format JSON qui peut être utilisé pour le stockage, la transmission ou l'échange de données.
    public static String convertObjectToJSON(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    // la cette méthode resetObject réinitialise tous les attributs d'un objet en fonction de leur type. Elle utilise la réflexion pour accéder aux attributs et les réinitialise en fonction de leur type primitif ou de référence. Cela permet de restaurer les valeurs par défaut des attributs de l'objet.
    public static void resetObject(Object o) throws Exception {
        Field[] attributs = o.getClass().getDeclaredFields();
        for (Field field : attributs) {
            field.setAccessible(true);
            if (field.getType() == int.class || field.getType() == double.class || field.getType() == float.class
                    || field.getType() == long.class || field.getType() == byte.class
                    || field.getType() == short.class || field.getType() == char.class) {
                field.set(o, 0);
            } else if (field.getType() == boolean.class) {
                field.set(o, false);
            } else {
                field.set(o, null);
            }
        }
    }

    // la  méthode castString permet de convertir une chaîne de caractères (texte) en un objet Java du type spécifié (classe). La méthode utilise des conversions spécifiques en fonction du type de classe fourni, telles que la conversion en Date, int, double, boolean, float, ou simplement en renvoyant la chaîne d'origine pour les autres types de classe.
    public static Object castString(Object o, Class<?> classe) throws Exception {
        String texte = o.toString();
        switch (classe.getSimpleName()) {
            case "Date":
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                if (texte.indexOf('T') != -1) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
                }
                Date d = sdf.parse(texte);
                return d;

            case "int":
                return Integer.parseInt(texte);

            case "double":
                return Double.parseDouble(texte);

            case "boolean":
                return Boolean.parseBoolean(texte);

            case "float":
                return Float.parseFloat(texte);

            default:
                return texte;
        }
    }

    // la méthode getAllPackages explore récursivement un dossier spécifié (path) et récupère tous les packages contenus dans ce dossier et ses sous-dossiers. Elle renvoie une liste de chaînes de caractères représentant les packages trouvés. Les packages sont construits en concaténant le chemin des dossiers avec les noms des dossiers successifs
    public static List<String> getAllPackages(List<String> packages, String path, String debut) {
        String concat = ".";
        if (packages == null)
            packages = new ArrayList<>();
        if (debut == null) {
            debut = "";
            concat = "";
        }
        File dossier = new File(path);
        File[] files = dossier.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                packages.add(debut + concat + file.getName());
                // System.out.println(debut + concat + file.getName());
                packages = getAllPackages(packages, file.getPath(), debut + concat + file.getName());
            }
        }
        return packages;
    }

    // la méthode manageSingleton gère l'instanciation des classes Singleton dans un conteneur représenté par un HashMap (singleton). La méthode vérifie si la classe spécifiée est annotée avec @Scope et si la portée de cette classe est de type Singleton. Si c'est le cas, la classe est ajoutée au HashMap singleton avec une valeur null. Cela permet de suivre les classes Singleton et de les instancier au besoin lorsqu'elles sont demandées.
    public static void manageSingleton(HashMap<String, Object> singleton, Class<?> classe) {
        if (classe.getAnnotation(Scope.class) != null) {
            if (classe.getAnnotation(Scope.class).scope() == Scope.Singleton) {
                singleton.put(classe.getName(), null);
            }
        }
    }

    // la méthode setFieldsFrontServlet gère la configuration des URL et des objets Mapping associés en explorant les packages, les classes et les méthodes annotées avec @RequestMapping. Elle crée des objets Mapping avec les informations nécessaires (nom de classe, nom de méthode, types de paramètres) et les associe aux URL correspondantes dans le HashMap urls. La méthode utilise également le HashMap singleton pour gérer les instances Singleton des classes lors de leur utilisation.
    public static void setFieldsFrontServlet(HashMap<String, Mapping> urls, HashMap<String, Object> singleton,
            String path, List<String> packages)
            throws Exception {
        for (int i = 0; i < packages.size(); i++) {
            List<Class<?>> modeles = getAllClasses(packages.get(i), path);
            List<Method> methodes = new ArrayList<>();
            String key = "";
            for (int n = 0; n < modeles.size(); n++) {
                manageSingleton(singleton, modeles.get(n));
                methodes = listAllMethods(modeles.get(n), RequestMapping.class);
                for (Method methode : methodes) {
                    Mapping map = new Mapping();
                    key = methode.getAnnotation(RequestMapping.class).path();
                    map.setClassName(modeles.get(n).getName());
                    map.setMethod(methode.getName());
                    map.setParamsType(methode.getParameterTypes());
                    urls.put(key, map);
                }
            }
        }
    }

    // la méthode retrieveDataFromURL divise une URL en parties en utilisant le délimiteur / et extrait les parties pertinentes de l'URL dans un tableau de chaînes de caractères. Les parties extraites de l'URL sont stockées dans un tableau data et renvoyées pour un traitement ultérieur. Cette méthode peut être utilisée pour récupérer des données spécifiques d'une URL, telles que des paramètres ou des segments d'URL.
    public static String[] retrieveDataFromURL(String url) {
        String[] urlData = url.split("/", 3);
        String[] data = new String[urlData.length - 1];
        for (int i = 0; i < data.length; i++) {
            System.out.println("URL " + urlData[i + 1]);
            data[i] = urlData[i + 1];
        }
        return data;
    }

    // la méthode getTypesAnnotedWith est utilisée pour récupérer une liste de classes qui sont annotées avec une annotation spécifique (annotation). Elle exploite d'autres méthodes pour explorer les classes dans le package spécifié (nomPackage) en utilisant le chemin spécifié (path), puis elle effectue une recherche pour trouver les classes qui sont annotées avec l'annotation spécifiée. Le résultat est renvoyé sous la forme d'une liste de classes. Si une exception se produit lors de l'exécution du code, une liste vide est renvoyée.
    public static List<Class<?>> getTypesAnnotedWith(String nomPackage, Class<? extends Annotation> annotation,
            String path) {
        try {
            return listAllClasses(getAllClasses(nomPackage, path), annotation);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // la  méthode getAllClasses est utilisée pour récupérer toutes les classes dans un package spécifié. Elle explore le répertoire correspondant au package, récupère tous les fichiers, et sélectionne uniquement ceux qui ont l'extension .class. Pour chaque fichier de classe, elle utilise la réflexion pour charger la classe correspondante et l'ajoute à une liste. Enfin, elle renvoie la liste des classes trouvées dans le package.
    public static List<Class<?>> getAllClasses(String nomPackage, String path) throws Exception {
        File directory = new File(packageToPath(nomPackage, path));
        // System.out.println(directory.getAbsolutePath());
        File[] classFile = directory.listFiles();
        List<Class<?>> classes = new ArrayList<>();
        for (int i = 0; i < classFile.length; i++) {
            if (classFile[i].getName().endsWith(".class"))
                classes.add(Class.forName(
                        nomPackage + "." + classFile[i].getName().substring(0, classFile[i].getName().length() - 6)));
        }
        return classes;
    }

    // la méthode packageToPath est utilisée pour convertir le nom d'un package en un chemin de fichier valide. Elle prend en compte les sous-packages dans le nom du package et les concatène avec le chemin initial pour construire le chemin complet. Si le chemin initial est null, elle utilise un chemin par défaut. La méthode renvoie le chemin final résultant.
    public static String packageToPath(String nom, String path) {
        String[] dossierPackage = new String[] { nom };
        if (nom.contains(".")) {
            dossierPackage = nom.split(".");
        }
        if (path == null) {
            path = ".";
        }
        for (int index = 0; index < dossierPackage.length; index++) {
            path = path.concat("/" + dossierPackage[index]);
        }
        return path;
    }

    // la  méthode listAllClasses est utilisée pour filtrer une liste de classes et retourner uniquement les classes qui sont annotées avec une annotation spécifique. Elle parcourt toutes les classes dans la liste, vérifie si chaque classe a l'annotation spécifiée, et ajoute les classes annotées à une nouvelle liste. Cette nouvelle liste est ensuite renvoyée, contenant toutes les classes annotées avec l'annotation spécifiée.
    public static List<Class<?>> listAllClasses(List<Class<?>> classes, Class annotation) {
        List<Class<?>> liste = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).getAnnotation(annotation) != null) {
                System.out.println("Class : " + classes.get(i).getName());
                liste.add(classes.get(i));
            }
        }
        return liste;
    }

    // la méthode listAllMethods est utilisée pour filtrer les méthodes d'une classe et retourner uniquement les méthodes qui sont annotées avec une annotation spécifique. Elle récupère toutes les méthodes déclarées de la classe, vérifie si chaque méthode a l'annotation spécifiée, et ajoute les méthodes annotées à une nouvelle liste. Cette nouvelle liste est ensuite renvoyée, contenant toutes les méthodes annotées avec l'annotation spécifiée.
    public static List<Method> listAllMethods(Class classe, Class annotation) {
        Method[] fonctions = classe.getDeclaredMethods();
        List<Method> liste = new ArrayList<>();
        for (Method fonction : fonctions) {
            if (fonction.getAnnotation(annotation) != null) {
                System.out.println("Fonction : " + fonction.getName());
                liste.add(fonction);
            }
        }
        return liste;
    }

    // la méthode listAllAttributes est utilisée pour filtrer les attributs d'une classe et retourner uniquement les attributs qui sont annotés avec une annotation spécifique. Elle récupère tous les attributs déclarés de la classe, vérifie si chaque attribut a l'annotation spécifiée, et ajoute les attributs annotés à une nouvelle liste. Cette nouvelle liste est ensuite renvoyée, contenant tous les attributs annotés avec l'annotation spécifiée.
    public static List<Field> listAllAttributes(Class classe, Class annotation) {
        Field[] fields = classe.getDeclaredFields();
        List<Field> liste = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getAnnotation(annotation) != null) {
                System.out.println("Attribut : " + fields[i].getName());
                liste.add(fields[i]);
            }
        }
        return liste;
    }



}
