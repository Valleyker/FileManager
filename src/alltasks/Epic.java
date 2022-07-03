package alltasks;

import info.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskListId;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description, LocalDateTime startTime, long duration) {
        super(id, name, description, startTime, duration);
        subTaskListId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskListId() {
        return subTaskListId;
    }

    public void setSubTaskListId(ArrayList<Integer> subTaskListId) {
        this.subTaskListId = subTaskListId;
    }

    @Override
    public Type getTaskType() {
        return Type.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskListId, epic.subTaskListId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() + ", " +
                "subTaskListId=" + subTaskListId + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getInfo() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
