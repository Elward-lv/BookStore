package domain;

public class Bill {
    private Integer id;
    private Double billPrices;
    private Integer isPayed;
    private String billInfo;
    private String billBook;
    private Integer billUser;

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", billPrices=" + billPrices +
                ", isPayed=" + isPayed +
                ", billInfo='" + billInfo + '\'' +
                ", billBook='" + billBook + '\'' +
                ", billUser=" + billUser +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getBillPrices() {
        return billPrices;
    }

    public void setBillPrices(Double billPrices) {
        this.billPrices = billPrices;
    }

    public Integer getIsPayed() {
        return isPayed;
    }

    public void setIsPayed(Integer isPayed) {
        this.isPayed = isPayed;
    }

    public String getBillInfo() {
        return billInfo;
    }

    public void setBillInfo(String billInfo) {
        this.billInfo = billInfo;
    }

    public String getBillBook() {
        return billBook;
    }

    public void setBillBook(String billBook) {
        this.billBook = billBook;
    }

    public Integer getBillUser() {
        return billUser;
    }

    public void setBillUser(Integer billUser) {
        this.billUser = billUser;
    }
}
