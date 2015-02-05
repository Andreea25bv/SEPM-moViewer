package dto;

/**
 * Created by Toan on 26.11.2014.
 */
public class Person {

    private Integer pid;
    private String name;
    private Integer tmdbid;
    private String biography;
    private String photoPath;

    public Person() {
    }


    public Person(Integer pid, String name, String biography, String photoPath) {
        this.pid = pid;
        this.tmdbid = 0;
        this.name = name;
        this.biography = biography;
        this.photoPath = photoPath;
    }

    public Person(Integer pid, String name, Integer tmdbid, String biography, String photoPath) {
        this.pid = pid;
        this.tmdbid = tmdbid;
        this.name = name;
        this.biography = biography;
        this.photoPath = photoPath;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public Integer getTmdbid() {
        return tmdbid;
    }

    public void setTmdbid(Integer tmdbid) {
        this.tmdbid = tmdbid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return "Person{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", tmdbid='" + tmdbid + '\'' +
                ", biography='" + biography + '\'' +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }
}
