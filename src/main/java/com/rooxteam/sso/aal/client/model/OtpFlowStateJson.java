package com.rooxteam.sso.aal.client.model;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpFlowStateJson {
    private Form form;
    private View view;
    private String serverUrl;
    private String execution;
}
