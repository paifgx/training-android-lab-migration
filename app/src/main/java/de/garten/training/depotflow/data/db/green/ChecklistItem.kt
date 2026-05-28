package de.garten.training.depotflow.data.db.green

import org.greenrobot.greendao.annotation.Entity
import org.greenrobot.greendao.annotation.Id
import org.greenrobot.greendao.annotation.Property

@Entity(nameInDb = "CHECKLIST_ITEM")
class ChecklistItem {
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
    @Property(nameInDb = "LABEL")
    var label: String? = null

    @Property(nameInDb = "IS_CHECKED")
    var isChecked: Boolean = false

    @Property(nameInDb = "IS_MANDATORY")
    var isMandatory: Boolean = false

    @JvmField
    @Property(nameInDb = "NOTE")
    var note: String? = null
}
