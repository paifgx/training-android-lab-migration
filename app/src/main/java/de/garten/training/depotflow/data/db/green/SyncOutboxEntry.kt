package de.garten.training.depotflow.data.db.green

import org.greenrobot.greendao.annotation.Entity
import org.greenrobot.greendao.annotation.Id
import org.greenrobot.greendao.annotation.Property

@Entity(nameInDb = "SYNC_OUTBOX")
class SyncOutboxEntry {
    @JvmField
    @Id
    var id: Long? = null

    @JvmField
    @Property(nameInDb = "AGGREGATE_TYPE")
    var aggregateType: String? = null

    @JvmField
    @Property(nameInDb = "AGGREGATE_ID")
    var aggregateId: String? = null

    @JvmField
    @Property(nameInDb = "OPERATION")
    var operation: String? = null

    @JvmField
    @Property(nameInDb = "PAYLOAD")
    var payload: String? = null

    @JvmField
    @Property(nameInDb = "CREATED_AT")
    var createdAt: String? = null

    @JvmField
    @Property(nameInDb = "ATTEMPTS")
    var attempts: Int = 0

    @JvmField
    @Property(nameInDb = "LAST_ERROR")
    var lastError: String? = null
}
