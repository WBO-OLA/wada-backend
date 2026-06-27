package com.wada.ola.personnel.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.dto.CommandRequest;
import com.wada.ola.personnel.entity.Command;
import com.wada.ola.personnel.repository.CommandRepository;
import com.wada.ola.personnel.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommandService {

    private final CommandRepository commandRepository;
    private final MemberRepository memberRepository;

    public CommandService(CommandRepository commandRepository, MemberRepository memberRepository) {
        this.commandRepository = commandRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<Command> findAll() {
        return commandRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Command findById(Long id) {
        return commandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Command", id));
    }

    @Transactional(readOnly = true)
    public List<Command> findRoots() {
        return commandRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Command> findChildren(Long parentId) {
        findById(parentId);
        return commandRepository.findByParentId(parentId);
    }

    /** Returns IDs of the given command and all its descendants (recursive BFS). */
    public List<Long> getAllDescendantIds(Long rootId) {
        findById(rootId); // validate existence
        List<Long> collected = new ArrayList<>();
        collected.add(rootId);
        List<Long> frontier = new ArrayList<>();
        frontier.add(rootId);
        while (!frontier.isEmpty()) {
            List<Command> children = commandRepository.findByParentIdIn(frontier);
            frontier.clear();
            for (Command child : children) {
                collected.add(child.getId());
                frontier.add(child.getId());
            }
        }
        return collected;
    }

    public Command create(CommandRequest request) {
        Command command = new Command();
        applyRequest(command, request);
        command.setParent(resolveParent(null, request.getParentId()));
        return commandRepository.save(command);
    }

    public Command update(Long id, CommandRequest request) {
        Command command = findById(id);
        applyRequest(command, request);
        command.setParent(resolveParent(id, request.getParentId()));
        return commandRepository.save(command);
    }

    public void delete(Long id) {
        if (!commandRepository.findByParentId(id).isEmpty()) {
            throw new IllegalArgumentException("Cannot delete a command that has child commands");
        }
        if (!memberRepository.findByCommandId(id).isEmpty()) {
            throw new IllegalArgumentException("Cannot delete a command that has members assigned");
        }
        commandRepository.deleteById(id);
    }

    private Command resolveParent(Long selfId, Long parentId) {
        if (parentId == null) {
            return null;
        }
        Command parent = findById(parentId);
        if (selfId != null) {
            Command ancestor = parent;
            while (ancestor != null) {
                if (ancestor.getId().equals(selfId)) {
                    throw new IllegalArgumentException("A command cannot be its own ancestor");
                }
                ancestor = ancestor.getParent();
            }
        }
        return parent;
    }

    private void applyRequest(Command command, CommandRequest request) {
        command.setName(request.getName());
        command.setDescription(request.getDescription());
        command.setType(request.getType());
    }
}
