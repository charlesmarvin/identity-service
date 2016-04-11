package io.mmc.domain;

import org.springframework.data.annotation.Id;

/**
 * Created by charlesmarvin on 4/8/16.
 */
public class Identity {
    @Id
    private String id;
    private String principal;
    private State state;

    public Identity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Identity{" +
                "id='" + id + '\'' +
                ", principal='" + principal + '\'' +
                ", state=" + state +
                '}';
    }

    public enum State {Active, Locked}
}
