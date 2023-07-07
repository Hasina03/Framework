package etu1909.framework;

public class Mapping {
    String className;
    String method;
    Class<?>[] paramsType;  //permet de stocker les types de paramètres d'une méthode associée à un mapping

    // ---------------- Constructeur ---------------
    public Mapping() {
    }

    public Mapping(String className, String method) {
        this.className = className;
        this.method = method;
    }

    //---------- Getter - Setter ------------------
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParamsType() {
        return paramsType;
    }

    public void setParamsType(Class<?>[] paramsType) {
        this.paramsType = paramsType;
    }

}
