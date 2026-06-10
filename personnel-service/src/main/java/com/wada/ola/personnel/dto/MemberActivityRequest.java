package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.MemberActivity;
import java.time.LocalDate;

public class MemberActivityRequest {

    private String title;
    private String description;
    private LocalDate activityDate;
    private MemberActivity.ActivityType type;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }

    public MemberActivity.ActivityType getType() { return type; }
    public void setType(MemberActivity.ActivityType type) { this.type = type; }
}
