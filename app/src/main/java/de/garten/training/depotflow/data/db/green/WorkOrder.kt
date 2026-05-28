package de.garten.training.depotflow.data.db.green

import de.garten.training.depotflow.data.db.converter.SyncStatusConverter
import de.garten.training.depotflow.data.db.converter.WorkOrderStatusConverter
import de.garten.training.depotflow.domain.SyncStatus
import de.garten.training.depotflow.domain.WorkOrderStatus
import org.greenrobot.greendao.annotation.Convert
import org.greenrobot.greendao.annotation.Entity
import org.greenrobot.greendao.annotation.Id
import org.greenrobot.greendao.annotation.Property
import org.greenrobot.greendao.annotation.Unique

@Entity(nameInDb = "WORK_ORDER")
class WorkOrder {
    @JvmField
    @Id
    var id: Long? = null

    @JvmField
    @Unique
    @Property(nameInDb = "SERVER_ID")
    var serverId: String? = null

    @JvmField
    @Property(nameInDb = "EXTERNAL_NUMBER")
    var externalNumber: String? = null

    @JvmField
    @Property(nameInDb = "TITLE")
    var title: String? = null

    @JvmField
    @Property(nameInDb = "CUSTOMER_NAME")
    var customerName: String? = null

    @JvmField
    @Convert(converter = WorkOrderStatusConverter::class, columnType = String::class)
    @Property(nameInDb = "STATUS")
    var status: WorkOrderStatus? = null

    @JvmField
    @Property(nameInDb = "PRIORITY")
    var priority: Int = 0

    @JvmField
    @Property(nameInDb = "DUE_AT")
    var dueAt: String? = null

    @JvmField
    @Property(nameInDb = "UPDATED_AT")
    var updatedAt: String? = null

    @JvmField
    @Property(nameInDb = "ASSIGNED_USER")
    var assignedUser: String? = null

    @JvmField
    @Convert(converter = SyncStatusConverter::class, columnType = String::class)
    @Property(nameInDb = "SYNC_STATUS")
    var syncStatus: SyncStatus? = null

    @JvmField
    @Property(nameInDb = "LAST_ERROR")
    var lastError: String? = null

    @Property(nameInDb = "DIRTY")
    var isDirty: Boolean = false
}
