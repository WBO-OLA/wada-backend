package com.wada.ola.reporting.dto;

import java.util.Map;

public class PersonnelSummary {
    private long totalMembers;
    private long active;
    private long injured;
    private long retired;
    private long passedAway;
    private Map<String, Long> byRank;
    private Map<String, Long> byCommand;

    public long getTotalMembers() { return totalMembers; }
    public void setTotalMembers(long v) { this.totalMembers = v; }
    public long getActive() { return active; }
    public void setActive(long v) { this.active = v; }
    public long getInjured() { return injured; }
    public void setInjured(long v) { this.injured = v; }
    public long getRetired() { return retired; }
    public void setRetired(long v) { this.retired = v; }
    public long getPassedAway() { return passedAway; }
    public void setPassedAway(long v) { this.passedAway = v; }
    public Map<String, Long> getByRank() { return byRank; }
    public void setByRank(Map<String, Long> v) { this.byRank = v; }
    public Map<String, Long> getByCommand() { return byCommand; }
    public void setByCommand(Map<String, Long> v) { this.byCommand = v; }
}
