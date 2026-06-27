package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.Command;

public class CommandRequest {

    private String name;
    private String description;
    private Command.CommandType type;
    private Long parentId;
    private Long commanderId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Command.CommandType getType() { return type; }
    public void setType(Command.CommandType type) { this.type = type; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getCommanderId() { return commanderId; }
    public void setCommanderId(Long commanderId) { this.commanderId = commanderId; }
}
