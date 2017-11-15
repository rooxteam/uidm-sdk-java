package com.rooxteam.sso.aal.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpFlowStateJson {
    private Form form;
    private View view;
    private String serverUrl;
    private String execution;
}
