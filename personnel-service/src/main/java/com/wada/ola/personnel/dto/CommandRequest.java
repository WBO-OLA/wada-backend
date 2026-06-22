package com.wada.ola.personnel.dto;

import com.wada.ola.personnel.entity.Command;

public class CommandRequest {

    private String name;
    private Command.CommandType type;
    private Long parentId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Command.CommandType getType() { return type; }
    public void setType(Command.CommandType type) { this.type = type; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
