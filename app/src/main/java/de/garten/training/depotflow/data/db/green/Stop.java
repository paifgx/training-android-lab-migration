package de.garten.training.depotflow.data.db.green;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import de.garten.training.depotflow.data.db.converter.StopTypeConverter;
import de.garten.training.depotflow.domain.StopType;

@Entity(nameInDb = "STOP")
public class Stop {
    @Id
    private Long id;

    @Property(nameInDb = "WORK_ORDER_ID")
    private long workOrderId;

    @Property(nameInDb = "REMOTE_ID")
    private String remoteId;

    @Property(nameInDb = "SEQUENCE_NO")
    private int sequence;

    @Convert(converter = StopTypeConverter.class, columnType = String.class)
    @Property(nameInDb = "TYPE")
    private StopType type;

    @Property(nameInDb = "NAME")
    private String name;

    @Property(nameInDb = "ADDRESS")
    private String address;

    @Property(nameInDb = "LATITUDE")
    private double latitude;

    @Property(nameInDb = "LONGITUDE")
    private double longitude;

    @Property(nameInDb = "STATUS")
    private String status;

    @Property(nameInDb = "ARRIVAL_WINDOW_FROM")
    private String arrivalWindowFrom;

    @Property(nameInDb = "ARRIVAL_WINDOW_TO")
    private String arrivalWindowTo;

    public Stop() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public StopType getType() {
        return type;
    }

    public void setType(StopType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getArrivalWindowFrom() {
        return arrivalWindowFrom;
    }

    public void setArrivalWindowFrom(String arrivalWindowFrom) {
        this.arrivalWindowFrom = arrivalWindowFrom;
    }

    public String getArrivalWindowTo() {
        return arrivalWindowTo;
    }

    public void setArrivalWindowTo(String arrivalWindowTo) {
        this.arrivalWindowTo = arrivalWindowTo;
    }
}
