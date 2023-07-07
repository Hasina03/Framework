package etu1909.framework.servlet;

import etu1909.framework.annotation.*;
import etu1909.framework.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;
import utilitaires.Util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@MultipartConfig
public class FrontServlet  extends HttpServlet {
    HashMap<String, Mapping> MappingUrls = new HashMap<>();
    HashMap<String, Object> singleton = new HashMap<>();

    // --------------- Getter - Setter -------------------
    public HashMap<String, Object> getSingleton() {
        return singleton;
    }

    public void setSingleton(HashMap<String, Object> singleton) {
        this.singleton = singleton;
    }

    public HashMap<String, Mapping> getMappingUrls() {
        return MappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> MappingUrls) {
        this.MappingUrls = MappingUrls;
    }


    // la  méthode init est utilisée pour initialiser les champs de la servlet frontale. Elle récupère le chemin du répertoire de la classe actuelle, effectue des opérations sur les champs à l'aide de la classe Util, et affiche les URL de mappage et les singletons à des fins de débogage.
    @Override
    public void init() {
        try {
            String path = this.getClass().getClassLoader().getResource("").getPath(); // Récupère le chemin du répertoire de la classe
            path = path.replaceAll("%20", " "); // Remplace les espaces encodés dans le chemin par des espaces normaux
            Util.setFieldsFrontServlet(this.getMappingUrls(), this.getSingleton(), path,
                    Util.getAllPackages(null, path.substring(1, path.length()), null)); // Initialise les champs de FrontServlet en utilisant les méthodes utilitaires
            System.out.println("HashMap URL");
            for (Map.Entry<String, Mapping> entry : this.getMappingUrls().entrySet()) {
                System.out.println(entry.getKey() + "|" + entry.getValue().getMethod()); // Affiche les informations des URL mappées (pour le débogage)
            }
            System.out.println("HashMap Singleton");
            for (Map.Entry<String, Object> entry : this.getSingleton().entrySet()) {
                System.out.println(entry.getKey()); // Affiche les noms des singletons (pour le débogage)
            }
        } catch (Exception e) {
            e.printStackTrace(); // Affiche la trace d'erreur en cas d'exception
        }
    }

    // la méthode sessionFromMVToHttp est utilisée pour transférer les données de la session d'un objet ModelView vers la session HTTP. Elle vérifie d'abord si l'objet est une instance de ModelView et si la session dans le ModelView n'est pas vide. Ensuite, elle parcourt les entrées de la session du ModelView et utilise setAttribute() de la session HTTP pour définir les attributs correspondants avec les valeurs appropriées.
    public void sessionFromMVToHttp(Object mv, HttpSession session) {
        if (mv instanceof ModelView) { // Vérifie si l'objet est une instance de ModelView
            if (((ModelView) mv).getSession() == null || ((ModelView) mv).getSession().isEmpty()) {
                return; // Si la session dans le ModelView est vide, retourne sans rien faire
            }
            for (Map.Entry<String, Object> entry : ((ModelView) mv).getSession().entrySet()) {
                if (entry.getKey() == "profile") { // Si la clé est "profile"
                    session.setAttribute(this.getInitParameter(entry.getKey()),
                            ((ModelView) mv).getSession().get(this.getInitParameter(entry.getKey())));
                } else {
                    session.setAttribute(entry.getKey(), ((ModelView) mv).getSession().get(entry.getKey()));
                }
            }
        }
    }


