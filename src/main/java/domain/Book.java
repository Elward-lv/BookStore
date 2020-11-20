package domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Book implements Serializable {

    private Integer id;
    private String bookCode;
    private String bookName;
    private String bookImg;//img url
    private Double bookPrice;
    private Integer bookNums;
    private String bookInfo;
    private String bookAuthor;
    private Date modifyDate;
    private Integer bookUser;
    private List<String> kinds;
    private Integer saledNum;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookCode='" + bookCode + '\'' +
                ", bookName='" + bookName + '\'' +
                ", bookImg='" + bookImg + '\'' +
                ", bookPrice=" + bookPrice +
                ", bookNums=" + bookNums +
                ", bookInfo='" + bookInfo + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", modifyDate=" + modifyDate +
                ", bookUser=" + bookUser +
                ", kinds=" + kinds +
                ", saledNum=" + saledNum +
                '}';
    }

    public Integer getSaledNum() {
        return saledNum;
    }

    public void setSaledNum(Integer saledNum) {
        this.saledNum = saledNum;
    }

    public String getBookImg() {
        return bookImg;
    }

    public void setBookImg(String bookImg) {
        this.bookImg = bookImg;
    }

    public Integer getBookUser() {
        return bookUser;
    }

    public void setBookUser(Integer bookUser) {
        this.bookUser = bookUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(Double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public Integer getBookNums() {
        return bookNums;
    }

    public void setBookNums(Integer bookNums) {
        this.bookNums = bookNums;
    }

    public String getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(String bookInfo) {
        this.bookInfo = bookInfo;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public List<String> getKinds() {
        return kinds;
    }

    public void setKinds(List<String> kinds) {
        this.kinds = kinds;
    }
}
