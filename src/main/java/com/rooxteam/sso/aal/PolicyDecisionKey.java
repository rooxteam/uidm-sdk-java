package com.rooxteam.sso.aal;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
class PolicyDecisionKey implements AalCacheKey {
    private final Principal subject;
    private final String resourceName;
    private final String actionName;
    private final Map<String, ?> envParameters;

    public PolicyDecisionKey(Principal subject, String resourceName, String actionName) {
        this.subject = subject;
        this.resourceName = resourceName;
        this.actionName = actionName;
        this.envParameters = new HashMap<>();
    }

    public PolicyDecisionKey(Principal subject, String resourceName, String actionName, Map<String, ?> envParameters) {
        this.subject = subject;
        this.resourceName = resourceName;
        this.actionName = actionName;
        this.envParameters = envParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PolicyDecisionKey that = (PolicyDecisionKey) o;

        if (subject != null ? !subject.equals(that.getSubject()) : that.getSubject() != null) {
            return false;
        }

        if (resourceName != null ? !resourceName.equals(that.getResourceName()) : that.getResourceName() != null) {
            return false;
        }

        if (!envParameters.isEmpty() ? !envParameters.equals(that.getEnvParameters()) : that.getEnvParameters() != null) {
            return false;
        }


        return !(actionName != null ? !actionName.equals(that.getActionName()) : that.getActionName() != null);
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0);
        result = 31 * result + (actionName != null ? actionName.hashCode() : 0);
        result = 31 * result + (envParameters != null ? envParameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {


        return "PolicyDecisionKey{" +
                "subject=" + subject +
                ", resourceName='" + resourceName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", envParameters='" + envParameters.toString() + '\'' +
                '}';
    }
}
