package com.universityproject.webapp.foodstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "donations")
public class Donations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private int donationId;
    @ManyToOne(fetch=FetchType.LAZY,
            cascade = {CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.DETACH})
    @JoinColumn(name = "charity_id")
    private Charities charitytId;
    @Column(name = "date")
    private Date donationDate;
    @OneToMany(mappedBy = "donation")
    @JsonIgnore
    private List<CartItems> cartItems;

    public List<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItems> cartItems) {
        this.cartItems = cartItems;
    }

    public Donations() {
    }

    public Donations(Date donationDate) {
        this.donationDate = donationDate;

    }

    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }


    public Charities getCharitytId() {
        return charitytId;
    }

    public void setCharitytId(Charities charitytId) {
        this.charitytId = charitytId;
    }




    public Date getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(Date donationDate) {
        this.donationDate = donationDate;
    }
}
