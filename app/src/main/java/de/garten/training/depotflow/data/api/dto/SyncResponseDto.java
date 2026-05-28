package de.garten.training.depotflow.data.api.dto;

import java.util.List;

public class SyncResponseDto {
    public boolean success;
    public List<String> acceptedAggregateIds;
    public List<String> rejectedAggregateIds;
    public String serverTime;

    public SyncResponseDto() {
    }
}
