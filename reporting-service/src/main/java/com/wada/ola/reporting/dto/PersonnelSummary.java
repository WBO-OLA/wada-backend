package com.wada.ola.reporting.dto;

import java.util.Map;

public class PersonnelSummary {
    private long totalMembers;
    private long active;
    private long newJoiners;
    private long injured;
    private long retired;
    private long passedAway;
    private Map<String, Long> byRank;
    private Map<String, Long> byUnit;

    public long getTotalMembers() { return totalMembers; }
    public void setTotalMembers(long v) { this.totalMembers = v; }
    public long getActive() { return active; }
    public void setActive(long v) { this.active = v; }
    public long getNewJoiners() { return newJoiners; }
    public void setNewJoiners(long v) { this.newJoiners = v; }
    public long getInjured() { return injured; }
    public void setInjured(long v) { this.injured = v; }
    public long getRetired() { return retired; }
    public void setRetired(long v) { this.retired = v; }
    public long getPassedAway() { return passedAway; }
    public void setPassedAway(long v) { this.passedAway = v; }
    public Map<String, Long> getByRank() { return byRank; }
    public void setByRank(Map<String, Long> v) { this.byRank = v; }
    public Map<String, Long> getByUnit() { return byUnit; }
    public void setByUnit(Map<String, Long> v) { this.byUnit = v; }
}
