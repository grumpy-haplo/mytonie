package de.haplo.mytonie.model.actions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Action {

	private String tonieId;
	private String audioId;
	private ActionType type;

	@Override
	public String toString() {
		if (ActionType.ADD.equals(type)) {
			return "ADD " + tonieId + " with audio " + audioId;
		} else {
			return "REMOVE Tonie " + tonieId;
		}
	}

}
