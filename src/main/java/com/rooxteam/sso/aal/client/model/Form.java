package com.rooxteam.sso.aal.client.model;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Form {
    private Map<String, Object> fields = new HashMap<>();
    private List<ResponseError> errors = new ArrayList<>();
    private String name;
}
