package de.garten.training.depotflow.data.db.green

import de.garten.training.depotflow.data.db.converter.StopTypeConverter
import de.garten.training.depotflow.domain.StopType
import org.greenrobot.greendao.annotation.Convert
import org.greenrobot.greendao.annotation.Entity
import org.greenrobot.greendao.annotation.Id
import org.greenrobot.greendao.annotation.Property

@Entity(nameInDb = "STOP")
class Stop {
    @JvmField
    @Id
    var id: Long? = null

    @JvmField
    @Property(nameInDb = "WORK_ORDER_ID")
    var workOrderId: Long = 0

    @JvmField
    @Property(nameInDb = "REMOTE_ID")
    var remoteId: String? = null

    @JvmField
    @Property(nameInDb = "SEQUENCE_NO")
    var sequence: Int = 0

    @JvmField
    @Convert(converter = StopTypeConverter::class, columnType = String::class)
    @Property(nameInDb = "TYPE")
    var type: StopType? = null

    @JvmField
    @Property(nameInDb = "NAME")
    var name: String? = null

    @JvmField
    @Property(nameInDb = "ADDRESS")
    var address: String? = null

    @JvmField
    @Property(nameInDb = "LATITUDE")
    var latitude: Double = 0.0

    @JvmField
    @Property(nameInDb = "LONGITUDE")
    var longitude: Double = 0.0

    @JvmField
    @Property(nameInDb = "STATUS")
    var status: String? = null

    @JvmField
    @Property(nameInDb = "ARRIVAL_WINDOW_FROM")
    var arrivalWindowFrom: String? = null

    @JvmField
    @Property(nameInDb = "ARRIVAL_WINDOW_TO")
    var arrivalWindowTo: String? = null
}
