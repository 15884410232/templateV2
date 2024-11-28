package com.levy.collection.flow.go.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class ModuleInfo implements Serializable {
    private String path;
    private String version;
    private ZonedDateTime timestamp;
}