package domain;

public class Permission {
    private int id;
    private String url;
    private String permi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPermi() {
        return permi;
    }

    public void setPermi(String permi) {
        this.permi = permi;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", permi='" + permi + '\'' +
                '}';
    }
}
