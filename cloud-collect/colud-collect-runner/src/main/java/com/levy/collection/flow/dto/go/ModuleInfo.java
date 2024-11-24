package com.levy.collection.flow.dto.go;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class ModuleInfo implements Serializable {
    private String path;
    private String version;
    private ZonedDateTime timestamp;
}