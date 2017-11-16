package com.example;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class WebhookManager {

	public static class WebhookInfo {
		private final Date date;
		private final String entityName;
		private final Map<String, Object> entity;

		public WebhookInfo(Date date, String entityName, Map<String, Object> entity) {
			this.date = date;
			this.entityName = entityName;
			this.entity = entity;
		}

		public Date getDate() {
			return date;
		}

		public String getEntityName() {
			return entityName;
		}

		public Map<String, Object> getEntity() {
			return entity;
		}
	}

	private final LoadingCache<String, CircularFifoQueue<WebhookInfo>> webhooks = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, CircularFifoQueue<WebhookInfo>>() {
		public CircularFifoQueue<WebhookInfo> load(String key) {
			return new CircularFifoQueue<>(50);
		}
	});

	public void add(String groupId, WebhookInfo webhook) {
		webhooks.getUnchecked(groupId).add(webhook);
	}

	public Collection<WebhookInfo> get(String groupId) {
		return webhooks.getUnchecked(groupId);
	}
}
