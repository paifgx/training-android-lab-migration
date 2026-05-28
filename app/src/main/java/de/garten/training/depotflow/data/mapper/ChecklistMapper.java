package de.garten.training.depotflow.data.mapper;

import de.garten.training.depotflow.data.api.dto.ChecklistItemDto;
import de.garten.training.depotflow.data.db.green.ChecklistItem;

public class ChecklistMapper {

    public ChecklistItem fromDto(ChecklistItemDto dto) {
        ChecklistItem item = new ChecklistItem();
        item.setRemoteId(dto.id);
        item.setLabel(dto.label);
        item.setChecked(dto.checked);
        item.setMandatory(dto.mandatory);
        item.setNote(dto.note);
        return item;
    }
}
