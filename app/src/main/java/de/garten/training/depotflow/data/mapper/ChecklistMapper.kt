package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.ChecklistItemDto
import de.garten.training.depotflow.data.db.green.ChecklistItem

class ChecklistMapper {
    fun fromDto(dto: ChecklistItemDto): ChecklistItem {
        return ChecklistItem().apply {
            remoteId = dto.id
            label = dto.label
            isChecked = dto.checked
            isMandatory = dto.mandatory
            note = dto.note
        }
    }
}
