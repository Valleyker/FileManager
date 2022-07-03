package alltasks;

import info.Type;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicTaskId;

    public Subtask(int id, String name, String description, LocalDateTime startTime, long duration) {
        super(id, name, description, startTime, duration);
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public Type getTaskType() {
        return Type.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicTaskId == subtask.epicTaskId;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() + ", " +
                "epicTaskId=" + epicTaskId + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getInfo() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
