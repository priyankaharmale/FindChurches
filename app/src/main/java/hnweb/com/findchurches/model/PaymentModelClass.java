package hnweb.com.findchurches.model;

public class PaymentModelClass {

    String  id,cid,reg_id,donation_amount,stripeToken,stripChargeID,stripTractionID,status,created_on;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getDonation_amount() {
        return donation_amount;
    }

    public void setDonation_amount(String donation_amount) {
        this.donation_amount = donation_amount;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public String getStripChargeID() {
        return stripChargeID;
    }

    public void setStripChargeID(String stripChargeID) {
        this.stripChargeID = stripChargeID;
    }

    public String getStripTractionID() {
        return stripTractionID;
    }

    public void setStripTractionID(String stripTractionID) {
        this.stripTractionID = stripTractionID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
