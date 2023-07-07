package etu1909.framework;

import java.util.HashMap;
import java.util.Map;


public class ModelView {
    String vue;
    HashMap<String, Object> data; // Données associées au modèle
    HashMap<String, Object> session; // Données de session associées au modèle
    boolean JSON; // Indicateur pour spécifier si le modèle est au format JSON

    //--------------- Constructeur ---------------------
     public ModelView(String vue) {
        this.vue = vue;
        this.data = new HashMap<>(); // Initialisation du tableau des données avec une nouvelle instance de HashMap
        this.session = new HashMap<>(); // Initialisation du tableau des données de session avec une nouvelle instance de HashMap
    }

    public ModelView(String vue, HashMap<String, Object> data) {
        this.vue = vue;
        this.data = data;
    }

    // --------------- Methode ---------------
    public void addItemData(String key, Object value) { // Méthode pour ajouter un élément aux données du modèle
        this.data.put(key, value);
    }

    public void addItemSession(String key, Object value) { // Méthode pour ajouter un élément aux données de session du modèle
        this.session.put(key, value);
    }

    // ---------------- Getter - Setter ---------------

    public String getVue() {
        return vue;
    }

    public void setVue(String vue) {
        this.vue = vue;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }

    public boolean isJSON() {
        return JSON;
    }

    public void setJSON(boolean jSON) {
        JSON = jSON;
    }

}
