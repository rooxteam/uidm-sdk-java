package com.rooxteam.otp.m2m.configuration;

import com.rooxteam.uidm.sdk.spring.configuration.OtpPerOperationApiConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @deprecated Import OtpPerOperationApiConfiguration directly
 */
@Deprecated
@Import(OtpPerOperationApiConfiguration.class)
public class M2MOtpPlugin {
}