    // la méthode partToByte est utilisée pour convertir une partie (Part) de la requête en un tableau d'octets (byte[]). Elle lit les données de la partie à partir du flux d'entrée, les écrit dans un flux de sortie en mémoire, puis convertit le flux de sortie en un tableau d'octets. Le tableau d'octets résultant contient les données de la partie et est renvoyé par la méthode
    public byte[] partToByte(Part part) throws Exception {
        InputStream is = part.getInputStream(); // Obtient le flux d'entrée à partir de la partie (part) reçue
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Crée un nouveau flux de sortie en mémoire pour stocker les données
        byte[] buffer = new byte[1024]; // Crée un tampon (buffer) pour lire les données de manière efficace
        int byteReader; // Variable pour stocker le nombre d'octets lus

        while ((byteReader = is.read(buffer)) != -1) { // Lit les données du flux d'entrée dans le tampon
            baos.write(buffer, 0, byteReader); // Écrit les données du tampon dans le flux de sortie
        }

        byte[] byteArray = baos.toByteArray(); // Convertit le flux de sortie en tableau d'octets (byte array)
        baos.close(); // Ferme le flux de sortie
        is.close(); // Ferme le flux d'entrée

        return byteArray; // Retourne le tableau d'octets contenant les données de la partie
    }


    // la méthode manageFileUpload est utilisée pour gérer le téléchargement de fichiers. Elle rend l'attribut accessible, affiche le nom de fichier soumis, vérifie si la partie contient des données, convertit la partie en tableau d'octets, puis crée une nouvelle instance de FileUpload avec le nom de fichier et les données. Enfin, elle affecte cette instance à l'attribut de l'objet.
    public void manageFileUpload(Object o, Field attribut, Part part) throws Exception {
        attribut.setAccessible(true); // Rend l'attribut accessible pour la modification (il peut être privé)
        System.out.println(part.getSubmittedFileName()); // Affiche le nom de fichier soumis dans la console

        if (part.getSize() > 0) { // Vérifie si la taille de la partie est supérieure à zéro (contient des données)
            byte[] b = this.partToByte(part); // Convertit la partie en tableau d'octets en utilisant la méthode partToByte()
            attribut.set(o, new FileUpload(part.getSubmittedFileName(), b)); // Crée une nouvelle instance de FileUpload avec le nom de fichier et les données, et l'assigne à l'attribut de l'objet
        }
    }


