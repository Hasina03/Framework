package etu1909.framework;

public class FileUpload {
    String path;
    String name;
    byte[] donnees;

    // ---------------- Constructeur -----------------
    public FileUpload() {
    }

    public FileUpload(String name, byte[] donnees) {
        this.name = name;
        this.donnees = donnees;
    }

    public FileUpload(String path, String name, byte[] donnees) {
        this.path = path;
        this.name = name;
        this.donnees = donnees;
    }

    //--------------- Getter - Stter -----------------
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getDonnees() {
        return donnees;
    }

    public void setDonnees(byte[] donnees) {
        this.donnees = donnees;
    }


}
