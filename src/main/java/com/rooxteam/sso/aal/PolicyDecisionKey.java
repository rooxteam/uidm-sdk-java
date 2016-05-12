package com.rooxteam.sso.aal;

import lombok.Getter;

class PolicyDecisionKey implements AalCacheKey {
    @Getter
    final Principal subject;
    final String resourceName;
    final String actionName;

    public PolicyDecisionKey(Principal subject, String resourceName, String actionName) {
        this.subject = subject;
        this.resourceName = resourceName;
        this.actionName = actionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PolicyDecisionKey that = (PolicyDecisionKey) o;

        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null)
            return false;
        return !(actionName != null ? !actionName.equals(that.actionName) : that.actionName != null);
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0);
        result = 31 * result + (actionName != null ? actionName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PolicyDecisionKey{" +
                "subject=" + subject +
                ", resourceName='" + resourceName + '\'' +
                ", actionName='" + actionName + '\'' +
                '}';
    }
}
