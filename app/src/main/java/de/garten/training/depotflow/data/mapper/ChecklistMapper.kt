package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.ChecklistItemDto
import de.garten.training.depotflow.data.db.green.ChecklistItem

class ChecklistMapper {
    fun fromDto(dto: ChecklistItemDto): ChecklistItem {
        val item = ChecklistItem()
        item.setRemoteId(dto.id)
        item.setLabel(dto.label)
        item.setChecked(dto.checked)
        item.setMandatory(dto.mandatory)
        item.setNote(dto.note)
        return item
    }
}
