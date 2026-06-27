package com.wada.ola.personnel.service;

import com.wada.ola.common.exception.ResourceNotFoundException;
import com.wada.ola.personnel.dto.CommandRequest;
import com.wada.ola.personnel.entity.Command;
import com.wada.ola.personnel.entity.Member;
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
        // Commander optional on create — no members exist in a brand-new command yet
        if (request.getCommanderId() != null) {
            Member commander = resolveCommander(request.getCommanderId());
            validateCommander(commander, null, null);
            command.setCommander(commander);
        }
        return commandRepository.save(command);
    }

    public Command update(Long id, CommandRequest request) {
        if (request.getCommanderId() == null) {
            throw new IllegalArgumentException("Commander is required");
        }
        Command command = findById(id);
        Member commander = resolveCommander(request.getCommanderId());
        validateCommander(commander, id, command.getCommander());
        applyRequest(command, request);
        command.setParent(resolveParent(id, request.getParentId()));
        command.setCommander(commander);
        return commandRepository.save(command);
    }

    @Transactional
    public Command assignCommander(Long commandId, Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("Commander is required");
        }
        Command command = findById(commandId);
        Member commander = resolveCommander(memberId);
        validateCommander(commander, commandId, command.getCommander());
        command.setCommander(commander);
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

    private Member resolveCommander(Long memberId) {
        if (memberId == null) return null;
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", memberId));
    }

    /**
     * Validates that:
     * 1. The member belongs to the target command (same-command rule).
     * 2. The member is not already the commander of a different command (one-command rule).
     *
     * @param commander  the member being assigned
     * @param commandId  the ID of the command being edited (null on create)
     * @param current    the command's existing commander (null if none)
     */
    private void validateCommander(Member commander, Long commandId, Member current) {
        // Rule 1: member must belong to this command
        if (commandId != null) {
            Long memberCommandId = commander.getCommand() != null ? commander.getCommand().getId() : null;
            if (!commandId.equals(memberCommandId)) {
                throw new IllegalArgumentException(
                        commander.getFirstName() + " " + commander.getLastName() +
                        " does not belong to this command and cannot be assigned as its commander.");
            }
        }

        // Rule 2: member must not already command a different command
        commandRepository.findByCommanderId(commander.getId()).ifPresent(existing -> {
            boolean isSameCommand = commandId != null && existing.getId().equals(commandId);
            boolean isSamePerson = current != null && current.getId().equals(commander.getId());
            if (!isSameCommand && !isSamePerson) {
                throw new IllegalArgumentException(
                        commander.getFirstName() + " " + commander.getLastName() +
                        " is already the commander of " + existing.getName() + ".");
            }
        });
    }

    private void applyRequest(Command command, CommandRequest request) {
        command.setName(request.getName());
        command.setDescription(request.getDescription());
        command.setType(request.getType());
    }
}
