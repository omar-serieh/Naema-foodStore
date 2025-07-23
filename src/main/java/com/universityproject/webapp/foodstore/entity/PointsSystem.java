package com.universityproject.webapp.foodstore.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "points_system")
public class PointsSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "points_id")
    private int pointsId;
    @ManyToOne(fetch=FetchType.LAZY,
            cascade = {CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH,})
    @JoinColumn(name = "user_id")
    private Users userId;
    @Column(name = "points_earned")
    private double pointsEarned;
    @Column(name = "date")
    private Date date;
    public PointsSystem() {}

    public PointsSystem(double pointsEarned, Date date) {
        this.pointsEarned = pointsEarned;
        this.date = date;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(double pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public int getPointsId() {
        return pointsId;
    }

    public void setPointsId(int pointsId) {
        this.pointsId = pointsId;
    }
}
