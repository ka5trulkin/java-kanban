package ru.yandex.practicum.taskTracker.http;

import ru.yandex.practicum.taskTracker.model.Type;

public enum Endpoint {
    GET_PRIORITIZES_TASKS,
    GET_HISTORY,
    GET_SUBTASKS_FROM_EPIC,
    GET_ALL_TASKS,
    GET_TASK_BY_ID,
    POST_TASK,
    DELETE_ALL_TASKS,
    DELETE_TASK_BY_ID,
    GET_ALL_EPICS,
    GET_EPIC_BY_ID,
    POST_EPIC,
    DELETE_ALL_EPICS,
    DELETE_EPIC_BY_ID,
    GET_ALL_SUBTASKS,
    GET_SUBTASK_BY_ID,
    POST_SUBTASK,
    DELETE_ALL_SUBTASKS,
    DELETE_SUBTASK_BY_ID,
    UNKNOWN;

    public static Endpoint post(Type taskType) {
        switch (taskType) {
            case TASK:
                return POST_TASK;
            case EPIC:
                return POST_EPIC;
            case SUBTASK:
                return POST_SUBTASK;
            default:
                return UNKNOWN;
        }
    }

    public static Endpoint getAll(Type taskType) {
        switch (taskType) {
            case TASK:
                return GET_ALL_TASKS;
            case EPIC:
                return GET_ALL_EPICS;
            case SUBTASK:
                return GET_ALL_SUBTASKS;
            default:
                return UNKNOWN;
        }
    }

    public static Endpoint getById(Type taskType) {
        switch (taskType) {
            case TASK:
                return GET_TASK_BY_ID;
            case EPIC:
                return GET_EPIC_BY_ID;
            case SUBTASK:
                return GET_SUBTASK_BY_ID;
            default:
                return UNKNOWN;
        }
    }

    public static Endpoint deleteAll(Type taskType) {
        switch (taskType) {
            case TASK:
                return DELETE_ALL_TASKS;
            case EPIC:
                return DELETE_ALL_EPICS;
            case SUBTASK:
                return DELETE_ALL_SUBTASKS;
            default:
                return UNKNOWN;
        }
    }

    public static Endpoint deleteById(Type taskType) {
        switch (taskType) {
            case TASK:
                return DELETE_TASK_BY_ID;
            case EPIC:
                return DELETE_EPIC_BY_ID;
            case SUBTASK:
                return DELETE_SUBTASK_BY_ID;
            default:
                return UNKNOWN;
        }
    }
}