    //la méthode setAttributeRequest est utilisée pour définir les attributs de la requête (HttpServletRequest) à partir des données contenues dans un objet ModelView. Elle vérifie d'abord si l'objet est une instance de ModelView et si les données dans le ModelView ne sont pas null ou vides. Ensuite, elle parcourt les entrées des données du ModelView et utilise setAttribute() de la requête pour définir les attributs correspondants avec les clés et les valeurs du ModelView.
    public void setAttributeRequest(HttpServletRequest request, Object mv) {
        if (mv instanceof ModelView) { // Vérifie si l'objet est une instance de ModelView
            if (((ModelView) mv).getData() == null || ((ModelView) mv).getData().size() == 0) {
                return; // Si les données dans le ModelView sont null ou vides, retourne sans rien faire
            }
            for (Map.Entry<String, Object> entry : ((ModelView) mv).getData().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue()); // Définit les attributs de la requête avec les clés et les valeurs du ModelView
            }
        }
    }


    //  méthode getMapping est utilisée pour récupérer l'objet Mapping associé à une URL spécifique à partir de la HashMap MappingUrls. Elle vérifie d'abord si l'URL est présente dans la HashMap. Si oui, elle renvoie l'objet Mapping correspondant. Sinon, elle lance une exception pour indiquer qu'aucune classe n'est associée à l'URL spécifiée.
    public Mapping getMapping(String url) throws Exception {
        if (this.MappingUrls.containsKey("/" + url)) { // Vérifie si l'URL est présente dans la HashMap MappingUrls
            return this.MappingUrls.get("/" + url); // Renvoie l'objet Mapping correspondant à l'URL
        }
        throw new Exception("Cette URL n'est associée à aucune classe"); // Lance une exception si l'URL n'est associée à aucune classe
    }

    // la méthode fillAttributeOfObject est utilisée pour remplir les attributs d'un objet à partir des paramètres d'une requête. Elle parcourt les attributs de l'objet, rend les attributs accessibles, vérifie si l'attribut est destiné à gérer les téléchargements de fichiers, récupère les valeurs des attributs à partir des paramètres de la requête, puis affecte les valeurs correspondantes aux attributs de l'objet.
    public void fillAttributeOfObject(Object o, HttpServletRequest request) throws Exception {
        Field[] attributs = o.getClass().getDeclaredFields(); // Récupère tous les attributs déclarés de l'objet
        for (Field field : attributs) { // Parcourt chaque attribut
            field.setAccessible(true); // Rend l'attribut accessible pour la modification
            if (field.getType() == FileUpload.class) { // Vérifie si le type de l'attribut est FileUpload
                try {
                    this.manageFileUpload(o, field, request.getPart(field.getName())); // Gère le téléchargement de fichier pour cet attribut en utilisant la méthode manageFileUpload()
                } catch (Exception e) {
                    e.printStackTrace(); // Affiche les détails de l'exception en cas d'erreur
                }
            } else { // Si le type de l'attribut n'est pas FileUpload
                String value = request.getParameter(field.getName()); // Récupère la valeur de l'attribut à partir des paramètres de la requête
                if (value != null) { // Vérifie si la valeur est non nulle
                    field.set(o, Util.castString(value, field.getType())); // Affecte la valeur à l'attribut en utilisant la méthode castString() pour effectuer la conversion si nécessaire
                }
            }
        }
    }

    // la  méthode instanceObject est utilisée pour instancier un objet à partir d'un Mapping qui contient des informations sur la classe à instancier. Elle vérifie d'abord si une instance de la classe existe déjà dans la HashMap singleton. Si oui, elle réinitialise l'objet existant et le renvoie. Sinon, elle crée une nouvelle instance de la classe en utilisant Class.forName().newInstance() et la stocke dans la HashMap singleton avant de la renvoyer.
    public Object instanceObject(Mapping map) throws Exception {
        if (this.singleton.containsKey(map.getClassName())) { // Vérifie si la classe est présente dans la HashMap singleton
            if (this.singleton.get(map.getClassName()) != null) { // Vérifie si l'instance de la classe existe déjà
                Util.resetObject(this.singleton.get(map.getClassName())); // Réinitialise l'objet existant en utilisant la méthode resetObject() de la classe Util
                return this.singleton.get(map.getClassName()); // Renvoie l'instance existante de la classe
            } else {
                this.singleton.replace(map.getClassName(), Class.forName(map.getClassName()).newInstance()); // Crée une nouvelle instance de la classe en utilisant Class.forName().newInstance() et la remplace dans la HashMap singleton
                return this.singleton.get(map.getClassName()); // Renvoie la nouvelle instance de la classe
            }
        }
        Class<?> c = Class.forName(map.getClassName()); // Obtient la classe à partir du nom de classe spécifié dans le Mapping
        Object o = c.newInstance(); // Crée une nouvelle instance de la classe en utilisant newInstance()
        return o; // Renvoie la nouvelle instance de la classe
    }

    // la méthode callModelAndFunction est utilisée pour appeler une fonction sur un objet donné, en utilisant la réflexion. Elle affiche d'abord le nom de la fonction à appeler, puis appelle la fonction sur l'objet en utilisant invoke(). Elle récupère le résultat de l'appel de la fonction et le renvoie. Si le résultat est une instance de ModelView, elle affiche également le nom de la vue associée.
    public Object callModelAndFunction(Object o, Method fonction, Object[] params, Mapping map) throws Exception {
        System.out.println("Fonction " + fonction.getName()); // Affiche le nom de la fonction à appeler
        Object resultat = fonction.invoke(o, params); // Appelle la fonction sur l'objet `o` en utilisant la méthode `invoke()` de la classe `Method`. Les paramètres sont passés via le tableau `params`.
        if (resultat instanceof ModelView) { // Vérifie si le résultat est une instance de `ModelView`
            System.out.println("Vue " + ((ModelView) resultat).getVue()); // Affiche le nom de la vue du `ModelView`
        }
        return resultat; // Renvoie le résultat de l'appel de la fonction
    }

    // la  méthode sendResponse est utilisée pour envoyer une réponse à une requête. Elle vérifie d'abord si l'objet mv est une instance de ModelView. Si c'est le cas et que le ModelView indique que la réponse doit être au format JSON, elle convertit les données du ModelView en JSON et envoie la réponse au format JSON. Sinon, si isResponseBody est true, elle convertit l'objet mv en JSON et envoie la réponse au format JSON. Si aucune de ces conditions n'est remplie, elle effectue une redirection interne vers une vue spécifiée dans le ModelView.
    public void sendResponse(HttpServletRequest request, HttpServletResponse response, Object mv, boolean isResponseBody)
        throws ServletException, IOException, Exception {
        if (mv instanceof ModelView) { // Vérifie si l'objet est une instance de `ModelView`
            if (((ModelView) mv).isJSON() == true) { // Vérifie si le `ModelView` indique que la réponse doit être au format JSON
                response.setContentType("application/json"); // Définit le type de contenu de la réponse comme JSON
                String json = Util.convertObjectToJSON(((ModelView) mv).getData()); // Convertit les données du `ModelView` en format JSON en utilisant la méthode `convertObjectToJSON()` de la classe `Util`
                PrintWriter out = response.getWriter(); // Récupère le flux d'écriture pour la réponse
                out.println(json); // Écrit la chaîne JSON dans le flux de sortie
                out.flush(); // Vide le flux de sortie
            } else { // Si le `ModelView` n'indique pas que la réponse est au format JSON
                this.dispatch(request, response, ((ModelView) mv).getVue()); // Effectue une redirection interne vers la vue spécifiée dans le `ModelView` en utilisant la méthode `dispatch()`
            }
        } else if (isResponseBody == true) { // Si l'objet n'est pas un `ModelView` mais la réponse doit être un corps de réponse
            System.out.println("Response Body"); // Affiche un message indiquant que la réponse est un corps de réponse
            response.setContentType("application/json"); // Définit le type de contenu de la réponse comme JSON
            String json = Util.convertObjectToJSON(mv); // Convertit l'objet en format JSON en utilisant la méthode `convertObjectToJSON()` de la classe `Util`
            PrintWriter out = response.getWriter(); // Récupère le flux d'écriture pour la réponse
            out.println(json); // Écrit la chaîne JSON dans le flux de sortie
            out.flush(); // Vide le flux de sortie
        }
    }

    // la méthode fillArgumentsOfFonction est utilisée pour remplir les arguments d'une fonction à partir des paramètres de la requête. Elle récupère les paramètres de la fonction en utilisant la réflexion, puis parcourt ces paramètres pour récupérer les valeurs correspondantes à partir des paramètres de la requête ou de la session, en fonction des annotations. Les valeurs converties sont stockées dans un tableau d'objets qui est renvoyé en tant que résultat.
    public Object[] fillArgumentsOfFonction(Method fonction, HttpServletRequest request) throws Exception {
        Parameter[] arguments = fonction.getParameters(); // Récupère les paramètres de la fonction en utilisant la méthode `getParameters()` de la classe `Method`
        Object[] params = new Object[arguments.length]; // Crée un tableau d'objets pour stocker les arguments de la fonction
        for (int i = 0; i < arguments.length; i++) { // Parcourt les paramètres de la fonction
            String value = null;
            if (arguments[i].getAnnotation(SessionAttribute.class) != null) { // Vérifie si le paramètre est annoté avec `SessionAttribute`
                value = request.getSession().getAttribute(arguments[i].getAnnotation(SessionAttribute.class).value()).toString(); // Récupère la valeur du paramètre à partir de la session en utilisant la valeur spécifiée dans l'annotation `SessionAttribute`
            } else { // Si le paramètre n'est pas annoté avec `SessionAttribute`
                value = request.getParameter(arguments[i].getName()); // Récupère la valeur du paramètre à partir des paramètres de la requête en utilisant le nom du paramètre
            }
            System.out.println(arguments[i].getName()); // Affiche le nom du paramètre
            if (value != null) { // Vérifie si une valeur a été trouvée pour le paramètre
                params[i] = Util.castString(value, arguments[i].getType()); // Convertit la valeur en utilisant la méthode `castString()` de la classe `Util` et l'assigne à l'index correspondant dans le tableau `params`
            }
        }
        return params; // Renvoie le tableau d'arguments rempli
    }


    // la méthode authentificate est utilisée pour vérifier l'authentification avant d'exécuter une méthode. Si la méthode est annotée avec Authentification, elle compare la valeur de l'attribut profile de l'annotation avec la valeur de l'attribut profile de la session. Si les valeurs sont différentes ou si l'attribut de session est null, une exception est lancée pour indiquer un accès interdit.
    public void authentificate(HttpServletRequest req, Method fonction) throws Exception {
        if (fonction.getAnnotation(Authentification.class) != null) {
            System.out.println(fonction.getAnnotation(Authentification.class).profile()); // Affiche la valeur de l'attribut `profile` de l'annotation `Authentification` sur la console
            System.out.println(req.getSession().getAttribute(this.getInitParameter("profile"))); // Affiche la valeur de l'attribut `profile` de la session sur la console
            if (req.getSession().getAttribute(this.getInitParameter("profile")) == null || // Vérifie si l'attribut `profile` de la session est null ou
                    fonction.getAnnotation(Authentification.class).profile().equalsIgnoreCase( // si la valeur de l'attribut `profile` de l'annotation `Authentification` n'est pas égale (ignorant la casse) à
                            req.getSession().getAttribute(this.getInitParameter("profile")).toString()) == false) { // la valeur de l'attribut `profile` de la session convertie en chaîne de caractères
                throw new Exception("Acces interdit"); // Lance une exception avec le message "Acces interdit" si les conditions ne sont pas remplies
            }
        }
    }


    public boolean isResponseBody(Method fonction) {
        return fonction.getAnnotation(ResponseBody.class) != null; // Vérifie si la méthode est annotée avec `ResponseBody` en utilisant la méthode `getAnnotation()` de la classe `Method`. Si l'annotation est présente, la méthode renvoie `true`, sinon elle renvoie `false`.
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] data = Util.retrieveDataFromURL(request.getRequestURI());
        System.out.println("URL du Client");
        for (int i = 0; i < data.length; i++) {
            String string = data[i];
            System.out.println(string);
        }
        try {
            if (data[data.length - 1].equalsIgnoreCase("")) {
                this.dispatch(request, response, "index.html");
                return;
            }
            Mapping map = getMapping(data[data.length - 1]);
            Object o = this.instanceObject(map);
            Method fonction = o.getClass().getDeclaredMethod(map.getMethod(), map.getParamsType());
            this.authentificate(request, fonction);
            this.fillAttributeOfObject(o, request);
            Object[] params = this.fillArgumentsOfFonction(fonction, request);
            Object reponse = callModelAndFunction(o, fonction, params, map);
            this.sessionFromMVToHttp(reponse, request.getSession());
            this.setAttributeRequest(request, reponse);
            if (reponse instanceof ModelView) {
                System.out.println(((ModelView) reponse).getVue());
            }
            this.sendResponse(request, response, reponse, this.isResponseBody(fonction));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            ModelView error = new ModelView("error.jsp");
            error.addItemData("message", e.getMessage());
            this.setAttributeRequest(request, error);
            this.dispatch(request, response, error.getVue());
            return;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    public void dispatch(HttpServletRequest request, HttpServletResponse response, String vue)
            throws ServletException, IOException {
        RequestDispatcher dispa = request.getRequestDispatcher("/WEB-INF/vues/" + vue);
        dispa.forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }



}
