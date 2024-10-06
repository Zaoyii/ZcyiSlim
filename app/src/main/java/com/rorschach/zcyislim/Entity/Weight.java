package com.rorschach.zcyislim.Entity;

public class Weight {
    long weightId;
    double weightData;
    long weightUserId;
    String weightCreateTime;

    public Weight(double weightData, long weightUserId) {
        this.weightData = weightData;
        this.weightUserId = weightUserId;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "weightId=" + weightId +
                ", weightData=" + weightData +
                ", weightUserId=" + weightUserId +
                ", weightCreateTime='" + weightCreateTime + '\'' +
                '}';
    }

    public long getWeightId() {
        return weightId;
    }

    public void setWeightId(long weightId) {
        this.weightId = weightId;
    }

    public double getWeightData() {
        return weightData;
    }

    public void setWeightData(double weightData) {
        this.weightData = weightData;
    }

    public long getWeightUserId() {
        return weightUserId;
    }

    public void setWeightUserId(long weightUserId) {
        this.weightUserId = weightUserId;
    }

    public String getWeightCreateTime() {
        return weightCreateTime;
    }

    public void setWeightCreateTime(String weightCreateTime) {
        this.weightCreateTime = weightCreateTime;
    }
}
