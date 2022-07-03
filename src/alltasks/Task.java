package alltasks;

import info.Status;
import info.Type;

import java.time.LocalDateTime;

public class Task {
    private Integer id;
    private String name;
    private String info;
    private Status status = Status.NEW;
    private LocalDateTime startTime;
    private long duration;

    public Task(Integer id, String name, String description, LocalDateTime startTime, long duration) {
        this.id = id;
        this.name = name;
        this.info = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime = null;
        if (this.startTime != null) {
            endTime = startTime.plusMinutes(duration);
        }
        return endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public Type getTaskType() {
        return Type.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id) && name.equals(task.name) && info.equals(task.info)
                && status.equals(task.status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + info + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }

}
