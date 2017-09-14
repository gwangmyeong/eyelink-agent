package com.m2u.eyelink.collector.alarm;

import static com.m2u.eyelink.collector.alarm.AgentAlarmTypeCategory.MEMORY;
import static com.m2u.eyelink.collector.alarm.AgentAlarmTypeCategory.CPU;
import static com.m2u.eyelink.collector.alarm.AgentAlarmTypeCategory.SERVICE;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.m2u.eyelink.collector.util.AgentEventTypeCategory;

public enum AgentAlarmType {
	// FIXME set value using config file
	MEMORY_70(30, "Memory used 70% over", true, MEMORY),
	MEMORY_80(80, "Memory used 80% over", true, MEMORY),
	MEMORY_90(90, "Memory used 90% over", true, MEMORY),
    MEMORY_100(100, "Memory used 100%", true, MEMORY),
	CPU_70(30, "CPU used 70% over", true, CPU),
	CPU_80(80, "CPU used 80% over", true, CPU),
	CPU_90(90, "CPU used 90% over", true, CPU),
	CPU_100(100, "CPU used 100%", true, CPU);
	
	private final int code;
	private final String desc;
	private final Set<AgentAlarmTypeCategory> category;
	private final boolean isUse;
	
	AgentAlarmType(int code, String desc, boolean isUse, AgentAlarmTypeCategory... category) {
		this.code = code;
		this.desc = desc;
		this.isUse = isUse;
		if (category == null || category.length == 0) {
			this.category = Collections.emptySet();
		} else {
			this.category = new HashSet<AgentAlarmTypeCategory>(Arrays.asList(category));
		}
	}

	public int getCode() {
		return this.code;
	}

	public String getDesc() {
		return desc;
	}

	public boolean isCategorizedAs(AgentEventTypeCategory category) {
		return this.category.contains(category);
	}

	@Override
	public String toString() {
		return desc;
	}

	public static AgentAlarmType getAlarmTypeByCode(int code) {
		for (AgentAlarmType alarmType : AgentAlarmType.values()) {
			if (alarmType.code == code) {
				return alarmType;
			}
		}
		return null;
	}

	public static Set<AgentAlarmType> getAlarmTypesByCatgory(AgentAlarmTypeCategory category) {
		Set<AgentAlarmType> eventTypes = new HashSet<AgentAlarmType>();
		for (AgentAlarmType eventType : AgentAlarmType.values()) {
			if (eventType.category.contains(category)) {
				eventTypes.add(eventType);
			}
		}
		return eventTypes;
	}

	public boolean getIsUse() {
		return isUse;
	}
}
