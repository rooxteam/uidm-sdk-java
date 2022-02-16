package com.rooxteam.sso.aal.opa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.rooxteam.sso.aal.client.EvaluationContext;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value
public class OpaInput {

    EvaluationContext evaluationContext;

    OpaAuthorization authorization;

    @JsonRawValue
    Object dataToPostprocess;

}
