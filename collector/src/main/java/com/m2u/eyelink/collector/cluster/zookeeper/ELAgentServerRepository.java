package com.m2u.eyelink.collector.cluster.zookeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.rpc.util.MapUtils;

public class ELAgentServerRepository {

	private final Map<String, Set<ELAgentServer>> pinpointServerRepository = new HashMap<>();

	public boolean addAndIsKeyCreated(String key, ELAgentServer pinpointServer) {
		synchronized (this) {
			boolean isContains = pinpointServerRepository.containsKey(key);
			if (isContains) {
				Set<ELAgentServer> pinpointServerSet = pinpointServerRepository.get(key);
				pinpointServerSet.add(pinpointServer);

				return false;
			} else {
				Set<ELAgentServer> pinpointServerSet = new HashSet<>();
				pinpointServerSet.add(pinpointServer);

				pinpointServerRepository.put(key, pinpointServerSet);
				return true;
			}
		}
	}

	public boolean removeAndGetIsKeyRemoved(String key, ELAgentServer pinpointServer) {
		synchronized (this) {
			boolean isContains = pinpointServerRepository.containsKey(key);
			if (isContains) {
				Set<ELAgentServer> pinpointServerSet = pinpointServerRepository.get(key);
				pinpointServerSet.remove(pinpointServer);

				if (pinpointServerSet.isEmpty()) {
					pinpointServerRepository.remove(key);
					return true;
				}
			}
			return false;
		}
	}

	public void clear() {
		synchronized (this) {
			pinpointServerRepository.clear();
		}
	}

	public List<ELAgentServer> getValues() {
		List<ELAgentServer> pinpointServerList = new ArrayList<>(pinpointServerRepository.size());

		for (Set<ELAgentServer> eachKeysValue : pinpointServerRepository.values()) {
			pinpointServerList.addAll(eachKeysValue);
		}

		return pinpointServerList;
	}

	private String getKey(ELAgentServer pinpointServer) {
		Map<Object, Object> properties = pinpointServer.getChannelProperties();
		final String applicationName = MapUtils.getString(properties, HandshakePropertyType.APPLICATION_NAME.getName());
		final String agentId = MapUtils.getString(properties, HandshakePropertyType.AGENT_ID.getName());
		final Long startTimeStamp = MapUtils.getLong(properties, HandshakePropertyType.START_TIMESTAMP.getName());

		if (StringUtils.isBlank(applicationName) || StringUtils.isBlank(agentId) || startTimeStamp == null
				|| startTimeStamp <= 0) {
			return StringUtils.EMPTY;
		}

		return applicationName + ":" + agentId + ":" + startTimeStamp;
	}

}
