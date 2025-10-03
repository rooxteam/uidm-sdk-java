package com.rooxteam.sso.aal.opa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rooxteam.sso.aal.client.model.EvaluationResponse;
import lombok.Data;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OpaPolicyResponse {

    EvaluationResponse result;

    @JsonProperty("decision_id")
    UUID decisionId;

}
