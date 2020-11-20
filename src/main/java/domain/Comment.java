package domain;

import java.util.Date;

public class Comment {
    private Integer id;
    private String commentInfo;
    private Integer replyUser;
    private Integer commentUser;
    private Integer commentBook;
    private Date modifyDate;
    private Integer stars;
    private Integer praises;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", commentInfo='" + commentInfo + '\'' +
                ", replyUser=" + replyUser +
                ", commentUser=" + commentUser +
                ", commentBook=" + commentBook +
                ", modifyDate=" + modifyDate +
                ", stars=" + stars +
                ", praises=" + praises +
                '}';
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Integer getPraises() {
        return praises;
    }

    public void setPraises(Integer praises) {
        this.praises = praises;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(String commentInfo) {
        this.commentInfo = commentInfo;
    }

    public Integer getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(Integer replyUser) {
        this.replyUser = replyUser;
    }

    public Integer getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(Integer commentUser) {
        this.commentUser = commentUser;
    }

    public Integer getCommentBook() {
        return commentBook;
    }

    public void setCommentBook(Integer commentBook) {
        this.commentBook = commentBook;
    }
}
