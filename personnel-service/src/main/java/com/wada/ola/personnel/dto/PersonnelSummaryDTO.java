package com.wada.ola.personnel.dto;

import java.util.Map;

public class PersonnelSummaryDTO {

    private long totalMembers;
    private long active;
    private long injured;
    private long retired;
    private long passedAway;
    private Map<String, Long> byRank;
    private Map<String, Long> byUnit;

    public long getTotalMembers() { return totalMembers; }
    public void setTotalMembers(long totalMembers) { this.totalMembers = totalMembers; }

    public long getActive() { return active; }
    public void setActive(long active) { this.active = active; }

    public long getInjured() { return injured; }
    public void setInjured(long injured) { this.injured = injured; }

    public long getRetired() { return retired; }
    public void setRetired(long retired) { this.retired = retired; }

    public long getPassedAway() { return passedAway; }
    public void setPassedAway(long passedAway) { this.passedAway = passedAway; }

    public Map<String, Long> getByRank() { return byRank; }
    public void setByRank(Map<String, Long> byRank) { this.byRank = byRank; }

    public Map<String, Long> getByUnit() { return byUnit; }
    public void setByUnit(Map<String, Long> byUnit) { this.byUnit = byUnit; }
}
