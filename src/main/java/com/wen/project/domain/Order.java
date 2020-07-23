package com.wen.project.domain;

import java.io.Serializable;
import java.util.Date;

import com.wen.framework.domain.BaseEntity;
import com.wen.framework.orm.annotation.Column;
import com.wen.framework.orm.annotation.Entity;

@Entity(tablename="t_order")
public class Order extends BaseEntity implements Serializable {
    @Column(name="customer_id", jdbcType="INTEGER", remark="客户ID")
    private Integer customerId;

    @Column(name="vehicle_id", jdbcType="INTEGER", remark="车辆ID")
    private Integer vehicleId;

    @Column(name="driver_id", jdbcType="INTEGER", remark="接单司机ID")
    private Integer driverId;

    @Column(name="vehicle_model_id", jdbcType="INTEGER", remark="车型")
    private Integer vehicleModelId;

    @Column(name="mileage", jdbcType="DOUBLE", precision=12, scale=4, remark="里程数")
    private Double mileage;

    @Column(name="route_type", jdbcType="INTEGER", remark="1单程，2往返")
    private Integer routeType;

    @Column(name="trip_num", jdbcType="INTEGER", remark="趟数")
    private Integer tripNum;

    @Column(name="demand", jdbcType="VARCHAR", length=128, remark="附加服务")
    private String demand;

    @Column(name="waybill_code", jdbcType="VARCHAR", length=128, remark="运单编码")
    private String waybillCode;

    @Column(name="waybill_img", jdbcType="VARCHAR", length=256, remark="运单图像路径")
    private String waybillImg;

    @Column(name="book_time", jdbcType="TIMESTAMP", remark="预约时间")
    private Date bookTime;

    @Column(name="pay_type", jdbcType="INTEGER", remark="支付类型，1寄付，2到付，3月结")
    private Integer payType;

    @Column(name="pay_status", jdbcType="INTEGER", remark="支付状态，1未支付，2已支付")
    private Integer payStatus;

    @Column(name="money", jdbcType="DOUBLE", precision=12, scale=2, remark="代收货款")
    private Double money;

    @Column(name="status", jdbcType="INTEGER", remark="状态,1新订单，2已分配司机，3司机已接单，4到达接货地，5运输中，6已完成，7已取消")
    private Integer status;

    @Column(name="mark", jdbcType="INTEGER", remark="订单标识，1普通订单，2预约订单，3集团客户订单,4超时订单")
    private Integer mark;

    @Column(name="remark", jdbcType="VARCHAR", length=255, remark="备注")
    private String remark;

    @Column(name="create_org", jdbcType="INTEGER", remark="创建机构")
    private Integer createOrg;

    @Column(name="update_org", jdbcType="INTEGER", remark="更新机构")
    private Integer updateOrg;

    @Column(name="total_price", jdbcType="DOUBLE", precision=12, scale=2, remark="总费用")
    private Double totalPrice;

    @Column(name="code", jdbcType="VARCHAR", length=100)
    private String code;

    @Column(name="contact_flag", jdbcType="INTEGER")
    private Integer contactFlag;

    private static final long serialVersionUID = -962536286L;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getVehicleModelId() {
        return vehicleModelId;
    }

    public void setVehicleModelId(Integer vehicleModelId) {
        this.vehicleModelId = vehicleModelId;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public Integer getTripNum() {
        return tripNum;
    }

    public void setTripNum(Integer tripNum) {
        this.tripNum = tripNum;
    }

    public String getDemand() {
        return demand;
    }

    public void setDemand(String demand) {
        this.demand = demand == null ? null : demand.trim();
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode == null ? null : waybillCode.trim();
    }

    public String getWaybillImg() {
        return waybillImg;
    }

    public void setWaybillImg(String waybillImg) {
        this.waybillImg = waybillImg == null ? null : waybillImg.trim();
    }

    public Date getBookTime() {
        return bookTime;
    }

    public void setBookTime(Date bookTime) {
        this.bookTime = bookTime;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getCreateOrg() {
        return createOrg;
    }

    public void setCreateOrg(Integer createOrg) {
        this.createOrg = createOrg;
    }

    public Integer getUpdateOrg() {
        return updateOrg;
    }

    public void setUpdateOrg(Integer updateOrg) {
        this.updateOrg = updateOrg;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getContactFlag() {
        return contactFlag;
    }

    public void setContactFlag(Integer contactFlag) {
        this.contactFlag = contactFlag;
    }
}