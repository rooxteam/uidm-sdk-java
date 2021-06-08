package com.rooxteam.sso.aal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PrincipalTest {

    /**
     * {
     * "sub": "25011010001448",
     * "ver": "1.0",
     * "tokenName": "id_token",
     * "iss": "PcrfAuthenticationService",
     * "imsi": "25011010001448",
     * "aud": [ "webapi" ],
     * "ath": 1438703273,
     * "azp": "webapi",
     * "realm": "/customer",
     * "ren": 1438703333,
     * "msisdn": "25011010001448",
     * "exp": 1438703333,
     * "tokenType": "JWTToken",
     * "iat": 1438703273,
     * "authLevel": [ "2" ], "jti": "3e262138-e678-4406-80c7-83b472fbbe79"
     * }
     */
    private static final String TOKEN = "eyAiYWxnIjogIkhTMjU2IiwgImN0eSI6ICJKV1QiLCAidHlwIjogImp3dCIgfQ.eyAidG9rZW5OYW1l" +
            "IjogImlkX3Rva2VuIiwgImF6cCI6ICJ3ZWJhcGkiLCAic3ViIjogIjI1MDExMDEwMDAxNDQ4IiwgIm1zaXNkbiI6ICIyNTAxMTAxMDAwMTQ" +
            "0OCIsICJpc3MiOiAiUGNyZkF1dGhlbnRpY2F0aW9uU2VydmljZSIsICJ2ZXIiOiAiMS4wIiwgImlhdCI6IDE0Mzg3MDMyNzMsICJleHAiOi" +
            "AxNDM4NzAzMzMzLCAidG9rZW5UeXBlIjogIkpXVFRva2VuIiwgInJlYWxtIjogIi9jdXN0b21lciIsICJhdXRoTGV2ZWwiOiBbICIyIiBdL" +
            "CAiYXVkIjogWyAid2ViYXBpIiBdLCAicmVuIjogMTQzODcwMzMzMywgImp0aSI6ICIzZTI2MjEzOC1lNjc4LTQ0MDYtODBjNy04M2I0NzJm" +
            "YmJlNzkiLCAiaW1zaSI6ICIyNTAxMTAxMDAwMTQ0OCIsICJhdGgiOiAxNDM4NzAzMjczIH0.pMqdpoGQisd2EPmF3duzftWG0v6U_LtM5qXV186-5xM";





}
